package com.onarandombox.MultiverseCore.displaytools;

/**
 * Represents a setting option that can be used by {@link DisplayHandler}.
 *
 * @param <T>
 */
@FunctionalInterface
public interface DisplaySetting<T> {

     /**
      * Gets the default value of this Display Setting.
      *
      * @return The default value.
      */
     T defaultValue();
}
