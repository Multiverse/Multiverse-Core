package com.onarandombox.multiverse.core.command;

import com.dumptruckman.minecraft.pluginbase.plugin.command.Command;
import com.onarandombox.multiverse.core.api.Core;

abstract class MultiverseCommand extends Command<Core> {

    static {
        // Statically initialize help language
        CommandHelp.init();
    }
}
