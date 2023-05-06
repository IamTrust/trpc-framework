package org.trpc.framework.core.serialize.jdk;

import org.trpc.framework.core.serialize.SerializeFactory;

import java.io.*;

/**
 * JDK 自带的序列化技术
 *
 * @author Trust会长
 * @Date 2023/5/6
 */
public class JDKSerializeFactory implements SerializeFactory {
    @Override
    public <T> byte[] serialize(T t) {
        byte[] data = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream output = new ObjectOutputStream(os);
            output.writeObject(t);
            output.flush();
            output.close();
            data = os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        try {
            ObjectInputStream input = new ObjectInputStream(is);
            Object result = input.readObject();
            return ((T) result);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
