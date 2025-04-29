package org.mvplugins.multiverse.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class REPatterns {

    private static final Map<String, Pattern> patternCache = new HashMap<>();

    public static Pattern get(String regex) {
        return patternCache.computeIfAbsent(regex, Pattern::compile);
    }

    public static final Pattern COLON = get(":");
    public static final Pattern COMMA = get(",");
    public static final Pattern DOT = get("\\.");
    public static final Pattern EQUALS = get("=");
    public static final Pattern SEMICOLON = get(";");
    public static final Pattern UNDERSCORE = get("_");
    public static final Pattern UUID = get("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
}
