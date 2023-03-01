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
import com.onarandombox.MultiverseCore.api.MVCore;
import com.onarandombox.MultiverseCore.commandtools.queue.QueuedCommand;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class DeleteCommand extends MultiverseCoreCommand {
    public DeleteCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.delete")
    @CommandCompletion("@mvworlds:scope=both")
    @Syntax("<world>")
    @Description("{@@mv-core.delete.description}")
    public void onDeleteCommand(BukkitCommandIssuer issuer,

                                @Single
                                @Conditions("validWorldName:scope=both")
                                @Syntax("<world>")
                                @Description("The world you want to delete.")
                                String worldName
    ) {
        this.plugin.getMVCommandManager().getCommandQueueManager().addToQueue(new QueuedCommand(
                issuer.getIssuer(),
                () -> {
                    issuer.sendInfo(MVCorei18n.DELETE_DELETING,
                            "{world}", worldName);
                    if (!this.plugin.getMVWorldManager().deleteWorld(worldName)) {
                        issuer.sendInfo(MVCorei18n.DELETE_FAILED,
                                "{world}", worldName);
                        return;
                    }
                    issuer.sendInfo(MVCorei18n.DELETE_SUCCESS,
                            "{world}", worldName);
                },
                "{@@mv-core.delete.prompt}".replace("{world}", worldName)
        ));
    }
}
