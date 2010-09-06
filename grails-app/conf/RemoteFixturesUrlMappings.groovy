class RemoteFixturesUrlMappings {
	static mappings = {
		"/fixture/load" {
			controller = "fixture"
			action = "load"
		}

		"/fixture/$fixture" {
			controller = "fixture"
			action = "loadNamed"
		}
	}
}
