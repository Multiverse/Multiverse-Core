package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.annotation.Values;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandAlias("mv")
public class LoadCommand extends MultiverseCommand {

    public LoadCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("load")
    @CommandPermission("multiverse.core.load")
    @Syntax("<world>")
    @CommandCompletion("@unloadedworlds")
    @Description("Loads a world into Multiverse.")
    public void onLoadCommand(CommandSender sender, @Single String world) {
        if (!this.plugin.getMVWorldManager().loadWorld(world)) {
            sender.sendMessage("Error trying to load world '" + world + "'!");
            return;
        }
        Command.broadcastCommandMessage(sender, "Loaded world '" + world + "'!");
    }
}
