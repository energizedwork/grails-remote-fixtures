package grails.plugin.remotefixtures

import grails.converters.*
import grails.plugin.fixtures.exception.*
import org.springframework.transaction.support.*
import org.springframework.beans.factory.*
import static javax.servlet.http.HttpServletResponse.*

class FixtureController {
	
	def fixtureLoader
	def grailsApplication
	def transactionManager

	def beforeInterceptor = {
		if (!grailsApplication.config.fixtures.enabled) {
			response.sendError SC_FORBIDDEN
		}
	}
	
	def load = {
		try {
			def fixture
			new TransactionTemplate(transactionManager).execute({ status ->
               	fixture = fixtureLoader.load(params.fixture)
           	} as TransactionCallback)
			def names = fixture.applicationContext.beanDefinitionNames - ["fixtureBeanPostProcessor", "autoAutoWirer"]
			def fixtureData = [:]
			names.each {
				fixtureData[it] = fixture.applicationContext[it]
			}
			withFormat {
				html { render view: "success", model: [fixture: fixtureData] }
				json { render fixtureData as JSON }
				xml { render fixtureData as XML }
			}
		} catch(UnknownFixtureException e) {
			render view: "notfound", model: [message: e.message]
		} catch(BeanCreationException e) {
			println "caught: $e"
			render view: "error", model: [exception: e.cause]
		}
	}
	
}