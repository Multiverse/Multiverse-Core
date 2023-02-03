package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BaseCommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.jetbrains.annotations.NotNull;

/**
 * A base command for Multiverse.
 */
public class MultiverseCommand extends BaseCommand {

    protected final MultiverseCore plugin;

    protected MultiverseCommand(@NotNull MultiverseCore plugin) {
        this.plugin = plugin;
    }
}
