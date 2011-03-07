class RemoteFixturesUrlMappings {
	static mappings = {
		"/fixture/load" {
			controller = "fixture"
			action = "load"
		}

		"/fixture/exec" {
			controller = "fixture"
			action = "exec"
		}

		"/fixture/$fixture" {
			controller = "fixture"
			action = "loadNamed"
		}
	}
}
