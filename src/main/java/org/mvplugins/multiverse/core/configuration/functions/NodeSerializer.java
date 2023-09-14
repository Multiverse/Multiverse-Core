package org.mvplugins.multiverse.core.configuration.functions;

public interface NodeSerializer<T> {
    T deserialize(Object object, Class<T> type);
    Object serialize(T object, Class<T> type);
}
