import groovyx.net.http.HTTPBuilder
import spock.lang.Specification
import static groovyx.net.http.ContentType.*

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
import grails.plugin.remotefixtures.test.*

fixture {
blackbeard(Pirate, name: "Edward Teach", nickname: "Blackbeard")
calicoJack(Pirate, name: "Jack Rackham", nickname: "Calico Jack")
blackBart(Pirate, name: "Bartholomew Roberts", nickname: "Black Bart")
}
''']
		def response = http.post(path: "/fixture/script", body: body, requestContentType: URLENC, contentType: HTML)

		then:
		response.BODY.@class == "success"
	}
}
