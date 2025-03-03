package org.mvplugins.multiverse.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
     * @return A {@link Class} if found, else null.
     */
    @Nullable
    public static Class<?> getClass(String classPath) {
        try {
            return Class.forName(classPath);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Check if the {@link Class} for a give classpath is present/valid.
     *
     * @param classPath Target classpath.
     * @return True if class path is a valid class, else false.
     */
    public static boolean hasClass(String classPath) {
        return getClass(classPath) != null;
    }

    /**
     * Try to get a {@link Method} from a given class.
     *
     * @param clazz             The class to search the method on.
     * @param methodName        Name of the method to get.
     * @param parameterTypes    Parameters present for that method.
     * @param <C>               The class type.
     * @return A {@link Method} if found, else null.
     */
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
     */
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
     */
    @Nullable
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
     */
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
     */
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
     */
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
     */
    @Nullable
    public static <C, V> V getFieldValue(C classInstance, @Nullable String fieldName, @NotNull Class<V> fieldType) {
        return getFieldValue(classInstance, getField(classInstance, fieldName), fieldType);
    }
}
