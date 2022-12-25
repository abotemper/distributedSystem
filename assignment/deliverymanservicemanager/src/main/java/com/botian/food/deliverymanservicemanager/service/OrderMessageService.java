package com.botian.food.deliverymanservicemanager.service;

import com.botian.food.deliverymanservicemanager.dao.DeliverymanDao;
import com.botian.food.deliverymanservicemanager.dto.OrderMessageDTO;
import com.botian.food.deliverymanservicemanager.enummeration.DeliverymanStatus;
import com.botian.food.deliverymanservicemanager.po.DeliverymanPO;
import com.botian.food.moodymq.listener.AbstractMessageListener;
import com.botian.food.moodymq.sender.TransMessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class OrderMessageService extends AbstractMessageListener {

    @Autowired
    DeliverymanDao deliverymanDao;
    @Autowired
    private TransMessageSender transMessageSender;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void receiveMessage(Message message) {
        String messageBody = new String(message.getBody());
        log.info("deliverCallback:messageBody:{}", messageBody);
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(messageBody,
                    OrderMessageDTO.class);
            List<DeliverymanPO> deliverymanPOS = deliverymanDao.selectAvaliableDeliveryman(DeliverymanStatus.AVALIABLE);
            orderMessageDTO.setDeliverymanId(deliverymanPOS.get(0).getId());
            log.info("onMessage:restaurantOrderMessageDTO:{}", orderMessageDTO);

            transMessageSender.send(
                    "exchange.order.deliveryman",
                    "key.order",
                    orderMessageDTO
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException();
        }
    }

}