package com.botian.food.rewardservicemanager.service;

import com.botian.food.moodymq.listener.AbstractMessageListener;
import com.botian.food.moodymq.sender.TransMessageSender;
import com.botian.food.rewardservicemanager.dao.RewardDao;
import com.botian.food.rewardservicemanager.dto.OrderMessageDTO;
import com.botian.food.rewardservicemanager.enummeration.RewardStatus;
import com.botian.food.rewardservicemanager.po.RewardPO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class OrderMessageService extends AbstractMessageListener {

    @Autowired
    RewardDao rewardDao;
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
            log.info("handleOrderService:orderSettlementDTO:{}", orderMessageDTO);
            //业务代码
            RewardPO rewardPO = new RewardPO();
            rewardPO.setOrderId(orderMessageDTO.getOrderId());
            rewardPO.setStatus(RewardStatus.SUCCESS);
            rewardPO.setAmount(orderMessageDTO.getPrice());
            rewardPO.setDate(new Date());
            rewardDao.insert(rewardPO);
            orderMessageDTO.setRewardId(rewardPO.getId());

            transMessageSender.send(
                    "exchange.order.settlement",
                    "key.settlement",
                    orderMessageDTO
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException();
        }
    }
}