package mn.edu.num.carrental.infrastructure.config;

import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;

@Component
public class DatabaseConfig {

	@Autowired
	private ApplicationConfig applicationConfig;


	public boolean isEnabled() {
		return Boolean.parseBoolean(applicationConfig.getProperty("db.enabled", "false"));
	}

	public String getUrl() {
		return applicationConfig.getProperty("db.url", "");
	}

	public String getUsername() {
		return applicationConfig.getProperty("db.username", "");
	}

	public String getPassword() {
		return applicationConfig.getProperty("db.password", "");
	}
}
