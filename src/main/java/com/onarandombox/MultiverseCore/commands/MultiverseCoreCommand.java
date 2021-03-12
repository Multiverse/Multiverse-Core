package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.messaging.CoreMessageKeys;

/**
 * Generic multiverse core command with handy reference to the plugin instance.
 */
public abstract class MultiverseCoreCommand extends MultiverseCommand {

    protected final MultiverseCore plugin;

    protected MultiverseCoreCommand(MultiverseCore plugin) {
        this.plugin = plugin;
    }

    protected boolean saveMVConfigs(CommandIssuer issuer) {
        if (this.plugin.saveMVConfigs()) {
            return true;
        }
        issuer.sendError(CoreMessageKeys.CONFIG_SAVE_FAILED);
        return false;
    }
}
