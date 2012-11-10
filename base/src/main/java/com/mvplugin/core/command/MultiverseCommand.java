package com.mvplugin.core.command;

import com.dumptruckman.minecraft.pluginbase.plugin.command.Command;
import com.mvplugin.core.api.MultiverseCore;

abstract class MultiverseCommand extends Command<MultiverseCore> {

    static {
        // Statically initialize help language
        CommandHelp.init();
    }
}
