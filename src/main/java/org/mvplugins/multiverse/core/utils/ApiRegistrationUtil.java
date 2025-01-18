package org.mvplugins.multiverse.core.utils;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.api.MultiverseCoreApi;
import org.mvplugins.multiverse.core.api.ServiceProvider;

import java.lang.reflect.Method;

public class ApiRegistrationUtil {

    private static final Method INIT;
    private static final Method SHUTDOWN;


    static {
        try {
            INIT = MultiverseCoreApi.class.getDeclaredMethod("init", ServiceProvider.class);
            INIT.setAccessible(true);

            SHUTDOWN = MultiverseCoreApi.class.getDeclaredMethod("shutdown");
            SHUTDOWN.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void init(MultiverseCore plugin) {
        try {
            INIT.invoke(null, plugin.getServiceLocator());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void shutdown() {
        try {
            SHUTDOWN.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
