package com.onarandombox.MultiverseCore.inject.registration;

import org.bukkit.event.Listener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that can be used to prevent a class that would normally be automatically registered from being
 * registered automatically.
 * <br/>
 * For example, any {@link Listener} would normally be registered automatically, but if it is annotated with
 * {@code @DoNotRegister}, it will not be registered automatically.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface DoNotRegister { }
