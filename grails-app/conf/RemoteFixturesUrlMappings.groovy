class RemoteFixturesUrlMappings {
	static mappings = {
		"/fixture/$fixture" {
			controller = "fixture"
			action = "load"
		}
	}
}
