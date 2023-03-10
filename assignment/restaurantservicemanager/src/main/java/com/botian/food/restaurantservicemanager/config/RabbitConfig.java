package com.botian.food.restaurantservicemanager.config;
import com.botian.food.restaurantservicemanager.service.OrderMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitConfig {


    @Bean
    public Exchange exchange1() {
        return new DirectExchange("exchange.order.restaurant");
    }

    @Bean
    public Queue queue1() {

        return new Queue("queue.restaurant",true, false, false, null);
    }

    @Bean
    public Binding binding1() {
        return new Binding(
                "queue.restaurant",
                Binding.DestinationType.QUEUE,
                "exchange.order.restaurant",
                "key.restaurant",
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
