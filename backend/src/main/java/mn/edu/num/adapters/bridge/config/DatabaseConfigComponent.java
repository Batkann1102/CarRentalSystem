package mn.edu.num.adapters.bridge.config;

import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.infrastructure.config.DatabaseConfig;

@Component
public class DatabaseConfigComponent extends DatabaseConfig {

    @Autowired
    private ApplicationConfigComponent applicationConfig;

    private void prepare() {
        setApplicationConfig(applicationConfig);
    }

    @Override
    public boolean isEnabled() {
        prepare();
        return super.isEnabled();
    }

    @Override
    public String getUrl() {
        prepare();
        return super.getUrl();
    }

    @Override
    public String getUsername() {
        prepare();
        return super.getUsername();
    }

    @Override
    public String getPassword() {
        prepare();
        return super.getPassword();
    }
}

