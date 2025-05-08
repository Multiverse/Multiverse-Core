package org.mvplugins.multiverse.core.dynamiclistener;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventClass;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventMethod;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.DefaultEventPriority;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventPriorityKey;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.IgnoreIfCancelled;
import org.mvplugins.multiverse.core.dynamiclistener.annotations.SkipIfEventExist;
import org.mvplugins.multiverse.core.utils.ReflectHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Registers {@link DynamicListener} to bukkit's event system.
 */
@Service
public final class DynamicListenerRegistration {

    private static final boolean hasEventExecutorCreate;

    static {
        hasEventExecutorCreate = ReflectHelper.getMethod(EventExecutor.class, "create", Method.class, Class.class) != null;
    }

    private final EventPriorityMapper eventPriorityMapper;

    @Inject
    DynamicListenerRegistration(@NotNull EventPriorityMapper eventPriorityMapper) {
        this.eventPriorityMapper = eventPriorityMapper;
    }

    /**
     * Registers a {@link DynamicListener} to bukkit's event system.
     *
     * @param listener  The listener to register
     * @param plugin    The plugin associated with the listener
     */
    public void register(@NotNull DynamicListener listener, @NotNull Plugin plugin) {
        Set<Method> listenerMethods = new HashSet<>();
        listenerMethods.addAll(List.of(listener.getClass().getMethods()));
        listenerMethods.addAll(List.of(listener.getClass().getDeclaredMethods()));
        listenerMethods.forEach(method -> {
            if (method.isAnnotationPresent(EventMethod.class)) {
                registerAsEventMethod(listener, plugin, method);
            } else if (method.isAnnotationPresent(EventClass.class)) {
                registerAsEventClass(listener, plugin, method);
            }
        });
    }

    private void registerAsEventMethod(DynamicListener listener, Plugin plugin, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1 && !Event.class.isAssignableFrom(parameterTypes[0])) {
            Logging.warning("Invalid event method %s in %s", method.getName(), listener.getClass().getName());
            return;
        }
        if (getSkipIfEventExist(method) != null) {
            return;
        }

        Class<? extends Event> eventClass = parameterTypes[0].asSubclass(Event.class);
        method.setAccessible(true);
        EventExecutor eventExecutor = createEventExecutor(method, eventClass);
        EventPriority priority = getDynamicEventPriority(method);
        boolean ignoreCancelled = isIgnoreIfCancelled(method);

        Logging.finest("Registering event listener for %s with priority %s", eventClass.getName(), priority);
        Bukkit.getPluginManager().registerEvent(eventClass, listener, priority, eventExecutor, plugin, ignoreCancelled);
    }

    private void registerAsEventClass(DynamicListener listener, Plugin plugin, Method method) {
        Class<? extends Event> eventClass = getEventClass(method);
        if (eventClass == null) {
            return;
        }
        if (getSkipIfEventExist(method) != null) {
            Logging.fine("Skipping event method %s in %s", method.getName(), listener.getClass().getName());
            return;
        }

        method.setAccessible(true);
        Object methodOutput = ReflectHelper.invokeMethod(listener, method);
        if (!(methodOutput instanceof EventRunnable eventRunnable)) {
            Logging.warning("Event method %s in %s did not return a SingleEventListener",
                    method.getName(), listener.getClass().getName());
            return;
        }
        EventExecutor executor = new EventRunnableExecutor<>(eventClass, eventRunnable);
        EventPriority priority = getDynamicEventPriority(method);
        boolean ignoreCancelled = isIgnoreIfCancelled(method);

        Logging.finest("Registering dynamic event for %s with priority %s", eventClass.getName(), priority);
        Bukkit.getPluginManager().registerEvent(eventClass, listener, priority, executor, plugin, ignoreCancelled);
    }

    private Class<? extends Event> getEventClass(Method method) {
        EventClass eventClassAnnotation = method.getAnnotation(EventClass.class);
        if (eventClassAnnotation == null) {
            return null;
        }
        return getEventClassFromString(eventClassAnnotation.value());
    }

    private Class<? extends Event> getSkipIfEventExist(Method method) {
        SkipIfEventExist skipIfEventExist = method.getAnnotation(SkipIfEventExist.class);
        if (skipIfEventExist == null) {
            return null;
        }
        return getEventClassFromString(skipIfEventExist.value());
    }

    private Class<? extends Event> getEventClassFromString(String className) {
        Class<?> annotatedClass = ReflectHelper.getClass(className);
        if (annotatedClass == null || !Event.class.isAssignableFrom(annotatedClass)) {
            // Usually means the server software used did not implement the event
            Logging.fine("Event class does not exist: %s", className);
            return null;
        }
        return annotatedClass.asSubclass(Event.class);
    }

    private EventPriority getDynamicEventPriority(Method method) {
        return Option.of(method.getAnnotation(EventPriorityKey.class))
                .flatMap(eventPriorityKey -> eventPriorityMapper.getPriority(eventPriorityKey.value()))
                .getOrElse(() -> getDefaultEventPriority(method));
    }

    private EventPriority getDefaultEventPriority(Method method) {
        DefaultEventPriority eventPriority = method.getAnnotation(DefaultEventPriority.class);
        return eventPriority == null ? EventPriority.NORMAL : eventPriority.value();
    }

    private boolean isIgnoreIfCancelled(Method method) {
        return method.isAnnotationPresent(IgnoreIfCancelled.class);
    }

    private EventExecutor createEventExecutor(Method method, Class<? extends Event> eventClass) {
        if (hasEventExecutorCreate) {
            return EventExecutor.create(method, eventClass);
        }
        return (listener, event) -> {
            try {
                if (!eventClass.isAssignableFrom(event.getClass())) {
                    return;
                }
                method.invoke(listener, event);
            } catch (InvocationTargetException ex) {
                throw new EventException(ex.getCause());
            } catch (Throwable t) {
                throw new EventException(t);
            }
        };
    }
}
