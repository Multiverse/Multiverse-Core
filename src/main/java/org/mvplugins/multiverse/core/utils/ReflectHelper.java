package org.mvplugins.multiverse.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.vavr.control.Try;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class used to help in doing various reflection actions.
 */
public final class ReflectHelper {

    /**
     * Try to get the {@link Class} based on its classpath.
     *
     * @param classPath The target classpath.
     * @return A {@link Try} containing the {@link Class} if found, else a failure.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    public static Try<Class<?>> tryGetClass(@NotNull String classPath) {
        return Try.of(() -> Class.forName(classPath));
    }

    /**
     * Check if the {@link Class} for a give classpath is present/valid.
     *
     * @param classPath Target classpath.
     * @return True if class path is a valid class, else false.
     */
    public static boolean hasClass(String classPath) {
        return tryGetClass(classPath).isSuccess();
    }

    /**
     * Try to get a {@link Method} from a given class.
     *
     * @param clazz             The class to search the method on.
     * @param methodName        Name of the method to get.
     * @param parameterTypes    Parameters present for that method.
     * @param <C>               The class type.
     * @return A {@link Try} containing the {@link Method} if found, else a failure.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    public static <C> Try<Method> tryGetMethod(@NotNull Class<C> clazz, @NotNull String methodName, Class<?>... parameterTypes) {
        return Try.of(() -> clazz.getMethod(methodName, parameterTypes))
                .orElse(Try.of(() -> {
                    Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                    method.setAccessible(true);
                    return method;
                }));
    }

    /**
     * Check if a {@link Method} exists on a given class.
     *
     * @param clazz             The class to search the method on.
     * @param methodName        Name of the method to check.
     * @param parameterTypes    Parameters present for that method.
     * @return True if method exists, else false.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static boolean hasMethod(@NotNull Class<?> clazz, @NotNull String methodName, Class<?>... parameterTypes) {
        return tryGetMethod(clazz, methodName, parameterTypes).isSuccess();
    }

    /**
     * Try to invoke a {@link Method} on a class instance.
     *
     * @param classInstance Instance of the class responsible for the method.
     * @param method        The method to invoke.
     * @param parameters    Parameters needed when invoking the method.
     * @param <C>           The class type.
     * @param <R>           The return type of the method.
     * @return A {@link Try} containing the return value of the method if found, else a failure.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    @SuppressWarnings("unchecked")
    public static <C, R> Try<R> tryInvokeMethod(@NotNull C classInstance, @NotNull Method method, Object...parameters) {
        return Try.of(() -> (R) method.invoke(classInstance, parameters));
    }

    /**
     * Try to invoke a static {@link Method}.
     *
     * @param method        The static method to invoke.
     * @param parameters    Parameters needed when invoking the method.
     * @param <R>           The return type of the method.
     * @return A {@link Try} containing the return value of the method if found, else a failure.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    @SuppressWarnings("unchecked")
    public static <R> Try<R> tryInvokeStaticMethod(@NotNull Method method, Object...parameters) {
        return Try.of(() -> (R) method.invoke(null, parameters));
    }

    /**
     * Try to get a {@link Field} from a given class.
     *
     * @param clazz     The class to search the field on.
     * @param fieldName Name of the field to get.
     * @param <C>       The class type.
     * @return A {@link Try} containing the {@link Field} if found, else a failure.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    public static <C> Try<Field> tryGetField(@NotNull Class<C> clazz, @NotNull String fieldName) {
        return Try.of(() -> clazz.getField(fieldName))
                .orElse(Try.of(() -> {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                }));
    }

    /**
     * Check if a {@link Field} exists on a given class.
     *
     * @param clazz     The class to search the field on.
     * @param fieldName Name of the field to check.
     * @return True if field exists, else false.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static boolean hasField(@NotNull Class<?> clazz, @NotNull String fieldName) {
        return tryGetField(clazz, fieldName).isSuccess();
    }

    /**
     * Try to get the value of a {@link Field} from an instance of the class responsible.
     *
     * @param classInstance Instance of the class to get the field value from.
     * @param field         The field to get the value from.
     * @param fieldType     Type of the field.
     * @param <C>           The class type.
     * @param <V>           The field value type.
     * @return A {@link Try} containing the field value if found, else a failure.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    public static <C, V> Try<V> tryGetFieldValue(@NotNull C classInstance, @NotNull Field field, @NotNull Class<V> fieldType) {
        return Try.of(() -> fieldType.cast(field.get(classInstance)));
    }

    /**
     * Try to get the value of a static {@link Field}.
     *
     * @param field     The static field to get the value from.
     * @param fieldType Type of the field.
     * @param <V>       The field value type.
     * @return A {@link Try} containing the field value if found, else a failure.
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    @NotNull
    public static <V> Try<V> tryGetStaticFieldValue(@NotNull Field field, @NotNull Class<V> fieldType) {
        return Try.of(() -> fieldType.cast(field.get(null)));
    }

    /**
     * Try to get the {@link Class} based on its classpath.
     *
     * @param classPath The target classpath.
     * @return A {@link Class} if found, else null.
     *
     * @deprecated Use {@link #tryGetClass(String)} instead, which returns a {@link Try} that can be used to handle
     * the failure case more explicitly.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @Nullable
    public static Class<?> getClass(String classPath) {
        try {
            return Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Try to get a {@link Method} from a given class.
     *
     * @param clazz             The class to search the method on.
     * @param methodName        Name of the method to get.
     * @param parameterTypes    Parameters present for that method.
     * @param <C>               The class type.
     * @return A {@link Method} if found, else null.
     *
     * @deprecated Use {@link #tryGetMethod(Class, String, Class[])} instead, which returns a {@link Try} that can be
     * used to handle the failure case more explicitly.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @Nullable
    public static <C> Method getMethod(Class<C> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Try to get a {@link Method} from a given class.
     *
     * @param classInstance     Instance of the class to search the method on.
     * @param methodName        Name of the method to get.
     * @param parameterTypes    Parameters present for that method.
     * @param <C>               The class type.
     * @return A {@link Method} if found, else null.
     *
     * @deprecated Use {@link #tryGetMethod(Class, String, Class[])} instead, which returns a {@link Try} that can be
     * used to handle the failure case more explicitly.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @Nullable
    public static <C> Method getMethod(C classInstance, String methodName, Class<?>... parameterTypes) {
        return getMethod(classInstance.getClass(), methodName, parameterTypes);
    }

    /**
     * Calls a {@link Method}.
     *
     * @param classInstance Instance of the class responsible for the method.
     * @param method        The method to call.
     * @param parameters    Parameters needed when calling the method.
     * @param <C>           The class type.
     * @param <R>           The return type.
     * @return Return value of the method call if any, else null.
     *
     * @deprecated Use {@link #tryInvokeMethod(Object, Method, Object...)} instead, which returns a {@link Try} that can
     * be used to handle the failure case more explicitly.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @Nullable
    @SuppressWarnings("unchecked")
    public static <C, R> R invokeMethod(C classInstance, Method method, Object...parameters) {
        try {
            return (R) method.invoke(classInstance, parameters);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Try to get a {@link Field} from a given class.
     *
     * @param clazz     The class to search the field on.
     * @param fieldName Name of the field to get.
     * @param <C>       The class type.
     * @return A {@link Field} if found, else null.
     *
     * @deprecated Use {@link #tryGetField(Class, String)} instead, which returns a {@link Try} that can be used to
     * handle the failure case more explicitly.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @Nullable
    public static <C> Field getField(Class<C> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * Try to get a {@link Field} from a given class.
     *
     * @param classInstance Instance of the class to search the field on.
     * @param fieldName     Name of the field to get.
     * @param <C>           The class type.
     * @return A {@link Field} if found, else null.
     *
     * @deprecated Use {@link #tryGetField(Class, String)} instead, which returns a {@link Try} that can be used to
     * handle the failure case more explicitly.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @Nullable
    public static <C> Field getField(C classInstance, String fieldName) {
        return getField(classInstance.getClass(), fieldName);
    }

    /**
     * Gets the value of a {@link Field} from an instance of the class responsible.
     *
     * @param classInstance Instance of the class to get the field value from.
     * @param field         The field to get value from.
     * @param fieldType     Type of the field.
     * @param <C>           The class type.
     * @param <V>           The field value type.
     * @return The field value if any, else null.
     *
     * @deprecated Use {@link #tryGetFieldValue(Object, Field, Class)} instead, which returns a {@link Try} that can be
     * used to handle the failure case more explicitly.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @Nullable
    public static <C, V> V getFieldValue(C classInstance, @Nullable Field field, @NotNull Class<V> fieldType) {
        try {
            if (field == null) {
                return null;
            }
            Object value = field.get(classInstance);
            return fieldType.isInstance(value) ? fieldType.cast(value) : null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Gets the value of a field name from an instance of the class responsible.
     *
     * @param classInstance Instance of the class to get the field value from.
     * @param fieldName     Name of the field to get value from.
     * @param fieldType     Type of the field.
     * @param <C>           The class type.
     * @param <V>           The field value type.
     * @return The field value if any, else null.
     *
     * @deprecated Use {@link #tryGetField(Class, String)} then map to {@link #tryGetFieldValue(Object, Field, Class)} instead,
     * which returns a {@link Try} that can be used to handle the failure case more explicitly.
     */
    @Deprecated(forRemoval = true, since = "5.7")
    @Nullable
    public static <C, V> V getFieldValue(C classInstance, @Nullable String fieldName, @NotNull Class<V> fieldType) {
        return getFieldValue(classInstance, getField(classInstance, fieldName), fieldType);
    }
}
