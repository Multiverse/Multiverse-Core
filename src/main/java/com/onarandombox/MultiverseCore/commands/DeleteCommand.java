package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.MessageType;
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
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import jakarta.inject.Inject;
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
    @Description("{@@mv-core.delete.description}")
    public void onDeleteCommand(BukkitCommandIssuer issuer,

                                @Single
                                @Conditions("worldname:scope=both")
                                @Syntax("<world>")
                                @Description("The world you want to delete.")
                                String worldName
    ) {
        this.commandManager.getCommandQueueManager().addToQueue(new QueuedCommand(
                issuer.getIssuer(),
                () -> {
                    issuer.sendInfo(MVCorei18n.DELETE_DELETING,
                            "{world}", worldName);
                    if (!this.worldManager.deleteWorld(worldName)) {
                        issuer.sendError(MVCorei18n.DELETE_FAILED,
                                "{world}", worldName);
                        return;
                    }
                    issuer.sendInfo(MVCorei18n.DELETE_SUCCESS,
                            "{world}", worldName);
                },
                this.commandManager.formatMessage(
                        issuer,
                        MessageType.INFO,
                        MVCorei18n.DELETE_PROMPT,
                        "{world}", worldName)
        ));
    }
}
