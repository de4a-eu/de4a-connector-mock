package eu.de4a.connector.mock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@Configuration
@Slf4j
public class WebConfig {

    @Value("${mock.allowedOriginList}")
    List<String> allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        log.info("Initializing CORS filter");
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    
    @Bean
    ReloadableResourceBundleMessageSource messageSource ()
    {
      final var ret = new ReloadableResourceBundleMessageSource ();
      ret.setBasenames ("classpath:messages/messages");
      ret.setDefaultEncoding (StandardCharsets.UTF_8.name ());
      ret.setUseCodeAsDefaultMessage (true);
      return ret;
    }

}
