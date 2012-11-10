package com.mvplugin.core.minecraft;

public enum WorldEnvironment {
    NETHER,
    NORMAL,
    THE_END
    ;

    public static WorldEnvironment getFromString(final String name) {
        for (final WorldEnvironment env : WorldEnvironment.values()) {
            if (name.equalsIgnoreCase(env.toString())) {
                return env;
            }
        }
        return null;
    }
}
