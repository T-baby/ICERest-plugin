package com.cybermkd.plugin.zbus.coder;

import org.zbus.net.http.Message;

public interface Coder {
    /**
     * @param obj Object对象
     * @return Message消息
     * @Title: encode
     * @Description: 将一个Object对象转化为一个Message对象
     * @since V1.0.0
     */
    public Message encode(Object obj);

    /**
     * @param tClass 类型
     * @param msg    Message消息
     * @return Object对象
     * @throws Exception
     * @Title: decode
     * @Description: 将一个Message对象依据类型转化成一个Object对象
     * @since V1.0.0
     */
    public Object decode(Class<?> tClass, Message msg) throws Exception;
}
