import grails.plugin.remotefixtures.test.*

include "pirates"

fixture {
	royalFortune(Ship) {
		name = "Royal Fortune"
		captain = blackBart
	}
	queenAnnesRevenge(Ship) {
		name = "Queen Anne's Revenge"
		captain = blackbeard
	}
}