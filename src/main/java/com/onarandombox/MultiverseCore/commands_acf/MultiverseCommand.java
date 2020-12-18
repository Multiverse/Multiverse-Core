package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.BaseCommand;
import com.onarandombox.MultiverseCore.MultiverseCore;

public abstract class MultiverseCommand extends BaseCommand {

    protected final MultiverseCore plugin;
    //TODO: Should we put world manager here?

    protected MultiverseCommand(MultiverseCore plugin) {
        this.plugin = plugin;
    }
}
