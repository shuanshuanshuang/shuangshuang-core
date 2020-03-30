package com.athenan.demo.counsumer;

import com.alibaba.fastjson.JSON;
import com.atguigu.demo.dto.MQEntity;
import com.atnanjing.demo.dao.TradeGoods;
import com.atnanjing.demo.dao.TradeMqConsumerLog;
import com.atnanjing.demo.dao.TradeMqConsumerLogExample;
import com.atnanjing.demo.dao.TradeMqConsumerLogKey;
import com.atnanjing.demo.mapper.TradeGoodsMapper;
import com.atnanjing.demo.mapper.TradeMqConsumerLogMapper;
import com.atnanjing.demo.utils.ShopCode;
import groovy.util.logging.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@RocketMQMessageListener(topic="${rocketmq.consumer.topics}",
        consumerGroup = "${spring.application.name}",
        messageModel = MessageModel.BROADCASTING
)
public class CancelMQListener implements RocketMQListener<MessageExt> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
   @Value("${spring.application.name}")
   private String groupName;

   @Autowired
   private TradeMqConsumerLogMapper tradeMqConsumerLogMapper;

   @Autowired
   private TradeGoodsMapper tradeGoodsMapper;

    @Override
    public void onMessage(MessageExt messageExt) {
        String msgId=null;
        String tags=null;
        String keys=null;
        String body=null;
        try{
            //1.解析消息内容
            tags=messageExt.getTags();
            keys=messageExt.getKeys();
            msgId=messageExt.getMsgId();
            body=new String(messageExt.getBody(),"UTF-8");
            logger.info("接受消息成功");
            //2.查询消息消费记录
            TradeMqConsumerLogKey primaryKey=new TradeMqConsumerLogKey();
            primaryKey.setMsgTag(tags);
            primaryKey.setGroupName(groupName);
            primaryKey.setMsgTag(tags);
            TradeMqConsumerLog mqConsumerLog = tradeMqConsumerLogMapper.selectByPrimaryKey(primaryKey);
            //3.判断是否消费过
            if(mqConsumerLog!=null){
                ///3.1获取消息处理状态
                Integer status = mqConsumerLog.getConsumerStatus();
                //处理过哦。。。返回
                if(ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode().intValue()==status.intValue()){
                    logger.info("消息:"+msgId+",已经处理过");
                    return;
                }
                //正在处理。。。返回
                if(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode().intValue()==status.intValue()){
                    logger.info("消息:"+msgId+",正在处理");
                    return;
                }
                //处理失败
                if(ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode().intValue()==status.intValue()){
                    Integer times = mqConsumerLog.getConsumerTimes();
                    if(times>3){
                        logger.info("消息:"+msgId+",消息处理超过3次,不能再进行处理了");
                        return;
                    }
                    mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                    //使用数据库乐观锁更新
                    TradeMqConsumerLogExample example=new TradeMqConsumerLogExample();
                    TradeMqConsumerLogExample.Criteria criteria = example.createCriteria();
                    criteria.andMsgTagEqualTo(mqConsumerLog.getMsgTag());
                    criteria.andMsgKeyEqualTo(mqConsumerLog.getMsgKey());
                    criteria.andGroupNameEqualTo(mqConsumerLog.getGroupName());
                    criteria.andConsumerTimesEqualTo(mqConsumerLog.getConsumerTimes());
                    int update = tradeMqConsumerLogMapper.updateByExampleSelective(mqConsumerLog, example);
                    if(update<=0){
                        //未修改成功,其他线程并发修改
                        logger.info("并发修改,稍后处理");
                    }
                }
            }else{
                //4.判断如果没有消费过
                mqConsumerLog=new TradeMqConsumerLog();
                mqConsumerLog.setMsgTag(tags);
                mqConsumerLog.setMsgKey(keys);
                mqConsumerLog.setGroupName(groupName);
                mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_PROCESSING.getCode());
                mqConsumerLog.setMsgBody(body);
                mqConsumerLog.setMsgId(msgId);
                mqConsumerLog.setConsumerTimes(0);
                //将消息处理消息添加到数据库
                tradeMqConsumerLogMapper.insert(mqConsumerLog);
            }
            //5.回退库存
            MQEntity mqEntity=JSON.parseObject(body,MQEntity.class);
            Long orderId = mqEntity.getOrderId();
            TradeGoods tradeGoods = tradeGoodsMapper.selectByPrimaryKey(orderId);
            tradeGoods.setGoodsNumber(tradeGoods.getGoodsNumber()+mqEntity.getGoodsNum());
            tradeGoodsMapper.updateByPrimaryKey(tradeGoods);
            //6.将消息的处理状态设为成功
            mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_SUCCESS.getCode());
            mqConsumerLog.setConsumerTimestamp(new Date());
            tradeMqConsumerLogMapper.updateByPrimaryKeySelective(mqConsumerLog);
            logger.info("回退库存成功");
        }catch (Exception e){
            e.printStackTrace();
            TradeMqConsumerLogKey primarykey=new TradeMqConsumerLogKey();
            primarykey.setMsgKey(keys);
            primarykey.setMsgTag(tags);
            primarykey.setGroupName(groupName);
            TradeMqConsumerLog mqConsumerLog = tradeMqConsumerLogMapper.selectByPrimaryKey(primarykey);
            if(mqConsumerLog==null){
                //数据库未有记录
                mqConsumerLog = new TradeMqConsumerLog();
                mqConsumerLog.setMsgTag(tags);
                mqConsumerLog.setMsgKey(keys);
                mqConsumerLog.setGroupName(groupName);
                mqConsumerLog.setConsumerStatus(ShopCode.SHOP_MQ_MESSAGE_STATUS_FAIL.getCode());
                mqConsumerLog.setMsgBody(body);
                mqConsumerLog.setMsgId(msgId);
                mqConsumerLog.setConsumerTimes(1);
                tradeMqConsumerLogMapper.insert(mqConsumerLog);
            }else{
                mqConsumerLog.setConsumerTimes(mqConsumerLog.getConsumerTimes()+1);
                tradeMqConsumerLogMapper.updateByPrimaryKeySelective(mqConsumerLog);
            }
        }
    }
}
