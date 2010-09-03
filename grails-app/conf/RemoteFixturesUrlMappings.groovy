class RemoteFixturesUrlMappings {
	static mappings = {
		"/fixture/script" {
			controller = "fixture"
			action = "script"
		}

		"/fixture/$fixture" {
			controller = "fixture"
			action = "load"
		}
	}
}
