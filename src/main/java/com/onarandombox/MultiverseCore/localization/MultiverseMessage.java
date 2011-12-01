package com.onarandombox.MultiverseCore.localization;

/**
 * An enum containing all messages/strings used by Multiverse.
 */
public enum MultiverseMessage {
    TEST_STRING("a test-string from the enum");

    private final String def;

    MultiverseMessage(String def) {
        this.def = def;
    }

    /**
     * @return This {@link MultiverseMessage}'s default-message
     */
    public String getDefault() {
        return def;
    }

}
