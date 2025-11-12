package com.rakesh.finflow.common.kafka.util;

import java.io.*;

public class SerializationUtils {

    public static <T extends Serializable> byte[] serialize(T obj) {
        try {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(obj);

                return byteArrayOutputStream.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static <T extends Serializable> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                 ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

                return clazz.cast(objectInputStream.readObject());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Object deserialize(byte[] bytes) {
        try {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                 ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {

                return objectInputStream.readObject();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
