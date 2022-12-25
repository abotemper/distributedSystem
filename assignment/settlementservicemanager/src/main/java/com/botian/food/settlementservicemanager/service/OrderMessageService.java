package com.botian.food.settlementservicemanager.service;

import com.botian.food.moodymq.listener.AbstractMessageListener;
import com.botian.food.moodymq.sender.TransMessageSender;
import com.botian.food.settlementservicemanager.dao.SettlementDao;
import com.botian.food.settlementservicemanager.dto.OrderMessageDTO;
import com.botian.food.settlementservicemanager.enummeration.SettlementStatus;
import com.botian.food.settlementservicemanager.po.SettlementPO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class OrderMessageService  extends AbstractMessageListener {

    @Autowired
    SettlementService settlementService;
    @Autowired
    SettlementDao settlementDao;
    @Autowired
    private TransMessageSender transMessageSender;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void receiveMessage(Message message) {
        String messageBody = new String(message.getBody());

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(messageBody,
                    OrderMessageDTO.class);
            SettlementPO settlementPO = new SettlementPO();
            settlementPO.setAmount(orderMessageDTO.getPrice());
            settlementPO.setDate(new Date());
            settlementPO.setOrderId(orderMessageDTO.getOrderId());
            Integer transactionId = settlementService.settlement(
                    orderMessageDTO.getAccountId(),
                    orderMessageDTO.getPrice());
            settlementPO.setStatus(SettlementStatus.SUCCESS);
            settlementPO.setTransactionId(transactionId);
            settlementDao.insert(settlementPO);
            orderMessageDTO.setSettlementId(transactionId);
            transMessageSender.send(
                    "exchange.settlement.order",
                    "key.order",
                    orderMessageDTO
            );
        } catch(Exception ex){
            log.error(ex.getMessage(), ex);
            throw new RuntimeException();
        }
    }

}