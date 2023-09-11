package com.onarandombox.MultiverseCore.configuration.node;

public class EnumNodeSerializer<T extends Enum<T>> implements NodeSerializer<T> {

    @Override
    public T deserialize(Object object, Class<T> type) {
        return Enum.valueOf(type, object.toString().toUpperCase());
    }

    @Override
    public Object serialize(T object, Class<T> type) {
        return object.toString();
    }
}
