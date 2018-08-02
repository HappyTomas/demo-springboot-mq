package win.leizhang.demo.mq.rabbitmq.service.bo;

import lombok.Data;

import java.util.Date;

/**
 * Created by zealous on 2018/5/9.
 */
@Data
public class MessageBO {

    //id
    private Long id;
    //消息
    private String msg;
    //时间戳
    private Date sendTime;

}
