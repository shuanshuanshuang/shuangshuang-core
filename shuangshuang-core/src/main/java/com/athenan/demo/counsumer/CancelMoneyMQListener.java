package com.athenan.demo.counsumer;

import com.alibaba.fastjson.JSON;
import com.atguigu.demo.dto.MQEntity;
import com.atguigu.demo.dto.TradeUserMoneyLogDTO;
import com.atnanjing.demo.dao.TradeUserMoneyLog;
import com.atnanjing.demo.service.impl.TradeUserServiceImpl;
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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

@Slf4j
@Component
@RocketMQMessageListener(topic="${rocketmq.consumer.topics}",
        consumerGroup = "${spring.application.name}",
        messageModel = MessageModel.BROADCASTING
)
public class CancelMoneyMQListener implements RocketMQListener<MessageExt> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TradeUserServiceImpl tradeUserService;

    @Override
    public void onMessage(MessageExt messageExt) {
        try {
            //1.解析消息
            String body = new String(messageExt.getBody(), "UTF-8");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            logger.info("接收到消息");
            if(mqEntity.getUserMoney()!=null && mqEntity.getUserMoney().compareTo(BigDecimal.ZERO)>0){
                //2.调用业务层,进行余额修改
                TradeUserMoneyLogDTO userMoneyLog = new TradeUserMoneyLogDTO();
                userMoneyLog.setUseMoney(mqEntity.getUserMoney());
                userMoneyLog.setMoneyLogType(ShopCode.SHOP_USER_MONEY_REFUND.getCode());
                userMoneyLog.setUserId(mqEntity.getUserId());
                userMoneyLog.setOrderId(mqEntity.getOrderId());
                tradeUserService.updateMoneyPaid(userMoneyLog);
                logger.info("余额回退成功");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("余额回退失败");
        }


    }
}
