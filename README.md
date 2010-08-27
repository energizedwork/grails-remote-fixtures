# Remote Fixtures Plugin

This plugin depends on the Fixtures plugin. It provides a controller endpoint you can use to load fixtures. This is primarily useful for functional testing.

## Loading fixtures

Simply hit `/fixture/$fixtureName` to load a fixture. A report page is displayed once the fixture has been successfully loaded. The report output can be sent as JSON or XML instead by appending `.json` or `.xml` to the URL (assuming your application has `grails.mime.file.extensions = true` set to `true` in _Config.groovy_).

## Destroying data

Since code can be executed in fixture files it is possible to create a fixture that tears down data. For example:

	Ship.list()*.delete()
	Pirate.list()*.delete()

