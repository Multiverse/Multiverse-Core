package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class LoadCommand extends MultiverseCommand {

    public LoadCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("load")
    @CommandPermission("multiverse.core.load")
    @Syntax("<world>")
    @CommandCompletion("@unloadedWorlds")
    @Description("Loads a world into Multiverse.")
    public void onLoadCommand(@NotNull CommandSender sender,
                              @NotNull @Flags("type=world name") @Conditions("isUnloadedWorld") String worldName) {

        if (!this.plugin.getMVWorldManager().loadWorld(worldName)) {
            sender.sendMessage("Error trying to load world '" + worldName + "'!");
            return;
        }
        Command.broadcastCommandMessage(sender, "Loaded world '" + worldName + "'!");
    }
}
