package com.botian.food.orderservicemanager.service;


import com.botian.food.moodymq.sender.TransMessageSender;
import com.botian.food.orderservicemanager.dao.OrderDetailDao;
import com.botian.food.orderservicemanager.dto.OrderMessageDTO;
import com.botian.food.orderservicemanager.enummeration.OrderStatus;
import com.botian.food.orderservicemanager.po.OrderDetailPO;
import com.botian.food.orderservicemanager.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderDetailDao orderDetailDao;
    @Autowired
    private TransMessageSender transMessageSender;

    public void createOrder(OrderCreateVO orderCreateVO) {
        log.info("orderService方法运行了");
        //创建订单
        log.info("createOrder:orderCreateVO:{}", orderCreateVO);
        OrderDetailPO orderPO = new OrderDetailPO();
        //设置订单数据
        orderPO.setAddress(orderCreateVO.getAddress());
        orderPO.setAccountId(orderCreateVO.getAccountId());
        orderPO.setProductId(orderCreateVO.getProductId());
        orderPO.setStatus(OrderStatus.ORDER_CREATING);
        orderPO.setDate(new Date());
        //通过接口OrderDetailDao插入数据到数据库
        orderDetailDao.insert(orderPO);
        OrderMessageDTO orderMessageDTO = new OrderMessageDTO();
        orderMessageDTO.setOrderId(orderPO.getId());
        orderMessageDTO.setProductId(orderPO.getProductId());
        orderMessageDTO.setAccountId(orderCreateVO.getAccountId());


        //发送信息到rabbitmq
        transMessageSender.send(
                "exchange.order.restaurant",
                "key.restaurant",
                orderMessageDTO
        );

        log.info("message sent");

    }
}


