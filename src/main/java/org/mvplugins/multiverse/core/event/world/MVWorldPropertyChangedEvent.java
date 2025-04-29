/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package org.mvplugins.multiverse.core.event.world;

import org.bukkit.event.HandlerList;

import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * This event is fired after the property is changed.
 * <p>
 * To get the name of the property that was changed, use {@link #getName()}.
 * To get the old value, use {@link #getOldValue()}.
 * To get the new value, use {@link #getNewValue()}.
 *
 * @param <T> The type of the property that was set.
 */
public final class MVWorldPropertyChangedEvent<T> extends MultiverseWorldEvent<MultiverseWorld> {
    private static final HandlerList HANDLERS = new HandlerList();

    private final String name;
    private final T oldValue;
    private final T newValue;

    public MVWorldPropertyChangedEvent(@NotNull MultiverseWorld world, @NotNull String name, T oldValue, T value) {
        super(world);
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = value;
    }

    /**
     * Gets the changed world property's name.
     *
     * @return The changed world property's name.
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Gets the old value.
     *
     * @return The old value.
     */
    public T getOldValue() {
        return oldValue;
    }

    /**
     * Gets the new value.
     *
     * @return The new value.
     */
    public T getNewValue() {
        return this.newValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the handler list. This is required by the event system.
     *
     * @return A list of HANDLERS.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
