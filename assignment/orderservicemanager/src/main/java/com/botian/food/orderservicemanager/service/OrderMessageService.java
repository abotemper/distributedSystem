package com.botian.food.orderservicemanager.service;


import com.botian.food.moodymq.listener.AbstractMessageListener;
import com.botian.food.moodymq.sender.TransMessageSender;
import com.botian.food.orderservicemanager.dao.OrderDetailDao;
import com.botian.food.orderservicemanager.dto.OrderMessageDTO;
import com.botian.food.orderservicemanager.enummeration.OrderStatus;
import com.botian.food.orderservicemanager.po.OrderDetailPO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
//这里@Autowired注解的意思就是，当Spring发现@Autowired注解时，将自动在代码上下文中找到和其匹配（默认是类型匹配）的Bean，并自动注入到相应的地方去。
@Service
//继承moodymq中的AbstractMessageListener
public class OrderMessageService extends AbstractMessageListener {

    @Autowired
    private OrderDetailDao orderDetailDao;
    @Autowired
    private TransMessageSender transMessageSender;
    ObjectMapper objectMapper = new ObjectMapper();

//moodymq中声明了此方法，我们在这里实现接收订单消息的方法。
    @Override
    public void receiveMessage(Message message) {
        //查看消息内容
        log.info("handleMessage:message:{}", new String(message.getBody()));
        try {
            //读取消息信息
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(message.getBody(),
                    OrderMessageDTO.class);
            OrderDetailPO orderPO = orderDetailDao.selectOrder(orderMessageDTO.getOrderId());
            log.info("orderPO:{}",orderPO);

            //业务代码，PO中有status，根据status的不同状态执行不同的代码。
            switch (orderPO.getStatus()) {

                //创建order的阶段。
                case ORDER_CREATING:
                    //如果确定对方收到了，那么就将订单的状态改变为RESTAURANT_CONFIRMED
                    //之后更新数据库的信息。
                    if (orderMessageDTO.getConfirmed() && null != orderMessageDTO.getPrice()) {
                        log.info("确认了，状态改了，price也有");
                        orderPO.setStatus(OrderStatus.RESTAURANT_CONFIRMED);
                        orderPO.setPrice(orderMessageDTO.getPrice());
                        orderDetailDao.update(orderPO);
                        //将信息传送到rabbitmq中，order--->deliveryman
                        transMessageSender.send(
                                "exchange.order.deliveryman",
                                "key.deliveryman",
                                orderMessageDTO
                        );
                    } else {
                        orderPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderPO);
                    }
                    break;
                    //类似地，
                case RESTAURANT_CONFIRMED:
                    if (null != orderMessageDTO.getDeliverymanId()) {
                        orderPO.setStatus(OrderStatus.DELIVERYMAN_CONFIRMED);
                        orderPO.setDeliverymanId(orderMessageDTO.getDeliverymanId());
                        orderDetailDao.update(orderPO);
                        transMessageSender.send(
                                "exchange.order.settlement",
                                "key.settlement",
                                orderMessageDTO
                        );
                    } else {
                        orderPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderPO);
                    }
                    break;
                case DELIVERYMAN_CONFIRMED:
                    if (null != orderMessageDTO.getSettlementId()) {
                        orderPO.setStatus(OrderStatus.SETTLEMENT_CONFIRMED);
                        orderPO.setSettlementId(orderMessageDTO.getSettlementId());
                        orderDetailDao.update(orderPO);
                        transMessageSender.send(
                                "exchange.order.reward",
                                "key.reward",
                                orderMessageDTO
                        );
                    } else {
                        orderPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderPO);
                    }
                    break;
                case SETTLEMENT_CONFIRMED:
                    if (null != orderMessageDTO.getRewardId()) {
                        orderPO.setStatus(OrderStatus.ORDER_CREATED);
                        orderPO.setRewardId(orderMessageDTO.getRewardId());
                        orderDetailDao.update(orderPO);
                    } else {
                        orderPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderPO);
                    }
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException();
        }
    }
}
