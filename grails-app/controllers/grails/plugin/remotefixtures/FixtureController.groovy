package grails.plugin.remotefixtures

import grails.plugin.fixtures.exception.UnknownFixtureException
import org.springframework.beans.factory.BeanCreationException
import grails.converters.*
import grails.plugin.fixtures.*
import static javax.servlet.http.HttpServletResponse.SC_FORBIDDEN
import static org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes.CONTENT_FORMAT
import org.springframework.transaction.support.*
import org.springframework.context.ApplicationContext
import grails.plugin.fixtures.files.FixtureFilePatternResolver

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
				html { render view: "success", plugin: 'remote-fixtures', model: [fixture: fixtureData] }
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
				html { render view: "success", plugin: 'remote-fixtures', model: [fixture: fixtureData] }
				json { render fixtureData as JSON }
				xml { render fixtureData as XML }
			}
		} catch (BeanCreationException e) {
			log.error "could not load fixture script, caught: $e"
			render view: "error", model: [exception: e.cause]
		}
	}

	def exec = {
		try {
			def fixture = params.fixture ?: loadNamedFixtureAsScript(params.fixtureName)
			println fixture
			def beans = params.beans
			params.remove('fixture')
			params.remove('fixtureName')
			params.remove('beans')
			def fixtureData = loadScriptWithBeans(fixture, beans, params)
			withFormat {
				html { render view: "success-beans", plugin: 'remote-fixtures', model: [results: fixtureData]}
				json { render fixtureData as JSON }
				xml { render fixtureData as XML }
			}
		} catch (BeanCreationException e) {
			log.error "could not load fixture script, caught: $e"
			render view: "error", model: [exception: e.cause]
		}
	}

	private String loadNamedFixtureAsScript(fixtureName) {
		println "fixtureName: $fixtureName, $grailsApplication, $grailsApplication.mainContext"
		def fixtureResource = new FixtureFilePatternResolver(grailsApplication, grailsApplication.mainContext).resolve(fixtureName)
		println "fixtureResource: $fixtureResource"
		def script = fixtureResource.length == 0 ? null : fixtureResource[0].inputStream.text
		println "script: $script"
		script
	}

	def loadScriptWithBeans(String script, beans, params) {
		if(beans instanceof String) {
			beans = [beans]
		}
		log.info "beans: $beans, script: $script"
		def binding = [params:params]
		beans.each { beanName ->
			binding[beanName] = grailsApplication.mainContext.getBean(beanName)
		}
		def shell = createFixtureEvaluator(binding)
		def result = shell.evaluate(script)
		if(!(result instanceof Map)) {
			result = [result: result]
		}
		result
	}

	private Map loadNamedFixture(String name) {
		def fixture
		new TransactionTemplate(transactionManager).execute({ status ->
			log.info "loading fixture $name..."
			fixture = fixtureLoader.load(name)
		} as TransactionCallback)
		return getFixtureData(fixture)
	}

	private Map loadFixtureScript(String script) {
		script = addImportsToScript(script)

		def shell = createFixtureEvaluator()
		def fixture
		new TransactionTemplate(transactionManager).execute({ status ->
			log.info "loading fixture from script..."
			fixture = shell.evaluate(script)
		} as TransactionCallback)
		return getFixtureData(fixture)
	}

	private Map<String, Object> getFixtureData(Fixture fixture) {
		def fixtureData = [:]
		if (log.isDebugEnabled()) {
			log.debug "fixture loaded: $fixture"
			log.debug "beans: ${fixture?.applicationContext?.beanDefinitionNames}"
			log.debug "inners: $fixture.inners"
		}
		fixture?.applicationContext?.beanDefinitionNames?.each {
			if (!(it in ["fixtureBeanPostProcessor", "autoAutoWirer"])) {
				fixtureData[it] = fixture.applicationContext[it]
			}
		}
		fixtureData
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

	private GroovyShell createFixtureEvaluator(extraBinding = null) {
		def fixture = fixtureLoader.createFixture()
		def classloader = Thread.currentThread().contextClassLoader
		def binding = new Binding()
		binding.fixture = fixture.&load
		binding.build = fixture.&build
		if(extraBinding) {
			extraBinding.each { key, value ->
				binding[key] = value
			}
		}
		def shell = new GroovyShell(classloader, binding)
		return shell
	}

}
