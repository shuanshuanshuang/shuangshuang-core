package com.athenan.demo.counsumer;

import com.alibaba.fastjson.JSON;
import com.atguigu.demo.dto.MQEntity;
import com.atnanjing.demo.dao.TradeCoupon;
import com.atnanjing.demo.mapper.TradeCouponMapper;
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

@Slf4j
@Component
@RocketMQMessageListener(topic="${rocketmq.consumer.topics}",
        consumerGroup = "${spring.application.name}",
        messageModel = MessageModel.BROADCASTING
)
public class CancelCouponMQListener implements RocketMQListener<MessageExt> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TradeCouponMapper tradeCouponMapper;
    @Override
    public void onMessage(MessageExt message) {
        try {
            //1. 解析消息内容
            String body = new String(message.getBody(), "UTF-8");
            MQEntity mqEntity = JSON.parseObject(body, MQEntity.class);
            logger.info("接收到消息");
            if(mqEntity.getCouponId()!=null){
                //2. 查询优惠券信息
                TradeCoupon coupon = tradeCouponMapper.selectByPrimaryKey(mqEntity.getCouponId());
                //3.更改优惠券状态
                coupon.setUsedTime(null);
                coupon.setIsUsed(ShopCode.SHOP_COUPON_UNUSED.getCode());
                coupon.setOrderId(null);
                tradeCouponMapper.updateByPrimaryKey(coupon);
            }
            logger.info("回退优惠券成功");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("回退优惠券失败");
        }

    }


}
