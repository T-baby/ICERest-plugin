package com.cybermkd.plugin.zbus.sender;

import java.io.Closeable;
import java.io.IOException;


public interface Sender<T> extends Closeable {
    /**
     * @param obj 发送对象
     * @throws IOException
     * @throws InterruptedException
     * @Title: sendSync
     * @Description: 发送对象到MQ／topic（同步方式）
     * @since V1.0.0
     */
    public void sendSync(T obj) throws IOException, InterruptedException;

    /**
     * @param obj 发送对象
     * @throws IOException
     * @throws InterruptedException
     * @Title: sendAsync
     * @Description: 发送对象到MQ／topic（异步方式）
     * @since V1.0.0
     */
    public void sendAsync(T obj) throws IOException, InterruptedException;

}
