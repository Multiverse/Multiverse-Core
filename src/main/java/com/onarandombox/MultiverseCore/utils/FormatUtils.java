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
        if (VersionUtils.getServerVersion().isHigherThanOrEqualTo(VersionUtils.v1_16_1_R01)) {
            // Parse rgb color codes
            return text.replaceAll(REPLACE_RGB_PATTERN, "§x§$2§$3§$4§$5§$6§$7");
        }
        Logging.finer("RGB Colour is not parse as your server does not support it!");
        return text;
    }

}
