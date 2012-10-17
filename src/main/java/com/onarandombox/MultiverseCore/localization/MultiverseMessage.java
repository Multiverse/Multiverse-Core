package com.onarandombox.MultiverseCore.localization;


/**
 * An enum containing all messages/strings used by Multiverse.
 */
public enum MultiverseMessage {
    // BEGIN CHECKSTYLE-SUPPRESSION: JavadocVariable
    TEST_STRING("a test-string from the enum"),

    //// Command handling error messages
    CH_PLUGIN_DISABLED("This plugin is disabled!"),
    CH_INTERNAL_ERROR("&cAn internal error occurred when attempting to perform this command."),
    CH_ADMIN_DEBUG("&cDetails were printed to the server console and logs, please add that to your bug report."),
    CH_USER_DEBUG("&cTry again and contact the server owner or an admin if this problem persists."),

    //// Not MV world message
    GENERIC_NOT_MV_WORLD("Multiverse doesn't know about &3%s&f yet.\nType &3/mv import ?&f for help!"),

    //// Commands
    // List Command
    CMD_LIST_NAME("World Listing"),
    CMD_LIST_DESC("Displays a listing of all worlds that you can enter."),
    CMD_LIST_TITLE("Multiverse World List"),
    CMD_LIST_NO_MATCH("No worlds matched your filter:"),

    //// Listeners
    // PlayerListener
    LISTENER_PLAYER_LOSTACCESSPERM("[MV] - Sorry you can't be in this world anymore!"),

    //// Permissions
    PERMS_NOACCESS_SELF("You don't have access to go here..."),
    PERMS_NOACCESS_OTHER("You can't send %s here..."),
    PERMS_NOACCESS_BLACKLIST_SELF("You don't have access to go to %s from %s"),
    PERMS_NOACCESS_BLACKLIST_OTHER("You don't have access to send %s from %s to %s"),

    //// World purger
    PURGER_ENTITIESKILLED("%d entities purged from the world '%s'"),
    // END CHECKSTYLE-SUPPRESSION: JavadocVariable
    ;

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
