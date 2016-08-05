package com.cybermkd.plugin.zbus.handler;

import com.cybermkd.plugin.zbus.coder.Coder;
import com.cybermkd.plugin.zbus.coder.JsonCoder;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;
import org.zbus.net.http.Message.MessageHandler;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TMsgHandler<T> implements MessageHandler {

    /**
     * 范型类型
     */
    private final Class<?> tClass;

    /**
     * 编码解码器
     */
    private static final Coder coder = new JsonCoder();

    /**
     * <p>
     * Title: TMsgHandler
     * </p>
     * <p>
     * Description: 构造函数
     * </p>
     *
     * @since V1.0.0
     */
    public TMsgHandler() {
        tClass = getSuperClassGenricType();
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void handle(Message msg, Session session) throws IOException {
        Object obj;
        try {
            obj = coder.decode(tClass, msg);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        this.handle((T) obj);
    }

    /**
     * @param msg 收到的消息
     * @Title: handle
     * @Description: 消费者收到消息后的处理函数，子类需实现此方法
     * @since V1.0.0
     */
    public abstract void handle(T msg);

    @SuppressWarnings("rawtypes")
    private Class getSuperClassGenricType() {
        Class<?> clazz = getClass();
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            throw new RuntimeException(clazz.getSimpleName() + "'s superclass not ParameterizedType");
        }
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (!(params[0] instanceof Class)) {
            throw new RuntimeException(
                    clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
        }
        return (Class<?>) params[0];
    }
}
