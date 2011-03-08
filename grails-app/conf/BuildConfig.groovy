grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
	inherits("global") {
		// excludes 'ehcache'
	}
	log "warn"
	repositories {
		grailsPlugins()
		grailsHome()
		grailsCentral()
	}
	dependencies {
		test("org.codehaus.groovy.modules.http-builder:http-builder:0.5.0-RC2") {
			excludes "groovy", "xml-apis"
		}

	}
}
