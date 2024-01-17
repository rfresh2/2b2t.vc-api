package vc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;

import java.time.Duration;

@SpringBootApplication
@EnableCaching
@OpenAPIDefinition(
		servers = {
				@Server(url = "https://api.2b2t.vc")
		}
)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory() {
		var requestFactory = new JdkClientHttpRequestFactory();
		requestFactory.setReadTimeout(Duration.ofSeconds(5));
		return requestFactory;
	}
}
