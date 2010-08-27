# Remote Fixtures Plugin

This plugin depends on the [Fixtures plugin][1]. It provides a controller endpoint you can use to load fixtures. This is primarily useful for functional testing.

## Loading fixtures

Simply hit `/fixture/$fixtureName` to load a fixture. A report page is displayed indicating the whether the fixture loaded successfully and what data it is composed of. The report output can be sent as JSON or XML instead by appending `.json` or `.xml` to the URL (assuming your application has `grails.mime.file.extensions = true` set in _Config.groovy_).

## Destroying data

Since code can be executed in fixture files it is possible to create a fixture that tears down data. For example, you could create a `fixtures/tearDown.groovy` containing the following:
	
	Ship.list()*.delete()
	Pirate.list()*.delete()
	
[1]: http://gpc.github.com/grails-fixtures/docs/manual/index.html "Grails Fixtures Plugin"