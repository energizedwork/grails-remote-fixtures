package grails.plugin.remotefixtures

import grails.plugin.fixtures.FixtureLoader
import grails.plugin.fixtures.exception.UnknownFixtureException
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.beans.factory.BeanCreationException
import grails.converters.*
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN
import org.springframework.transaction.support.*
import static org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes.*

class FixtureController {

	FixtureLoader fixtureLoader
	def grailsApplication
	def transactionManager

	def beforeInterceptor = {
		if (!grailsApplication.config.fixtures.enabled) {
			response.sendError SC_FORBIDDEN
		}
		if (!(request.format in ["html", "json", "xml"])) {
			log.debug "unsupported output format '$request.format', using 'html' instead"
			request.setAttribute(CONTENT_FORMAT, "html")
		}
	}

	def loadNamed = {
		try {
			def fixtureData = loadNamedFixture(params.fixture)
			withFormat {
				html { render view: "success", model: [fixture: fixtureData] }
				json { render fixtureData as JSON }
				xml { render fixtureData as XML }
			}
		} catch (UnknownFixtureException e) {
			log.error "fixture $params.fixture not found"
			render view: "notfound", model: [message: e.message]
		} catch (BeanCreationException e) {
			log.error "could not load fixture $params.fixture, caught: $e"
			render view: "error", model: [exception: e.cause]
		}
	}

	def load = {
		try {
			def fixtureData = loadFixtureScript(params.fixture)
			withFormat {
				html { render view: "success", model: [fixture: fixtureData] }
				json { render fixtureData as JSON }
				xml { render fixtureData as XML }
			}
		} catch (BeanCreationException e) {
			log.error "could not load fixture script, caught: $e"
			render view: "error", model: [exception: e.cause]
		}
	}

	private Map loadNamedFixture(String name) {
		def fixture
		new TransactionTemplate(transactionManager).execute({ status ->
			log.info "loading fixture $name..."
			fixture = fixtureLoader.load(name)
		} as TransactionCallback)
		def names = fixture.applicationContext.beanDefinitionNames - ["fixtureBeanPostProcessor", "autoAutoWirer"]
		def fixtureData = [:]
		names.each {
			fixtureData[it] = fixture.applicationContext[it]
		}
		return fixtureData
	}

	private Map loadFixtureScript(String script) {
		script = addImportsToScript(script)

		def shell = createFixtureEvaluator()
		def fixture
		new TransactionTemplate(transactionManager).execute({ status ->
			log.info "loading fixture from script..."
			fixture = shell.evaluate(script)
		} as TransactionCallback)
		def names = fixture.applicationContext.beanDefinitionNames - ["fixtureBeanPostProcessor", "autoAutoWirer"]
		def fixtureData = [:]
		names.each {
			fixtureData[it] = fixture.applicationContext[it]
		}
		return fixtureData
	}

	private String addImportsToScript(String script) {
		def buffer = new StringBuilder()
		grailsApplication.config.fixtures.autoimport.each { pkg ->
			log.debug "auto-importing all classes from $pkg"
			buffer << "import $pkg.*\n"
		}
		buffer << script
		buffer.toString()
	}

	private GroovyShell createFixtureEvaluator() {
		def fixture = fixtureLoader.createFixture()
		def classloader = Thread.currentThread().contextClassLoader
		def binding = new Binding()
		binding.fixture = fixture.&load
		binding.build = fixture.&build
		def shell = new GroovyShell(classloader, binding)
		return shell
	}

}