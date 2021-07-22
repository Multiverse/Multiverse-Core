package com.onarandombox.MultiverseCore.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EnumMapping<T extends Enum<T>> {

    public static <T extends Enum<T>> Builder<T> builder(Class<T> enumClass) {
        return builder(enumClass, false);
    }

    public static <T extends Enum<T>> Builder<T> builder(Class<T> enumClass, boolean caseSensitive) {
        return new Builder<>(enumClass, caseSensitive);
    }

    private final Class<T> enumClass;
    private final boolean caseSensitive;
    private final Map<String, T> enumsMap;

    public EnumMapping(Class<T> enumClass, boolean caseSensitive) {
        this.enumClass = enumClass;
        this.caseSensitive = caseSensitive;
        this.enumsMap = new HashMap<>();
    }

    @NotNull
    public Optional<T> parseValue(@Nullable String value) {
        if (value == null) {
            return Optional.empty();
        }

        T enumType = this.enumsMap.get(caseSensitive ? value : value.toLowerCase());
        if (enumType != null) {
            return Optional.of(enumType);
        }

        try {
            return Optional.of(Enum.valueOf(enumClass, value));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public Map<String, T> getEnumsMap() {
        return Collections.unmodifiableMap(this.enumsMap);
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public Class<? extends Enum<T>> getEnumClass() {
        return enumClass;
    }

    public static class Builder<T extends Enum<T>> {

        private final EnumMapping<T> enumMapping;

        public Builder(Class<T> enumClass, boolean caseSensitive) {
            this.enumMapping = new EnumMapping<>(enumClass, caseSensitive);
            if (!this.enumMapping.caseSensitive) {
                addLowerCaseMap();
            }
        }

        private void addLowerCaseMap() {
            Arrays.stream(this.enumMapping.enumClass.getEnumConstants()).forEach(enumType -> {
                addAlias(enumType.name(), enumType);
            });
        }

        public Builder<T> mapWithoutUnderscore() {
            Arrays.stream(this.enumMapping.enumClass.getEnumConstants()).forEach(enumType -> {
                addAlias(enumType.name().replace("_", ""), enumType);
            });
            return this;
        }

        @NotNull
        public Builder<T> addAlias(String aliasName, String enumString) {
            this.enumMapping.parseValue(enumString).ifPresent(enumType -> addAlias(aliasName, enumType));
            return this;
        }

        @NotNull
        public Builder<T> addAlias(String aliasName, T enumType) {
            if (!this.enumMapping.caseSensitive) {
                aliasName = aliasName.toLowerCase();
            }
            this.enumMapping.enumsMap.computeIfAbsent(aliasName, name -> enumType);
            return this;
        }

        @NotNull
        public EnumMapping<T> build() {
            return this.enumMapping;
        }
    }
}
