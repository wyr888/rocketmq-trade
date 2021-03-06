package com.pan.trade.user.mq.processor;

import com.alibaba.fastjson.JSON;
import com.pan.trade.common.protocol.mq.CancelOrderMQ;
import com.pan.trade.common.protocol.user.ChangeUserMoneyReq;
import com.pan.trade.common.rocketmq.IMessageProcessor;
import com.pan.trade.common.tenum.TradeEnum;
import com.pan.trade.user.service.IUserService;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

/**
 * Created by Loren on 2018/5/16.
 */
public class CancelOrderProcessor implements IMessageProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CancelOrderProcessor.class);

    @Autowired
    private IUserService userService;

    public boolean handleMessage(MessageExt messageExt) {
        try {
            String body = new String(messageExt.getBody(),"UTF-8");
            String msgId = messageExt.getMsgId();
            String tags = messageExt.getTags();
            String keys = messageExt.getKeys();
            CancelOrderMQ cancelOrderMQ = JSON.parseObject(body,CancelOrderMQ.class);
            logger.info("user CancelOrdermessage:"+body);
            if (cancelOrderMQ.getUserMoney() !=null && cancelOrderMQ.getUserMoney().compareTo(BigDecimal.ZERO) == 1){
                ChangeUserMoneyReq changeUserMoneyReq = new ChangeUserMoneyReq();
                changeUserMoneyReq.setUserId(cancelOrderMQ.getUserId());
                changeUserMoneyReq.setMoneyLogType(Integer.valueOf(TradeEnum.UserMoneyLogTypeStatusEnum.RETURN.getCode()));
                changeUserMoneyReq.setUserMoney(cancelOrderMQ.getUserMoney());
                userService.changeUserMoney(changeUserMoneyReq);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
