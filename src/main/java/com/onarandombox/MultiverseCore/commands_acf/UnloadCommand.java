package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandAlias("mv")
public class UnloadCommand extends MultiverseCommand {

    public UnloadCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("unload")
    @CommandPermission("multiverse.core.unload")
    @Syntax("<world>")
    @CommandCompletion("@mvworlds")
    @Description("Unloads a world from Multiverse. This does NOT remove the world folder. This does NOT remove it from the config file.")
    public void onUnloadCommand(CommandSender sender, @Single String worldName) {
        if (!this.plugin.getMVWorldManager().unloadWorld(worldName)) {
            sender.sendMessage("Error trying to unload world '" + worldName + "'!");
            return;
        }
        Command.broadcastCommandMessage(sender, "Unloaded world '" + worldName + "'!");
    }
}
