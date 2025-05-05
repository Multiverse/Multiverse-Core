package org.mvplugins.multiverse.core.dynamiclistener.annotations;

import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.dynamiclistener.EventPriorityMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The event priority to use for the event if {@link EventPriorityKey} is not set in {@link EventPriorityMapper}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultEventPriority {
    @NotNull EventPriority value();
}
