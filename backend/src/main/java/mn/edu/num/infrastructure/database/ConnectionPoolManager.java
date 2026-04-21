package mn.edu.num.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import mn.edu.num.infrastructure.config.DatabaseConfig;

public class ConnectionPoolManager {

    private DatabaseConfig databaseConfig;

    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public Optional<Connection> getConnection() {
        if (!databaseConfig.isEnabled() || databaseConfig.getUrl().isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(DriverManager.getConnection(
                    databaseConfig.getUrl(),
                    databaseConfig.getUsername(),
                    databaseConfig.getPassword()
            ));
        } catch (SQLException ex) {
            System.err.println("[Database] Could not open PostgreSQL connection: " + ex.getMessage());
            return Optional.empty();
        }
    }
}

