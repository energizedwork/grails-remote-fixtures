package grails.plugin.remotefixtures

import grails.converters.*
import grails.plugin.fixtures.exception.*
import org.springframework.transaction.support.*
import static javax.servlet.http.HttpServletResponse.*

class FixtureController {
	
	def fixtureLoader
	def grailsApplication
	def transactionManager
	
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
			def counts = [:]
			grailsApplication.domainClasses.each {
				counts[it.name] = it.clazz.count()
			}
			withFormat {
				html fixture: fixtureData
				json { render fixtureData as JSON }
				xml { render fixtureData as XML }
			}
		} catch(UnknownFixtureException e) {
			response.sendError SC_NOT_FOUND, e.message
		}
	}
	
}