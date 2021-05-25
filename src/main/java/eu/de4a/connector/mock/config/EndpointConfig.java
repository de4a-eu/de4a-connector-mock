package eu.de4a.connector.mock.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class EndpointConfig {

    @Bean
    @Profile("do")
    public DOConfig initDOConfig() {
        return new  DOConfig();
    }
}
