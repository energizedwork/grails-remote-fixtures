package grails.plugin.remotefixtures.test

class Pirate {
	
	String name
	String nickname
	
	static constraints = {
		name blank: false
	}
	
	String toString() {
		"Pirate $id: $name (\"$nickname\")"
	}
	
}