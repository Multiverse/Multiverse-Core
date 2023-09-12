package org.mvplugins.multiverse.core.commands;

import java.util.concurrent.CompletableFuture;

import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
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
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.commandtools.queue.QueuedCommand;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.WorldManager;
import org.mvplugins.multiverse.core.worldnew.helpers.PlayerWorldTeleporter;

@Service
@CommandAlias("mv")
class DeleteCommand extends MultiverseCommand {

    private final WorldManager worldManager;
    private final PlayerWorldTeleporter playerWorldTeleporter;

    private final CommandFlag REMOVE_PLAYERS_FLAG = flag(CommandFlag.builder("--remove-players")
            .addAlias("-r")
            .build());

    @Inject
    DeleteCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull PlayerWorldTeleporter playerWorldTeleporter) {
        super(commandManager);
        this.worldManager = worldManager;
        this.playerWorldTeleporter = playerWorldTeleporter;
    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.delete")
    @CommandCompletion("@mvworlds:scope=loaded @flags:groupName=mvdeletecommand")
    @Syntax("<world>")
    @Description("{@@mv-core.delete.description}")
    void onDeleteCommand(
            MVCommandIssuer issuer,

            @Single
            @Conditions("worldname:scope=loaded")
            @Syntax("<world>")
            @Description("The world you want to delete.")
            LoadedMultiverseWorld world,

            @Optional
            @Syntax("[--remove-players]")
            @Description("")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        this.commandManager.getCommandQueueManager().addToQueue(new QueuedCommand(
                issuer.getIssuer(),
                () -> {
                    runDeleteCommand(issuer, world, parsedFlags);
                }, this.commandManager.formatMessage(
                        issuer, MessageType.INFO, MVCorei18n.DELETE_PROMPT, "{world}", world.getName())));
    }

    private void runDeleteCommand(MVCommandIssuer issuer, LoadedMultiverseWorld world, ParsedCommandFlags parsedFlags) {
        issuer.sendInfo(MVCorei18n.DELETE_DELETING, "{world}", world.getName());

        CompletableFuture<Void> future = parsedFlags.hasFlag(REMOVE_PLAYERS_FLAG)
                ? CompletableFuture.allOf(playerWorldTeleporter.removeFromWorld(world))
                : CompletableFuture.completedFuture(null);

        future.thenRun(() -> doWorldDeleting(issuer, world));
    }

    private void doWorldDeleting(MVCommandIssuer issuer, LoadedMultiverseWorld world) {
        worldManager.deleteWorld(world)
                .onSuccess(deletedWorldName -> {
                    Logging.fine("World delete success: " + deletedWorldName);
                    issuer.sendInfo(MVCorei18n.DELETE_SUCCESS, "{world}", deletedWorldName);
                }).onFailure(failure -> {
                    Logging.fine("World delete failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }
}
