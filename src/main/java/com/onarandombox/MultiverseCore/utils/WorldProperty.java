/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.utils;

/**
 * Multiverse 2
 * Currently Unused
 */

public class WorldProperty<T> {
    private Class<T> type;
    private Object value;

    public WorldProperty(Class<T> type, Object two, String propertyLocation) {
        this.type = type;
        this.value = two;
    }

    public Class<T> getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean setValue(Object value) {
        if (value.getClass().equals(this.type)) {
            this.value = value;
            return true;
        }
        return false;
    }
}
