package com.onarandombox.MultiVerseCore;

import org.bukkit.command.CommandSender;

public abstract class MVCommandHandler {

    protected final MultiVerseCore plugin;

    public MVCommandHandler(MultiVerseCore plugin) {
        this.plugin = plugin;
    }

    public abstract boolean perform(CommandSender sender, String[] args);
}
