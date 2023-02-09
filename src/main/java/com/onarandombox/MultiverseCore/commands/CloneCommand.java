package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CloneCommand extends MultiverseCommand {
    public CloneCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("clone")
    @CommandPermission("multiverse.core.clone")
    @Syntax("<world> <new world name>")
    @CommandCompletion("@mvworlds:scope=both @empty")
    @Description("Clones a world.")
    public void onCloneCommand(CommandIssuer issuer,

                               @NotNull
                               @Syntax("<world>")
                               @Description("The target world to clone.")
                               MVWorld world,

                               @Single
                               @Conditions("worldname:scope=new")
                               @Syntax("<name>")
                               @Description("The new cloned world name.")
                               String newWorldName
    ) {
        issuer.sendMessage(String.format("Cloning world '%s' to '%s'...", world.getName(), newWorldName));

        if (!this.plugin.getMVWorldManager().cloneWorld(world.getName(), newWorldName)) {
            issuer.sendMessage(String.format("%sWorld could not be cloned! See console for more details.", ChatColor.RED));
            return;
        }
        issuer.sendMessage(String.format("%sCloned world '%s'!", ChatColor.GREEN, newWorldName));
    }
}
