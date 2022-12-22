package com.gaethering.gaetheringserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    public static final String APPLICATION_DESTINATION_PREFIX = "/app";
    public static final String DESTINATION_PREFIX = "/topic";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(APPLICATION_DESTINATION_PREFIX);
        registry.enableStompBrokerRelay(DESTINATION_PREFIX)
            .setAutoStartup(true)
            .setRelayHost(host)
            .setRelayPort(61613)
            .setSystemLogin(username)
            .setSystemPasscode(password)
            .setClientLogin(username)
            .setClientPasscode(password);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connect").withSockJS();
        registry.addEndpoint("/ws-connect");
    }
}
