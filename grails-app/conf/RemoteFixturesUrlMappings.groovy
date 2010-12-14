class RemoteFixturesUrlMappings {
	static mappings = {
		"/fixture/load" {
			controller = "fixture"
			action = "load"
		}

		"/fixture/executeWithBeans" {
			controller = "fixture"
			action = "executeWithBeans"
		}

		"/fixture/$fixture" {
			controller = "fixture"
			action = "loadNamed"
		}
	}
}
