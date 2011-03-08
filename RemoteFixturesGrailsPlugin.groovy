class RemoteFixturesGrailsPlugin {

    def groupId = "com.energizedwork"	
    def version = "1.0-beans-3-SNAPSHOT"
    def grailsVersion = "1.3.0 > *"
    def dependsOn = [fixtures: "1.0.1"]
    def pluginExcludes = [
            "grails-app/views/error.gsp",
            "grails-app/i18n/*",
			"grails-app/domain/**/*",
			"fixtures",
            "scripts/ReleaseLocal.groovy"
    ]

    def author = "Rob Fletcher"
    def authorEmail = "rob@energizedwork.com"
    def title = "Remote Fixtures Plugin"
    def description = '''\\
Provides a controller endpoint that can be used to load fixtures.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/remote-fixtures"

    def doWithWebDescriptor = { xml ->
    }

    def doWithSpring = {
    }

    def doWithDynamicMethods = { ctx ->
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def onChange = { event ->
    }

    def onConfigChange = { event ->
    }
}
