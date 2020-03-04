package com.athenan.demo.counsumer;

import com.alibaba.fastjson.JSON;
import com.atnanjing.demo.dao.Student;
import com.atnanjing.demo.dao.UserDO;
import com.atnanjing.demo.mapper.StudentMapper;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class MQConsumeMsgListenerProcessor implements MessageListenerConcurrently {

    private static final Logger logger = LoggerFactory.getLogger(MQConsumeMsgListenerProcessor.class);

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        if(CollectionUtils.isEmpty(msgs)){
            logger.info("接收到的消息为空，不做任何处理");
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
        for (MessageExt messageExt:msgs ) {
            String msg=new String(messageExt.getBody());
            logger.info("接收到的消息是："+msg);
            if(messageExt.getTopic().equals("DemoTopic")){
                if(messageExt.getTags().equals("DemoTag")){
                    int reconsumeTimes=messageExt.getReconsumeTimes();
                    if(reconsumeTimes == 3){
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    //以下为业务处理代码
                    UserDO userDO=JSON.parseObject(msg,UserDO.class);
                    Student student=new Student();
                    student.setId(userDO.getId());
                    student.setAge(userDO.getAge());
                    student.setName(userDO.getName());
                    student.setSex(userDO.getSex().toString());
                    logger.info("接收到的消息为Student"+student);



                }

            }

        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
