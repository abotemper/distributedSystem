package com.botian.food.moodymq.listener;

import com.botian.food.moodymq.po.TransMessagePO;
import com.botian.food.moodymq.service.TransMessageService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
public abstract class AbstractMessageListener implements ChannelAwareMessageListener {

    @Autowired
    TransMessageService transMessageService;

    @Value("#{new Integer('${moodymq.reconsumeTimes}')}")
    int reconsumeTimes;

    public abstract void receiveMessage(Message message);

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();

        TransMessagePO transMessagePO =
                transMessageService.messageReceiveReady(
                        message.getMessageProperties().getMessageId(),
                        message.getMessageProperties().getReceivedExchange(),
                        message.getMessageProperties().getReceivedRoutingKey(),
                        message.getMessageProperties().getConsumerQueue(),
                        new String(message.getBody())
                );

        log.info("Received message, current message ID: {} consumption times: {}", messageProperties.getMessageId(), transMessagePO.getSequence());

        try {
            receiveMessage(message);
            log.info("消息收到了");
            // 成功的回执
            channel.basicAck(deliveryTag, false);

            transMessageService.messageReceiveSuccess(transMessagePO.getId());
        } catch (Exception e) {
            log.error("RabbitMQ message consumption failed" + e.getMessage(), e);
            if (transMessagePO.getSequence() >= reconsumeTimes) {
                // 入死信队列
                channel.basicReject(deliveryTag, false);
            } else {
                // 重回到队列，重新消费, 按照2的指数级递增
                Thread.sleep((long) (Math.pow(2, transMessagePO.getSequence()) * 1000));
                channel.basicNack(deliveryTag, false, true);
            }
        }
    }
}
