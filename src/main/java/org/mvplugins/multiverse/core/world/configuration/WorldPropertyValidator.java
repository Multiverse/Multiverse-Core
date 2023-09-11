package org.mvplugins.multiverse.core.world.configuration;

import me.main__.util.SerializationConfig.ChangeDeniedException;
import me.main__.util.SerializationConfig.ObjectUsingValidator;
import org.bukkit.Bukkit;
import org.mvplugins.multiverse.core.event.MVWorldPropertyChangeEvent;
import org.mvplugins.multiverse.core.world.SimpleMVWorld;

/**
 * Validates world-property-changes.
 * @param <T> The type of the property that should be validated.
 */
public class WorldPropertyValidator<T> extends ObjectUsingValidator<T, SimpleMVWorld> {
    /**
     * {@inheritDoc}
     */
    @Override
    public T validateChange(String property, T newValue, T oldValue, SimpleMVWorld object) throws ChangeDeniedException {
        MVWorldPropertyChangeEvent<T> event = new MVWorldPropertyChangeEvent<T>(object, null, property, newValue);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            throw new ChangeDeniedException();
        return event.getTheNewValue();
    }
}
