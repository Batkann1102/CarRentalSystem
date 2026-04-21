package mn.edu.num.carrental.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import mn.edu.num.annotation.Component;

@Component
public class ApplicationConfig {

    private final Properties properties = new Properties();

    public ApplicationConfig() {
        try (InputStream inputStream = ApplicationConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load application.properties", ex);
        }
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getServerPort() {
        return Integer.parseInt(getProperty("server.port", "8080"));
    }

    public String getContextPath() {
        return getProperty("server.context-path", "/");
    }
}

