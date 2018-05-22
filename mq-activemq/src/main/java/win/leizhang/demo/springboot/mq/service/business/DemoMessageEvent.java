package win.leizhang.demo.springboot.mq.service.business;

import com.alibaba.fastjson.JSONException;
import com.crt.jms.mq.JmsClusterMgr;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Service
public class DemoMessageEvent implements MessageListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    JmsClusterMgr jmsClusterMgr;

    @Override
    public void onMessage(Message message) {
        TextMessage objMsg = null;
        String text;
        try {
            objMsg = (TextMessage) message;
            text = objMsg.getText();
            if (StringUtils.isBlank(text)) {
                log.warn("The message queue does not have the required parameters!");
                return;
            }

            // debug
            log.info("the text ==> {}", text);
            // 处理业务
            execute(text);
        } catch (ClassCastException cce) {
            try {
                message.acknowledge();
            } catch (JMSException jmse) {
                log.error("[ClassCastException] 不能转换成TextMessage对象。详情==> ", jmse);
            }
        } catch (Exception e) {
            log.error("发生异常，没有处理的Message写入临时表，请查看[mq_receiver]日志");
            // 异常分类
            if (e instanceof NullPointerException) {
                //log.info("[BusinessException] 详情==> {}, {}", ((BusinessServiceException) e).getCode(), e.getMessage());
            } else if (e instanceof JSONException) {
                log.error("[JsonException] 详情==> json转换错误");
            } else {
                log.error("[OtherException] 详情==> ", e);
            }

            if (null != objMsg) {
                // 签收前，写临时表
                jmsClusterMgr.writeReceiveMsgToDB(objMsg);
            }
        } finally {
            if (null != objMsg) {
                try {
                    // 最终被签收
                    objMsg.acknowledge();
                } catch (JMSException jmse) {
                    log.error("发生异常! 详情==> {}", jmse);
                }
            }
        }

    }

    private void execute(String text) {
        //GrowthAccountBO bo = JSONObject.parseObject(text, GrowthAccountBO.class);
        //Long memberId = bo.getMemberId();
        //Integer levelId = bo.getMemberLevel();

        // 业务处理和调用

        log.debug("execute finish!");
    }
}
