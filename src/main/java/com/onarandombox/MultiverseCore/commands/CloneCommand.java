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
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CloneCommand extends MultiverseCoreCommand {
    public CloneCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("clone")
    @CommandPermission("multiverse.core.clone")
    @CommandCompletion("@mvworlds:scope=both @empty")
    @Syntax("<world> <new world name>")
    @Description("{@@mv-core.clone.description}")
    public void onCloneCommand(CommandIssuer issuer,

                               @Conditions("validWorldName:scope=both")
                               @Syntax("<world>")
                               @Description("{@@mv-core.clone.world.description}")
                               String worldName,

                               @Single
                               @Conditions("validWorldName:scope=new")
                               @Syntax("<new world name>")
                               @Description("{@@mv-core.clone.newWorld.description}")
                               String newWorldName
    ) {
        issuer.sendInfo(MVCorei18n.CLONE_CLONING,
                "{world}", worldName,
                "{newWorld}", newWorldName);

        if (!this.plugin.getMVWorldManager().cloneWorld(worldName, newWorldName)) {
            issuer.sendInfo(MVCorei18n.CLONE_FAILED);
            return;
        }
        issuer.sendInfo(MVCorei18n.CLONE_SUCCESS,
                "{world}", newWorldName);
    }
}
