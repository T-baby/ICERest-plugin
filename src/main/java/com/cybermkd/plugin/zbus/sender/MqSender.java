package com.cybermkd.plugin.zbus.sender;

import org.zbus.mq.Protocol.MqMode;

public class MqSender<T> extends AbstractSender<T> {

    /**
     * <p>
     * Title: Sender
     * </p>
     * <p>
     * Description: 构建一个MQ发送器
     * </p>
     *
     * @param mq MQ队列名
     * @since V1.0.0
     */
    public MqSender(String mq) {
        super(mq, MqMode.MQ);
    }
}
