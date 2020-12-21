package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CloneCommand extends MultiverseCommand {

    public CloneCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("clone")
    @CommandPermission("multiverse.core.clone")
    @Syntax("<world> <name>")
    @CommandCompletion("@MVWorlds|@unloadedWorlds")
    @Description("Clones a world.")
    public void onCloneCommand(@NotNull CommandSender sender,
                               @NotNull @Conditions("isWorldInConfig") String worldName,
                               @NotNull @Single @Flags("trim") @Conditions("creatableWorldName") String newWorldName) {

        sender.sendMessage((this.plugin.getMVWorldManager().cloneWorld(worldName, newWorldName))
                ? ChatColor.GREEN + "World cloned!"
                : ChatColor.RED + "World could not be cloned! See console for more details.");
    }
}
