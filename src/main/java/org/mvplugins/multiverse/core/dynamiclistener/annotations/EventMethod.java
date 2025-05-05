package org.mvplugins.multiverse.core.dynamiclistener.annotations;

import org.bukkit.event.EventHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a method as an event method. Similar to {@link EventHandler}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventMethod {
}
