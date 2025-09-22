package vc.springboot;


import me.paulschwarz.springdotenv.DotenvPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.bootstrap.ConfigurableBootstrapContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Fixed compat with Spring boot 4.x and dotenv
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DotenvApplicationRunListener implements SpringApplicationRunListener {

    @SuppressWarnings("unused")
    public DotenvApplicationRunListener(SpringApplication application, String[] args) {
        // unused
    }

    /**
     * Add the {@link DotenvPropertySource} to the application's environment.
     *
     * @param bootstrapContext the bootstrap context
     * @param environment the environment
     */
    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        DotenvPropertySource.addToEnvironment(environment);
        SpringApplicationRunListener.super.environmentPrepared(bootstrapContext, environment);
    }
}
