package mn.edu.num.infrastructure.config;

public class DatabaseConfig {

	private ApplicationConfig applicationConfig;

	public void setApplicationConfig(ApplicationConfig applicationConfig) {
		this.applicationConfig = applicationConfig;
	}

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
