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
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.commandtools.queue.QueuedCommand;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class DeleteCommand extends MultiverseCommand {

    private final MVWorldManager worldManager;

    @Inject
    public DeleteCommand(@NotNull MVCommandManager commandManager, @NotNull MVWorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.delete")
    @CommandCompletion("@mvworlds:scope=both")
    @Syntax("<world>")
    @Description("Deletes a world on your server PERMANENTLY.")
    public void onDeleteCommand(BukkitCommandIssuer issuer,

                                @Single
                                @Conditions("validWorldName:scope=both")
                                @Syntax("<world>")
                                @Description("The world you want to delete.")
                                String worldName
    ) {
        this.commandManager.getCommandQueueManager().addToQueue(new QueuedCommand(
                issuer.getIssuer(),
                () -> {
                    issuer.sendMessage(String.format("Deleting world '%s'...", worldName));
                    if (!this.worldManager.deleteWorld(worldName)) {
                        issuer.sendMessage(String.format("%sThere was an issue deleting '%s'! Please check console for errors.", ChatColor.RED, worldName));
                        return;
                    }
                    issuer.sendMessage(String.format("%sWorld %s was deleted!", ChatColor.GREEN, worldName));
                },
                "Are you sure you want to delete world '" + worldName + "'?"
        ));
    }
}
