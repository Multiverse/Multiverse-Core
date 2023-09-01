package com.onarandombox.MultiverseCore.configuration.node;

public interface NodeSerializer<T> {
    T deserialize(Object object, Class<T> type);
    Object serialize(T object, Class<T> type);
}
