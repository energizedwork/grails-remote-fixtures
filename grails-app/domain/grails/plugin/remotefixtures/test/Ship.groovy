package grails.plugin.remotefixtures.test

class Ship {
	
	String name
	Pirate captain
	
	static constraints = {
		name blank: false, unique: true
	}
	
	String toString() {
		"$name ($captain.name)"
	}
	
}