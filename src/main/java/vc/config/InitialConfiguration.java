package vc.config;

import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
public class InitialConfiguration {
    private final DataSource dataSource;

    public InitialConfiguration(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public DataSourceConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider
                (new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean
    public DefaultDSLContext dsl() {
        return new DefaultDSLContext(configuration());
    }

    @Bean
    public SwaggerIndexTransformer swaggerIndexTransformer(
        SwaggerUiConfigProperties configProperties,
        SwaggerUiOAuthProperties oAuthProperties,
        SwaggerUiConfigParameters configParameters,
        SwaggerWelcomeCommon welcomeCommon,
        ObjectMapperProvider objectMapperProvider) {
        return new SwaggerCodeBlockTransformer(configProperties, oAuthProperties, configParameters, welcomeCommon, objectMapperProvider);
    }

    public DefaultConfiguration configuration() {
        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();

        jooqConfiguration.set(connectionProvider());
        jooqConfiguration.setSQLDialect(SQLDialect.POSTGRES);
        // could be decreased i think. some players with A LOT of data may take a long time to select though
        jooqConfiguration.settings().setQueryTimeout(60);

        return jooqConfiguration;
    }
}
