package mn.edu.num.adapters.bridge.database;

import java.sql.Connection;
import java.util.Optional;
import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.adapters.bridge.config.DatabaseConfigComponent;
import mn.edu.num.infrastructure.database.ConnectionPoolManager;

@Component
public class ConnectionPoolManagerComponent extends ConnectionPoolManager {

    @Autowired
    private DatabaseConfigComponent databaseConfig;

    @Override
    public Optional<Connection> getConnection() {
        setDatabaseConfig(databaseConfig);
        return super.getConnection();
    }
}

