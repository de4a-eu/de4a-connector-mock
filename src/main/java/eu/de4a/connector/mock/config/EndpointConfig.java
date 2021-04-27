package eu.de4a.connector.mock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EndpointConfig {

    @Bean
    public DOConfig initDOConfig() {
        return new  DOConfig();
    }
}
