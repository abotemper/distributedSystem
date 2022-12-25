package com.botian.food.rewardservicemanager.config;

import com.botian.food.rewardservicemanager.service.OrderMessageService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitConfig {

    @Bean
    public Exchange exchange1() {
        return new TopicExchange("exchange.order.reward");
    }

    @Bean
    public Queue queue1() {
        return new Queue("queue.reward",true, false, false, null);
    }

    @Bean
    public Binding binding1() {
        return new Binding(
                "queue.reward",
                Binding.DestinationType.QUEUE,
                "exchange.order.reward",
                "key.reward",
                null);
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(ConnectionFactory connectionFactory,
                                                                         OrderMessageService orderMessageService) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("queue.order");
        container.setExposeListenerChannel(true);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(orderMessageService);
        return container;
    }
}