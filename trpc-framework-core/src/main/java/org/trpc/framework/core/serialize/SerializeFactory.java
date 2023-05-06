package org.trpc.framework.core.serialize;

/**
 * 序列化/反序列化的接口
 *
 * @author Trust会长
 * @Date 2023/5/6
 */
public interface SerializeFactory {

    /**
     * 序列化
     * @param t
     * @return
     * @param <T>
     */
    <T> byte[] serialize(T t);

    /**
     * 反序列化
     * @param bytes
     * @return
     * @param <T>
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
