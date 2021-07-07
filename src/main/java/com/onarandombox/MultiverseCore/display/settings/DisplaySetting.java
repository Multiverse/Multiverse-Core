package com.onarandombox.MultiverseCore.display.settings;

import com.onarandombox.MultiverseCore.display.DisplayHandler;

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
