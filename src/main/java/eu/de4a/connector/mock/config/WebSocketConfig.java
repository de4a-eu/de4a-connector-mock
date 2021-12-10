package eu.de4a.connector.mock.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Profile("do")
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    DOConfig doConfig;
    @Value("${mock.allowedOriginList}")
    List<String> allowedOrigins;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(String.format("%s%s", doConfig.getPreviewBaseEndpoint(), doConfig.getWebsocketMessagesEndpoint()));
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.debug("allowed origins: {}", allowedOrigins.toArray(new String[0]));
        registry
                .addEndpoint(String.format("%s%s", doConfig.getPreviewBaseEndpoint(), doConfig.getWebsocketSocketEndpoint()))
                .setAllowedOrigins(allowedOrigins.toArray(new String[0]))
                .withSockJS()
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1.5.1/dist/sockjs.min.js");
    }
}
