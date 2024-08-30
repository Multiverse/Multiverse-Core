package org.mvplugins.multiverse.core.utils;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.utils.message.Message;
import org.mvplugins.multiverse.core.utils.message.MessageReplacement;

public enum MVCorei18n implements MessageKeyProvider {
    // BEGIN CHECKSTYLE-SUPPRESSION: Javadoc

    // config status
    CONFIG_SAVE_FAILED,
    CONFIG_NODE_NOTFOUND,

    // check command
    CHECK_CHECKING,

    // clone command
    CLONE_CLONING,
    CLONE_SUCCESS,

    // Coordinates command
    COORDINATES_INFO_TITLE,
    COORDINATES_INFO_WORLD,
    COORDINATES_INFO_ALIAS,
    COORDINATES_INFO_WORLDSCALE,
    COORDINATES_INFO_COORDINATES,
    COORDINATES_INFO_DIRECTION,

    // create command
    CREATE_PROPERTIES,
    CREATE_PROPERTIES_ENVIRONMENT,
    CREATE_PROPERTIES_SEED,
    CREATE_PROPERTIES_WORLDTYPE,
    CREATE_PROPERTIES_ADJUSTSPAWN,
    CREATE_PROPERTIES_GENERATOR,
    CREATE_PROPERTIES_STRUCTURES,
    CREATE_LOADING,
    CREATE_SUCCESS,

    // delete command
    DELETE_DELETING,
    DELETE_PROMPT,
    DELETE_SUCCESS,

    // Dumps command
    DUMPS_DESCRIPTION,
    DUMPS_URL_LIST,

    // gamerule set command
    GAMERULE_SET_FAILED,
    GAMERULE_SET_SUCCESS_SINGLE,
    GAMERULE_SET_SUCCESS_MULTIPLE,

    // gamerule reset command
    GAMERULE_RESET_FAILED,
    GAMERULE_RESET_SUCCESS_SINGLE,
    GAMERULE_RESET_SUCCESS_MULTIPLE,

    // gamerule list command
    GAMERULE_LIST_DESCRIPTION,
    GAMERULE_LIST_DESCRIPTION_PAGE,
    GAMERULE_LIST_DESCRIPTION_WORLD,
    GAMERULE_LIST_TITLE,

    // Generators command
    GENERATORS_DESCRIPTION,
    GENERATORS_DESCRIPTION_FLAGS,
    GENERATORS_EMPTY,

    // import command
    IMPORT_IMPORTING,
    IMPORT_SUCCESS,

    // load command
    LOAD_LOADING,
    LOAD_SUCCESS,

    // regen command
    REGEN_REGENERATING,
    REGEN_PROMPT,
    REGEN_SUCCESS,

    // reload command
    RELOAD_RELOADING,
    RELOAD_SUCCESS,

    // remove command
    REMOVE_SUCCESS,

    // root MV command
    ROOT_TITLE,
    ROOT_HELP,

    // spawn tp command
    SPAWN_DESCRIPTION,
    SPAWN_PLAYER_DESCRIPTION,
    SPAWN_MESSAGE,
    SPAWN_CONSOLENAME,
    SPAWN_YOU,

    // teleport command
    TELEPORT_SUCCESS,

    // unload command
    UNLOAD_UNLOADING,
    UNLOAD_SUCCESS,

    // who command
    WHO_DESCRIPTION,
    WHO_ALL_DESCRIPTION,
    WHO_WORLD_DESCRIPTION,
    WHO_FLAGS_DESCRIPTION,
    WHO_EMPTY,

    // version command
    VERSION_MV,
    VERSION_AUTHORS,
    VERSION_SECRETCODE,

    // debug command
    DEBUG_INFO_OFF,
    DEBUG_INFO_ON,

    // commands error
    COMMANDS_ERROR_PLAYERSONLY,
    COMMANDS_ERROR_MULTIVERSEWORLDONLY,

    // entry check
    ENTRYCHECK_BLACKLISTED,
    ENTRYCHECK_NOTENOUGHMONEY,
    ENTRYCHECK_CANNOTPAYENTRYFEE,
    ENTRYCHECK_EXCEEDPLAYERLIMIT,
    ENTRYCHECK_NOWORLDACCESS,

    // world manager result
    CLONEWORLD_INVALIDWORLDNAME,
    CLONEWORLD_WORLDEXISTFOLDER,
    CLONEWORLD_WORLDEXISTUNLOADED,
    CLONEWORLD_WORLDEXISTLOADED,
    CLONEWORLD_COPYFAILED,

    CREATEWORLD_INVALIDWORLDNAME,
    CREATEWORLD_WORLDEXISTFOLDER,
    CREATEWORLD_WORLDEXISTUNLOADED,
    CREATEWORLD_WORLDEXISTLOADED,
    CREATEWORLD_BUKKITCREATIONFAILED,

    DELETEWORLD_WORLDNONEXISTENT,
    DELETEWORLD_LOADFAILED,
    DELETEWORLD_WORLDFOLDERNOTFOUND,
    DELETEWORLD_FAILEDTODELETEFOLDER,

    IMPORTWORLD_INVALIDWORLDNAME,
    IMPORTWORLD_WORLDFOLDERINVALID,
    IMPORTWORLD_WORLDEXISTUNLOADED,
    IMPORTWORLD_WORLDEXISTLOADED,
    IMPORTWORLD_BUKKITCREATIONFAILED,

    LOADWORLD_WORLDALREADYLOADING,
    LOADWORLD_WORLDNONEXISTENT,
    LOADWORLD_WORLDEXISTFOLDER,
    LOADWORLD_WORLDEXISTLOADED,
    LOADWORLD_BUKKITCREATIONFAILED,

    REMOVEWORLD_WORLDNONEXISTENT,

    UNLOADWORLD_WORLDALREADYUNLOADING,
    UNLOADWORLD_WORLDNONEXISTENT,
    UNLOADWORLD_WORLDUNLOADED,
    UNLOADWORLD_BUKKITUNLOADFAILED,

    // generic
    GENERIC_SUCCESS,
    GENERIC_FAILURE;

    // END CHECKSTYLE-SUPPRESSION: Javadoc

    private final MessageKey key = MessageKey.of("mv-core." + this.name().replace('_', '.').toLowerCase());

    @Override
    public MessageKey getMessageKey() {
        return this.key;
    }

    @NotNull
    public Message bundle(@NotNull String nonLocalizedMessage, @NotNull MessageReplacement... replacements) {
        return Message.of(this, nonLocalizedMessage, replacements);
    }
}
