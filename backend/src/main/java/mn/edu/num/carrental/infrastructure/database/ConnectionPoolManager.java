package mn.edu.num.carrental.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;
import mn.edu.num.carrental.infrastructure.config.DatabaseConfig;

@Component
public class ConnectionPoolManager {

    @Autowired
    private DatabaseConfig databaseConfig;


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

