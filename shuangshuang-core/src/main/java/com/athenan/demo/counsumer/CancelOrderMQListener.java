package com.athenan.demo.counsumer;

import com.alibaba.fastjson.JSON;
import com.atguigu.demo.dto.MQEntity;
import com.atnanjing.demo.dao.TradeOrder;
import com.atnanjing.demo.mapper.TradeOrderMapper;
import com.atnanjing.demo.utils.ShopCode;
import groovy.util.logging.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(topic ="${rocketmq.consumer.topics}",consumerGroup = "${spring.application.name}",messageModel = MessageModel.BROADCASTING)
public class CancelOrderMQListener implements RocketMQListener<MessageExt> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TradeOrderMapper tradeOrderMapper;
    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            //1.解析消息内容
            String body=new String(messageExt.getBody());
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            logger.info("接受消息成功");
            //2.查询订单
            TradeOrder tradeOrder = tradeOrderMapper.selectByPrimaryKey(mqEntity.getOrderId());
            //3.更新订单状态
            tradeOrder.setOrderStatus(ShopCode.SHOP_ORDER_CANCEL.getCode());
            tradeOrderMapper.updateByPrimaryKey(tradeOrder);
            logger.info("订单状态设置为取消");
        }catch (UnknownError error){
            error.printStackTrace();
            logger.info("订单取消失败");
        }
    }
}
