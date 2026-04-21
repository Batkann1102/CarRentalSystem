package mn.edu.num.carrental.infrastructure.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import mn.edu.num.annotation.Autowired;
import mn.edu.num.annotation.Component;

@Component
public class DatabaseInitializer {

    @Autowired
    private ConnectionPoolManager connectionPoolManager;

    public void initializeSchemaIfEnabled() {
        if (!connectionPoolManager.isDatabaseEnabled()) {
            return;
        }

        String schemaSql = loadSchemaSql();
        Optional<Connection> connectionOptional = connectionPoolManager.getConnection();
        if (connectionOptional.isEmpty()) {
            System.err.println("[Database] Schema initialization skipped because PostgreSQL is not reachable.");
            return;
        }

        try (Connection connection = connectionOptional.get();
             Statement statement = connection.createStatement()) {
            for (String sql : schemaSql.split(";\\s*(\\r?\\n|$)")) {
                String trimmedSql = sql.trim();
                if (!trimmedSql.isEmpty()) {
                    statement.execute(trimmedSql);
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to initialize PostgreSQL schema: " + ex.getMessage(), ex);
        }
    }

    private String loadSchemaSql() {
        try (InputStream inputStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (inputStream == null) {
                throw new IllegalStateException("schema.sql was not found on the classpath.");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read schema.sql", ex);
        }
    }
}

