import groovyx.net.http.HTTPBuilder
import spock.lang.Specification
import static groovyx.net.http.ContentType.HTML
import static groovyx.net.http.ContentType.JSON
import javax.servlet.http.HttpServletResponse
import groovyx.net.http.HttpResponseException

class FixtureSpec extends Specification {

	def "can load a fixture by name"() {
		when:
		def http = new HTTPBuilder("http://localhost:8080")
		def response = http.post(path: "/fixture/pirates")

		then:
		response.BODY.@class == "success"
	}

	def "can throw a script block at the fixture controller"() {
		when:
		def http = new HTTPBuilder("http://localhost:8080")
		def body = [fixture: '''
fixture {
blackbeard(Pirate, name: "Edward Teach", nickname: "Blackbeard")
calicoJack(Pirate, name: "Jack Rackham", nickname: "Calico Jack")
blackBart(Pirate, name: "Bartholomew Roberts", nickname: "Black Bart")
}
''']
		def result
		def responseCode
		http.post(path: "/fixture/load.json", body: body, contentType: JSON) { resp, json ->
			responseCode = resp.status
			result = json
		}

		then:
		responseCode == HttpServletResponse.SC_OK
		result.keySet() == ['blackbeard', 'calicoJack', 'blackBart'] as Set
	}
	
	def "can call a method on a spring bean when calling executeWithBeans"() {
		when:
		HTTPBuilder http = new HTTPBuilder("http://localhost:8080")
		def body = [
		        beans: ['transactionManager', 'fixtureLoader'],
				fixture: """
					[transactionManager: transactionManager.getClass().simpleName, fixtureLoader: fixtureLoader.getClass().simpleName]
				"""
		]
		def result
		def responseCode
		http.post(path: "/fixture/executeWithBeans.json", body: body, contentType: JSON) { resp, json ->
			responseCode = resp.status
			result = json
		}

		then:
		responseCode == HttpServletResponse.SC_OK
		result.size() == 2
		result.transactionManager == 'GrailsHibernateTransactionManager'
		result.fixtureLoader == 'FixtureLoader'
	}

	def "if we call executeWithBeans with an invalid bean name, exception thrown"() {
		when:
		HTTPBuilder http = new HTTPBuilder("http://localhost:8080")
		def body = [
		        beans: ['omgwtf'],
				fixture: """
					[omgwtf: omgwtf.getClass().simpleName]
				"""
		]
		http.post(path: "/fixture/executeWithBeans.json", body: body, contentType: JSON)

		then:
		thrown(HttpResponseException)
	}
}
