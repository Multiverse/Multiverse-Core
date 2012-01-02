package com.onarandombox.MultiverseCore.localization;

/**
 * An enum containing all messages/strings used by Multiverse.
 */
public enum MultiverseMessage {
    TEST_STRING("a test-string from the enum"),

    // Generic Strings
    GENERIC_SORRY("Sorry..."),
    GENERIC_PAGE("Page"),
    GENERIC_OF("of"),
    GENERIC_UNLOADED("UNLOADED"),
    GENERIC_PLUGIN_DISABLED("This plugin is Disabled!"),

    // Errors
    ERROR_LOAD("Your configs were not loaded. Very little will function in Multiverse."),

    //// Commands
    // List Command
    LIST_NAME("World Listing"),
    LIST_DESC("Displays a listing of all worlds that you can enter."),
    LIST_TITLE("Multiverse World List"),
    LIST_NO_MATCH("No worlds matched your filter:");

    private final List<String> def;

    MultiverseMessage(String def, String... extra) {
        this.def = new ArrayList<String>();
        this.def.add(def);
        this.def.addAll(Arrays.asList(extra));
    }

    /**
     * @return This {@link MultiverseMessage}'s default-message
     */
    public List<String> getDefault() {
        return def;
    }

}
