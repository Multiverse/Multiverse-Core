package com.onarandombox.MultiVerseCore.commands;

import org.bukkit.command.CommandSender;

import com.onarandombox.MultiVerseCore.MVCommandHandler;
import com.onarandombox.MultiVerseCore.MultiVerseCore;

public class MVReload extends MVCommandHandler {

    public MVReload(MultiVerseCore plugin) {
        super(plugin);
    }

    @Override
    public boolean perform(CommandSender sender, String[] args) {
        // TODO: Implement Permissions
        MultiVerseCore.log.info(MultiVerseCore.logPrefix + "Reloading MultiVerse");
        plugin.loadConfigs();
        plugin.loadWorlds();
        MultiVerseCore.log.info(MultiVerseCore.logPrefix + "Reload Complete!");
        return true;
    }

}
