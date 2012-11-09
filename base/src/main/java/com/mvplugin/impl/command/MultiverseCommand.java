package com.mvplugin.impl.command;

import com.dumptruckman.minecraft.pluginbase.plugin.command.Command;
import com.mvplugin.impl.MVCore;

abstract class MultiverseCommand extends Command<MVCore> {

    static {
        // Statically initialize help language
        CommandHelp.init();
    }
}
