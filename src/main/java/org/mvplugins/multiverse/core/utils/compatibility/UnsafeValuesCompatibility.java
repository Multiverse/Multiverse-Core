package org.mvplugins.multiverse.core.utils.compatibility;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.UnsafeValues;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.utils.ReflectHelper;

import java.lang.reflect.Method;

/**
 * Compatibility class used to handle API changes in {@link UnsafeValues} class.
 *
 * @since 5.6
 */
@ApiStatus.AvailableSince("5.7")
public final class UnsafeValuesCompatibility {

    private static final Try<Method> GET_MAIN_LEVEL_NAME_METHOD;

    static {
        GET_MAIN_LEVEL_NAME_METHOD = ReflectHelper.tryGetMethod(UnsafeValues.class, "getMainLevelName");
    }

    /**
     * Try to call the getMainLevelName method in UnsafeValues, which is used to get level-name configured in
     * server.properties file.
     *
     * @return The level-name configured in server.properties file, or empty if the method is not available.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static Option<String> getMainLevelName() {
        return GET_MAIN_LEVEL_NAME_METHOD
                .flatMap(method -> ReflectHelper.tryInvokeMethod(Bukkit.getUnsafe(), method))
                .map(String.class::cast)
                .toOption();
    }

    private UnsafeValuesCompatibility() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
