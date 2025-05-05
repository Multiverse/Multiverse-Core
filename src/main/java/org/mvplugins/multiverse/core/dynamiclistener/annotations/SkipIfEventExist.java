package org.mvplugins.multiverse.core.dynamiclistener.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Do not register the event if the following event class exists on the server. This is useful for fallback events
 * if another event class is not available on the server.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SkipIfEventExist {
    String value();
}
