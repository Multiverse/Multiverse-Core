package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;

/**
 * String formatting utilities
 */
public class FormatUtils {

    final static String REPLACE_COLOR_PATTERN = "(&)?&([0-9a-fk-orA-FK-OR])";
    final static String REPLACE_RGB_PATTERN = "(&)?&#([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])";

    public static String parseColors(String text) {
        return(FormatUtils.parseLegacyColor(FormatUtils.parseRGBColor(text)));
    }

    public static String parseLegacyColor(String text) {
        // normal code code
        return text.replaceAll(REPLACE_COLOR_PATTERN, "§$2");
    }

    public static String parseRGBColor(String text) {
        // Parse rgb color codes
        return text.replaceAll(REPLACE_RGB_PATTERN, "§x§$2§$3§$4§$5§$6§$7");
    }

    public static String removeColors(String text) {
        return(FormatUtils.removeLegacyColor((FormatUtils.removeRGBColor(text))));
    }

    public static String removeLegacyColor(String text) {
        // normal code code
        return text.replaceAll(REPLACE_COLOR_PATTERN, "");
    }

    public static String removeRGBColor(String text) {
        // Parse rgb color codes
        return text.replaceAll(REPLACE_RGB_PATTERN, "");
    }

}
