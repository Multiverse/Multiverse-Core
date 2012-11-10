package com.mvplugin.core.command;

import com.dumptruckman.minecraft.pluginbase.plugin.command.Command;

abstract class MultiverseCommand extends Command<MVCore> {

    static {
        // Statically initialize help language
        CommandHelp.init();
    }
}
