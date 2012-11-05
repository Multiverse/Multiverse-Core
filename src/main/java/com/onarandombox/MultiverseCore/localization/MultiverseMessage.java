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
    // Anchor Command
    CMD_ANCHOR_NOLISTPERM("&cYou don't have the permission to list anchors!"),
    CMD_ANCHOR_LISTHEADER("&d====[ Multiverse Anchor List ]===="),
    CMD_ANCHOR_NOMATCH("&cSorry... &fNo anchors matched your filter: &b%s"),
    CMD_ANCHOR_NODEF("&cSorry... &fNo anchors were defined."),
    CMD_ANCHOR_PAGEHEADER("&b Page %d of %d"),
    CMD_ANCHOR_NODELPERM("&cYou don't have the permission to delete anchors!"),
    CMD_ANCHOR_NOCREATEPERM("&cYou don't have the permission to create anchors!"),
    CMD_ANCHOR_DELSUCCESS("Anchor '%s' was successfully &cdeleted!"),
    CMD_ANCHOR_DELFAIL("Anchor '%s' was &cNOT&f deleted!"),
    CMD_ANCHOR_CREATESUCCESS("Anchor '%s' was &asuccessfully&f created!"),
    CMD_ANCHOR_CREATEFAIL("Anchor '%s' was &cNOT&f created!"),
    CMD_ANCHOR_CONSOLECREATE("You must be a player to create Anchors!"),
    // Check Command
    CMD_CHECK_NOSUCHPLAYER("Could not find player &d%s&f\nAre they online?"),
    CMD_CHECK_NOSUCHDEST("You asked if '%s' could go to '&d%s&f', but I couldn't find a Destination of that name? Did you type it correctly?"),
    // Clone Command
    CMD_CLONE_NOSUCHWORLD("Sorry, Multiverse doesn't know about world '%s', so we can't clone it!\nCheck the &a/mv list&f command to verify it is listed."),
    CMD_CLONE_SUCCESS("&aWorld Cloned!"),
    CMD_CLONE_FAIL("&cWorld could NOT be cloned!"),
    // Config Command
    CMD_CONFIG_SETFAIL("&cSetting '%s' to '%s' failed!"),
    CMD_CONFIG_SUCCESS("&aSUCCESS!&f Values were updated successfully!"),
    CMD_CONFIG_FAIL("&cFAIL!&f Check your console for details!"),
    // Create Command
    CMD_CREATE_WORLDEXISTS("&cMultiverse cannot create &n&6another&r&c world named '%s'."),
    CMD_CREATE_FILEEXISTS("&cA Folder/World already exists with this name!\n&cIf you are confident it is a world you can import it with &a/mv import"),
    CMD_CREATE_INVALIDENV("&cThat is not a valid environment."),
    CMD_CREATE_INVALIDTYPE("&cThat is not a valid World Type."),
    CMD_CREATE_INVALIDGEN("Invalid generator '%s'! &cAborting world creation."),
    CMD_CREATE_START("Starting creation of world '%s'..."),
    CMD_CREATE_COMPLETE("Complete!"),
    CMD_CREATE_FAILED("FAILED."),
    // Generator Command
    CMD_GENERATOR_LISTHEADER("&b--- Loaded Generator Plugins ---"),
    CMD_GENERATOR_NOGENSFOUND("&cNo Generator Plugins found."),
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
    ; // SUPPRESS CHECKSTYLE: Whitespace

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
