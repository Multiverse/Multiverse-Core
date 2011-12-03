package com.onarandombox.MultiverseCore.localization;

/**
 * An enum containing all messages/strings used by Multiverse.
 */
public enum MultiverseMessage {
    TEST_STRING("a test-string from the enum"),
    LIST_NAME("World Listing"),
    LIST_TITLE("Multiverse World List"),
    LIST_DESC("Displays a listing of all worlds that you can enter."),
    LIST_NO_MATCH("No worlds matched your filter:"),
    GENERIC_SORRY("Sorry..."),
    GENERIC_OF("of"),
    GENERIC_PAGE("Page"),
    GENERIC_UNLOADED("UNLOADED");

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
