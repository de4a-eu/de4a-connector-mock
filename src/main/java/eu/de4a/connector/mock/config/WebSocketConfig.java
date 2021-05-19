package eu.de4a.connector.mock.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@Profile("do")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    DOConfig doConfig;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(String.format("%s%s", doConfig.getPreviewBaseEndpoint(), doConfig.getWebsocketMessagesEndpoint()));
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint(String.format("%s%s", doConfig.getPreviewBaseEndpoint(), doConfig.getWebsocketSocketEndpoint()))
                .withSockJS()
                .setClientLibraryUrl("https://cdn.jsdelivr.net/sockjs/1.5.1/sockjs.min.js");
    }
}
