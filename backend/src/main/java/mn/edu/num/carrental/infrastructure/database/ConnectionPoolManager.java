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

    private String lastConnectionFailureMessage;

    @Autowired
    private DatabaseConfig databaseConfig;

    public boolean isDatabaseEnabled() {
        return databaseConfig.isEnabled();
    }


    public Optional<Connection> getConnection() {
        if (!databaseConfig.isEnabled() || databaseConfig.getUrl().isBlank()) {
            return Optional.empty();
        }

        try {
            Connection connection = DriverManager.getConnection(
                    databaseConfig.getUrl(),
                    databaseConfig.getUsername(),
                    databaseConfig.getPassword()
            );
            clearConnectionFailure();
            return Optional.of(connection);
        } catch (SQLException ex) {
            logConnectionFailure(ex);
            return Optional.empty();
        }
    }

    public Connection getRequiredConnection() {
        if (!databaseConfig.isEnabled()) {
            throw new IllegalStateException("PostgreSQL is not enabled. Set db.enabled=true to require database persistence.");
        }

        if (databaseConfig.getUrl().isBlank()) {
            throw new IllegalStateException("Database URL is empty. Configure db.url in application.properties or DB_URL in the environment.");
        }

        try {
            return DriverManager.getConnection(
                    databaseConfig.getUrl(),
                    databaseConfig.getUsername(),
                    databaseConfig.getPassword()
            );
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not connect to PostgreSQL: " + ex.getMessage(), ex);
        }
    }

    private synchronized void logConnectionFailure(SQLException ex) {
        String message = ex.getMessage();
        if (message != null && message.equals(lastConnectionFailureMessage)) {
            return;
        }

        lastConnectionFailureMessage = message;
        System.err.println("[Database] PostgreSQL is unavailable, using in-memory fallback. " + message);
    }

    private synchronized void clearConnectionFailure() {
        lastConnectionFailureMessage = null;
    }
}

