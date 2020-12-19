package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class UnloadCommand extends MultiverseCommand {

    public UnloadCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("unload")
    @CommandPermission("multiverse.core.unload")
    @Syntax("<world>")
    @CommandCompletion("@MVWorlds")
    @Description("Unloads a world from Multiverse. This does NOT remove the world folder. This does NOT remove it from the config file.")
    public void onUnloadCommand(@NotNull CommandSender sender,
                                @NotNull @Flags("other") MultiverseWorld world) {

        //TODO: Should be able to use MVWorld object directly
        if (!this.plugin.getMVWorldManager().unloadWorld(world.getName())) {
            sender.sendMessage("Error trying to unload world '" + world.getName() + "'!");
            return;
        }
        Command.broadcastCommandMessage(sender, "Unloaded world '" + world.getName() + "'!");
    }
}
