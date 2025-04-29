package org.mvplugins.multiverse.core;

/**
 * A utility class that enables automated tests to flag Multiverse for testing. This allows Multiverse to not perform
 * certain behaviors such as enabled stats uploads.
 */
final class TestingMode {

    private static boolean enabled = false;

    private TestingMode() {
        // No instantiation
    }

    static void enable() {
        enabled = true;
    }

    static boolean isDisabled() {
        return !enabled;
    }
}
