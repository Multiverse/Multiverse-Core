/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.event;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is fired *before* the property is actually changed.
 * <p>
 * If it is cancelled, no change will happen.
 * <p>
 * If you want to get the values of the world before the change, query the world.
 * To get the name of the property that was changed, use {@link #getPropertyName()}.
 * To get the new value, use {@link #getTheNewValue()}. To change it, use {@link #setTheNewValue(Object)}.
 * @param <T> The type of the property that was set.
 */
public class MVWorldPropertyChangeEvent<T> extends Event implements Cancellable {
    private MultiverseWorld world;
    private CommandSender changer;
    private boolean isCancelled = false;
    private String name;
    private T value;

    public MVWorldPropertyChangeEvent(MultiverseWorld world, CommandSender changer, String name, T value) {
        this.world = world;
        this.changer = changer;
        this.name = name;
        this.value = value;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the handler list. This is required by the event system.
     * @return A list of handlers.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Gets the changed world property's name.
     * @return The changed world property's name.
     */
    public String getPropertyName() {
        return this.name;
    }

    /**
     * Gets the new value.
     * @return The new value.
     * @deprecated Use {@link #getTheNewValue()} instead.
     */
    @Deprecated
    public String getNewValue() {
        return this.value.toString();
    }

    /**
     * Gets the new value.
     * @return The new value.
     */
    public T getTheNewValue() {
        return this.value;
    }

    /**
     * Sets the new value.
     * <p>
     * This method is only a stub, it'll <b>always</b> throw an {@link UnsupportedOperationException}!
     * @param value The new new value.
     * @deprecated Use {@link #setTheNewValue(Object)} instead.
     */
    @Deprecated
    public void setNewValue(String value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the new value.
     * @param value The new value.
     */
    public void setTheNewValue(T value) {
        this.value = value;
    }

    /**
     * Get the world targeted because of this change.
     *
     * @return A valid MultiverseWorld.
     */
    public MultiverseWorld getWorld() {
        return this.world;
    }

    /**
     * Gets the person (or console) who was responsible for the change.
     * <p>
     * This may be null!
     *
     * @return The person (or console) who was responsible for the change.
     */
    public CommandSender getCommandSender() {
        return this.changer;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }
}
