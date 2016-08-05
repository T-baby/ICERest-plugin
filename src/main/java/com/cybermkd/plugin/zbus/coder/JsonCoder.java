package com.cybermkd.plugin.zbus.coder;

import com.alibaba.fastjson.JSON;
import org.zbus.net.http.Message;

public class JsonCoder implements Coder {

    @Override
    public Message encode(Object obj) {

        Message msg = new Message();

        msg.setBody(JSON.toJSONString(obj));

        return msg;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object decode(Class<?> tClass, Message msg) throws Exception {

        String textMsg = msg.getBodyString();

        Object obj = JSON.parseObject(textMsg, tClass);

        return obj;
    }
}
