package vc.data.duckdb;

import org.duckdb.DuckDBConnection;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.DriverManager;

@Component
public class DuckDbInstance implements DisposableBean {
    DuckDBConnection connection;
    private final String connectionString;
    Jdbi jdbi;

    public DuckDbInstance(
        final @Value("${DATABASE_HOST}") String postgresHost,
        final @Value("${DATABASE_PORT}") String postgresPort,
        final @Value("${DATABASE_DB}") String postgresDb,
        final @Value("${DATABASE_USER}") String postgresUsername,
        final @Value("${DATABASE_PASSWORD}") String postgresPassword
    ) {
        this.connectionString = "'dbname=%s host=%s port=%s user=%s password=%s'".formatted(
            postgresDb,
            postgresHost,
            postgresPort,
            postgresUsername,
            postgresPassword
        );
        initializeConnection();
    }

    public DuckDBConnection getConnection() {
        return connection;
    }

    public void initializeConnection() {
        try {
            connection = (DuckDBConnection) DriverManager.getConnection("jdbc:duckdb:vc.db");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize DuckDB connection", e);
        }
        this.jdbi = Jdbi.create(connection);
        new PostgresPlugin().customizeJdbi(jdbi);
        try (var handle = jdbi.open()) {
            handle.execute("SET memory_limit TO '1GB'");
            handle.createUpdate("ATTACH " + connectionString + " AS postgres_db (TYPE postgres, READ_ONLY)")
                .execute();
        }
    }

    public Jdbi getJdbi() {
        return jdbi;
    }

    @Override
    public void destroy() throws Exception {
        connection.close();
    }
}
