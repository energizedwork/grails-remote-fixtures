package grails.plugin.remotefixtures

import grails.converters.*
import grails.plugin.fixtures.exception.*
import org.springframework.transaction.support.*
import org.springframework.beans.factory.*
import static javax.servlet.http.HttpServletResponse.*
import org.apache.commons.lang.StringUtils

class FixtureController {

	def fixtureLoader
	def grailsApplication
	def transactionManager

	def beforeInterceptor = {
		println request.dump()
		if (!grailsApplication.config.fixtures.enabled) {
			response.sendError SC_FORBIDDEN
		}
	}

	def load = {
		try {
			def fixtureData = loadFixture(params.fixture)
			withFormat {
				html { render view: "success", model: [fixture: fixtureData] }
				json { render fixtureData as JSON }
				xml { render fixtureData as XML }
			}
		} catch (UnknownFixtureException e) {
			render view: "notfound", model: [message: e.message]
		} catch (BeanCreationException e) {
			println "caught: $e"
			render view: "error", model: [exception: e.cause]
		}
	}

	def script = {
		File file = File.createTempFile("fixture", ".groovy", new File("./fixtures"))
		try {
			file.withWriter {writer ->
				writer << params.fixture
			}
			println "generated file $file.absolutePath"
			println "which contains... $file.text"
			def name = StringUtils.substringBeforeLast(file.name, ".")
			println "loading fixture $name"

			def fixtureData = loadFixture(name)
			println "got data $fixtureData"
//			withFormat {
//				html {
					render view: "success", model: [fixture: fixtureData]
//				}
//				json { render fixtureData as JSON }
//				xml { render fixtureData as XML }
//			}
		} catch (UnknownFixtureException e) {
			println "o noes, unknown fixture"
			render view: "notfound", model: [message: e.message]
		} catch (BeanCreationException e) {
			println "caught: $e"
			render view: "error", model: [exception: e.cause]
		} finally {
			println "deleting $file.absolutePath"
			file.delete()
		}
	}

	private Map loadFixture(String name) {
		def fixture
		new TransactionTemplate(transactionManager).execute({ status ->
			fixture = fixtureLoader.load(name)
		} as TransactionCallback)
		def names = fixture.applicationContext.beanDefinitionNames - ["fixtureBeanPostProcessor", "autoAutoWirer"]
		def fixtureData = [:]
		names.each {
			fixtureData[it] = fixture.applicationContext[it]
		}
		return fixtureData
	}
}