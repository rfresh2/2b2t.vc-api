package vc.data.duckdb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DuckDbInstance implements DisposableBean {
    private static final int DEFAULT_DUCKDB_POOL_SIZE = 2;

    private HikariDataSource dataSource;
    private final String connectionString;
    private final String duckDbPath;
    private final int poolSize;
    private final boolean attachPostgres;
    private Jdbi jdbi;

    public DuckDbInstance(
        final @Value("${DATABASE_HOST}") String postgresHost,
        final @Value("${DATABASE_PORT}") String postgresPort,
        final @Value("${DATABASE_DB}") String postgresDb,
        final @Value("${DATABASE_USER}") String postgresUsername,
        final @Value("${DATABASE_PASSWORD}") String postgresPassword,
        final @Value("${DUCK_DB_PATH:vc.db}") String duckDbPath,
        final @Value("${DUCK_DB_POOL_SIZE:" + DEFAULT_DUCKDB_POOL_SIZE + "}") int poolSize,
        final @Value("${DUCK_DB_ATTACH_POSTGRES:true}") boolean attachPostgres
    ) {
        this.connectionString = "'dbname=%s host=%s port=%s user=%s password=%s'".formatted(
            postgresDb,
            postgresHost,
            postgresPort,
            postgresUsername,
            postgresPassword
        );
        this.duckDbPath = duckDbPath;
        this.poolSize = poolSize;
        this.attachPostgres = attachPostgres;
        initializeConnection();
    }

    public synchronized void initializeConnection() {
        if (dataSource != null) {
            dataSource.close();
        }
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:duckdb:" + duckDbPath);
        config.setDriverClassName("org.duckdb.DuckDBDriver");
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(1);
        config.setReadOnly(false);
        config.setPoolName("duckdb");
        config.setConnectionInitSql(connectionInitSql());
        dataSource = new HikariDataSource(config);
        this.jdbi = Jdbi.create(dataSource);
        new PostgresPlugin().customizeJdbi(jdbi);
    }

    private String connectionInitSql() {
        List<String> statements = new ArrayList<>();
        statements.add("SET memory_limit TO '1GB'");
        if (attachPostgres) {
            statements.add("ATTACH IF NOT EXISTS " + connectionString + " AS postgres_db (TYPE postgres, READ_ONLY)");
        }
        statements.add("SET threads TO %s".formatted(Math.clamp(Runtime.getRuntime().availableProcessors() / 2, 1, 4)));
        return statements.stream().collect(Collectors.joining(";", "", ";"));
    }

    public Jdbi getJdbi() {
        return jdbi;
    }

    @Override
    public synchronized void destroy() {
        dataSource.close();
    }
}
