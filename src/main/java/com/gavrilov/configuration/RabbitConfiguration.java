package com.gavrilov.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    private final Logger logger = LoggerFactory.getLogger(RabbitConfiguration.class);

    @Value("${spring.rabbitmq.host}")
    private String hostRabbitmq;

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(hostRabbitmq);
    }

    /**
     * Задает базовый набор переносимых административных операций для AMQP
     *
     * @return
     */
    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    /**
     * Вспомогательный класс, упрощающий синхронный доступ к RabbitMQ (отправка и получение сообщений).
     *
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(connectionFactory());
    }

    @Bean
    public Queue myQueue() {
        return new Queue("rabbit-queue");
    }

    @Bean
    public FanoutExchange topicExchange() {
        return new FanoutExchange("rabbit-fanout-exchange");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(myQueue()).to(topicExchange());
    }
}
