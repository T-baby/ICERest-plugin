package com.cybermkd.plugin.zbus.sender;

import com.cybermkd.plugin.zbus.ZbusPlugin;
import com.cybermkd.plugin.zbus.coder.Coder;
import com.cybermkd.plugin.zbus.coder.JsonCoder;
import org.zbus.mq.Producer;
import org.zbus.mq.Protocol.MqMode;
import org.zbus.net.http.Message;

import java.io.IOException;

abstract class AbstractSender<T> implements Sender<T> {
    /**
     * 生产者
     */
    private Producer producer;

    /**
     * mq名称
     */
    private final String mq;

    /**
     * mq类型
     */
    private final MqMode mqMode;

    /**
     * 编码解码器
     */
    private static final Coder coder = new JsonCoder();

    /**
     * <p>Title: AbstractSender</p>
     * <p>Description: 默认构造函数</p>
     *
     * @param mq     MQ队列名
     * @param mqMode MQ队列类型
     * @since V1.0.0
     */
    public AbstractSender(String mq, MqMode mqMode) {
        this.mq = mq;
        this.mqMode = mqMode;
    }

    /**
     * @throws IOException
     * @throws InterruptedException
     * @Title: ensureProducer
     * @Description: 确保生产者使用前被创建
     * @since V1.0.0
     */
    private void ensureProducer() throws IOException, InterruptedException {
        if (this.producer == null) {
            synchronized (this) {
                if (this.producer == null) {
                    //创建生产者
                    producer = ZbusPlugin.createProducer(this, mq, mqMode);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        //将producer重新设定为null，重新获取producer对象
        producer = null;
    }

    /**
     * @param obj 发送对象
     * @throws IOException
     * @throws InterruptedException
     * @Title: sendSync
     * @Description: 发送对象到MQ／topic（同步方式）
     * @since V1.0.0
     */
    @Override
    public void sendSync(T obj) throws IOException, InterruptedException {
        ensureProducer();
        producer.sendSync(encode(obj));
    }

    /**
     * @param obj 发送对象
     * @throws IOException
     * @throws InterruptedException
     * @Title: sendAsync
     * @Description: 发送对象到MQ／topic（异步方式）
     * @since V1.0.0
     */
    @Override
    public void sendAsync(T obj) throws IOException, InterruptedException {
        ensureProducer();
        producer.sendAsync(encode(obj));
    }

    /**
     * @param obj
     * @return
     * @Title: encode
     * @Description: 默认编码，子类可重载
     * @since V1.0.0
     */
    protected Message encode(T obj) {
        return coder.encode(obj);
    }
}
