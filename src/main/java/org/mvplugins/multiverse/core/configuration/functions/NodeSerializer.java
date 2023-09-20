package org.mvplugins.multiverse.core.configuration.functions;

/**
 * A function that serializes and deserializes objects to and from YAML.
 *
 * @param <T>   The type of the object to serialize and deserialize.
 */
public interface NodeSerializer<T> {
    /**
     * Deserializes an object from YAML.
     *
     * @param object    The object to deserialize.
     * @param type      The type of the object to deserialize.
     * @return The deserialized typed value.
     */
    T deserialize(Object object, Class<T> type);

    /**
     * Serializes an object to YAML.
     *
     * @param object    The object to serialize.
     * @param type      The type of the object to serialize.
     * @return The serialized object.
     */
    Object serialize(T object, Class<T> type);
}
