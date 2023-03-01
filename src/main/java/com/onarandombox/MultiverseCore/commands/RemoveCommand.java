package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
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
public class RemoveCommand extends MultiverseCoreCommand {
    public RemoveCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("remove")
    @CommandPermission("multiverse.core.remove")
    @CommandCompletion("@mvworlds:scope=both")
    @Syntax("<world>")
    @Description("{@@mv-core.remove.description}")
    public void onRemoveCommand(BukkitCommandIssuer issuer,

                                @Single
                                @Conditions("mvworlds:scope=both")
                                @Syntax("<world>")
                                @Description("{@@mv-core.remove.world.description}")
                                String worldName
    ) {
        if (!this.plugin.getMVWorldManager().removeWorldFromConfig(worldName)) {
            issuer.sendInfo(MVCorei18n.REMOVE_FAILED);
            return;
        }
        issuer.sendInfo(MVCorei18n.REMOVE_SUCCESS,
                "{world}", worldName);
    }
}
