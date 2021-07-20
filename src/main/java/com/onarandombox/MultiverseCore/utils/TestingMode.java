package com.onarandombox.MultiverseCore.utils;

/**
 * A utility class that enables automated tests to flag Multiverse for testing. This allows Multiverse to not perform
 * certain behaviors such as enabled stats uploads.
 */
public class TestingMode {

    private static boolean enabled = false;

    public static void enable() {
        enabled = true;
    }

    public static boolean isDisabled() {
        return !enabled;
    }
}
