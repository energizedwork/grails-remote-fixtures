import org.apache.ivy.core.settings.IvySettings
import org.apache.ivy.plugins.resolver.IvyRepResolver

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.plugin.release.url='http://plugins.energizedwork.com'
grails.plugin.release.directory='/var/www/plugins.energizedwork.com/htdocs'

IvyRepResolver ewResolver = new IvyRepResolver()
IvySettings settings = new IvySettings()
settings.defaultLatestStrategy = settings.getLatestStrategy('latest-time')
ewResolver.settings = settings
ewResolver.changingPattern = '^.+-SNAPSHOT$'
ewResolver.name = 'ew'
ewResolver.ivyroot = 'http://repo.energizedwork.com/'
ewResolver.ivypattern = '[organisation]/[module]-ivy-[revision].xml'
ewResolver.artroot = 'http://repo.energizedwork.com/'
ewResolver.artpattern = '[organisation]/[module]-[revision](-[classifier]).[ext]'
ewResolver.checkmodified = true
ewResolver.latest = 'latest-time'

grails.project.dependency.resolution = {
	inherits("global") {
		// excludes 'ehcache'
	}
	log "warn"
	repositories {
        resolver ewResolver
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenLocal()
        mavenCentral()
        mavenRepo 'http://repository.codehaus.org'
	}
	dependencies {
		test("org.codehaus.groovy.modules.http-builder:http-builder:0.5.0-RC2") {
			excludes "groovy", "xml-apis"
		}
        build 'com.energizedwork:release-plugin:0.1-SNAPSHOT'
	}
}
