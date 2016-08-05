package com.cybermkd.plugin.zbus.sender;

import com.cybermkd.plugin.zbus.Topic;
import org.zbus.mq.Protocol.MqMode;
import org.zbus.net.http.Message;

public class TopicSender<T> extends AbstractSender<T> {

    /**
     * 主题
     */
    private final String topic;

    /**
     * <p>
     * Title: TopicSender
     * </p>
     * <p>
     * Description: 构建一个Topic发送器
     * </p>
     *
     * @param topic Topic对象
     * @since V1.0.0
     */
    public TopicSender(final Topic topic) {
        this(topic.getMqName(), topic.getTopicName());
    }

    /**
     * <p>
     * Title: TopicSender
     * </p>
     * <p>
     * Description: 构建一个Topic发送器
     * </p>
     *
     * @param mq    MQ队列名
     * @param topic 主题名
     * @since V1.0.0
     */
    public TopicSender(String mq, String topic) {
        super(mq, MqMode.PubSub);
        this.topic = topic;
    }


    @Override
    protected Message encode(T obj) {
        //设定topic
        return super.encode(obj).setTopic(this.topic);
    }
}
