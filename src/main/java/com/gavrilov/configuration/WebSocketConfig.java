package com.gavrilov.configuration;

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
    private String hostRabbitmq;
    @Value("${spring.rabbitmq.port}")
    private String portRabbitmq;
    @Value("${spring.rabbitmq.username}")
    private String usernameRabbitmq;
    @Value("${spring.rabbitmq.password}")
    private String passwordRabbitmq;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/api");
        config.enableStompBrokerRelay("/topic")
                .setRelayHost(hostRabbitmq)
                .setRelayPort(Integer.parseInt(portRabbitmq))
                .setClientLogin(usernameRabbitmq)
                .setClientPasscode(passwordRabbitmq)
                .setSystemHeartbeatReceiveInterval(10000)
                .setSystemHeartbeatSendInterval(10000);

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*").withSockJS();
    }

}