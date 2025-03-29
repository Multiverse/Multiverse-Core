package org.mvplugins.multiverse.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class REPatterns {

    private static final Map<String, Pattern> patternCache = new HashMap<>();

    public static Pattern get(String regex) {
        return patternCache.computeIfAbsent(regex, Pattern::compile);
    }

    public static Pattern COLON = get(":");
    public static Pattern COMMA = get(",");
    public static Pattern DOT = get("\\.");
    public static Pattern EQUALS = get("=");
    public static Pattern SEMICOLON = get(";");
    public static Pattern UNDERSCORE = get("_");
}
