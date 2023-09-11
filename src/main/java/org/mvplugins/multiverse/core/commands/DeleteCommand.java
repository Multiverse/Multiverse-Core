package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.commandtools.queue.QueuedCommand;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.worldnew.WorldManager;

@Service
@CommandAlias("mv")
public class DeleteCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    @Inject
    public DeleteCommand(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.delete")
    @CommandCompletion("@mvworlds:scope=both")
    @Syntax("<world>")
    @Description("{@@mv-core.delete.description}")
    public void onDeleteCommand(MVCommandIssuer issuer,

                                @Single
                                @Conditions("worldname:scope=both")
                                @Syntax("<world>")
                                @Description("The world you want to delete.")
                                String worldName
    ) {
        this.commandManager.getCommandQueueManager().addToQueue(new QueuedCommand(
                issuer.getIssuer(),
                () -> {
                    issuer.sendInfo(MVCorei18n.DELETE_DELETING, "{world}", worldName);
                    worldManager.deleteWorld(worldName)
                            .onSuccess(deletedWorldName -> {
                                Logging.fine("World delete success: " + deletedWorldName);
                                issuer.sendInfo(MVCorei18n.DELETE_SUCCESS, "{world}", deletedWorldName);
                            }).onFailure(failure -> {
                                Logging.fine("World delete failure: " + failure);
                                issuer.sendError(failure.getFailureMessage());
                            });
                },
                this.commandManager.formatMessage(
                        issuer,
                        MessageType.INFO,
                        MVCorei18n.DELETE_PROMPT,
                        "{world}", worldName)
        ));
    }
}
