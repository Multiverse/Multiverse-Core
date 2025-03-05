package org.mvplugins.multiverse.core.world.helpers;

import java.util.ArrayList;
import java.util.List;

/**
 * A data transfer for storing and restoring data from multiple {@link DataStore} from one object to another.
 *
 * @param <T>   The type of the object to transfer data from and to.
 */
public final class DataTransfer<T> {
    private final List<DataStore<T>> dataStores;

    /**
     * Creates a new {@link DataTransfer} instance.
     */
    public DataTransfer() {
        this.dataStores = new ArrayList<>();
    }

    /**
     * Adds a {@link DataStore} to this {@link DataTransfer} instance.
     *
     * @param dataStore The {@link DataStore} to add.
     * @param object    The object to copy data from.
     * @return This {@link DataTransfer} instance.
     */
    public DataTransfer<T> addDataStore(DataStore<T> dataStore, T object) {
        this.dataStores.add(dataStore.copyFrom(object));
        return this;
    }

    /**
     * Copies the data from all {@link DataStore} instances in this {@link DataTransfer} instance to the given object.
     *
     * @param object    The object to paste data to.
     * @return This {@link DataTransfer} instance.
     */
    public DataTransfer<T> pasteAllTo(T object) {
        this.dataStores.forEach(dataStore -> dataStore.pasteTo(object));
        return this;
    }
}
