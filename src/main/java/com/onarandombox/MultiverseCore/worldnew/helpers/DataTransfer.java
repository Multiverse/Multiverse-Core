package com.onarandombox.MultiverseCore.worldnew.helpers;

import java.util.ArrayList;
import java.util.List;

public class DataTransfer<T> {
    private final List<DataStore<T>> dataStores;

    public DataTransfer() {
        this.dataStores = new ArrayList<>();
    }

    public DataTransfer<T> addDataStore(DataStore<T> dataStore, T object) {
        this.dataStores.add(dataStore.copyFrom(object));
        return this;
    }

    public DataTransfer<T> pasteAllTo(T object) {
        this.dataStores.forEach(dataStore -> dataStore.pasteTo(object));
        return this;
    }
}
