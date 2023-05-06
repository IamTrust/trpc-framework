package org.trpc.framework.core.serialize.fastjson;

import com.alibaba.fastjson.JSON;
import org.trpc.framework.core.serialize.SerializeFactory;

/**
 * FastJson 序列化方式
 *
 * @author Trust会长
 * @Date 2023/5/6
 */
public class FastJsonSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        String jsonStr = JSON.toJSONString(t);
        return jsonStr.getBytes();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return JSON.parseObject(new String(bytes), clazz);
    }
}
