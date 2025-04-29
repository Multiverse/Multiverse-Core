package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.RemovePlayerFlags;
import org.mvplugins.multiverse.core.command.queue.CommandQueueManager;
import org.mvplugins.multiverse.core.command.queue.CommandQueuePayload;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.utils.WorldTickDeferrer;
import org.mvplugins.multiverse.core.utils.result.AsyncAttemptsAggregate;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.helpers.PlayerWorldTeleporter;

@Service
class DeleteCommand extends CoreCommand {

    private final CommandQueueManager commandQueueManager;
    private final WorldManager worldManager;
    private final PlayerWorldTeleporter playerWorldTeleporter;
    private final WorldTickDeferrer worldTickDeferrer;
    private final RemovePlayerFlags flags;

    @Inject
    DeleteCommand(
            @NotNull CommandQueueManager commandQueueManager,
            @NotNull WorldManager worldManager,
            @NotNull PlayerWorldTeleporter playerWorldTeleporter,
            @NotNull WorldTickDeferrer worldTickDeferrer,
            @NotNull RemovePlayerFlags flags
    ) {
        this.commandQueueManager = commandQueueManager;
        this.worldManager = worldManager;
        this.playerWorldTeleporter = playerWorldTeleporter;
        this.worldTickDeferrer = worldTickDeferrer;
        this.flags = flags;
    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.delete")
    @CommandCompletion("@mvworlds:scope=loaded @flags:groupName=" + RemovePlayerFlags.NAME)
    @Syntax("<world>")
    @Description("{@@mv-core.delete.description}")
    void onDeleteCommand(
            MVCommandIssuer issuer,

            @Single
            @Syntax("<world>")
            @Description("The world you want to delete.")
            MultiverseWorld world,

            @Optional
            @Syntax("[--remove-players]")
            @Description("")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        commandQueueManager.addToQueue(CommandQueuePayload
                .issuer(issuer)
                .action(() -> runDeleteCommand(issuer, world, parsedFlags))
                .prompt(Message.of(MVCorei18n.DELETE_PROMPT, "",
                        Replace.WORLD.with(world.getName()))));
    }

    private void runDeleteCommand(MVCommandIssuer issuer, MultiverseWorld world, ParsedCommandFlags parsedFlags) {
        issuer.sendInfo(MVCorei18n.DELETE_DELETING, Replace.WORLD.with(world.getName()));

        var future = parsedFlags.hasFlag(flags.removePlayers)
                        && world.isLoaded()
                        && world instanceof LoadedMultiverseWorld loadedWorld
                ? playerWorldTeleporter.removeFromWorld(loadedWorld)
                : AsyncAttemptsAggregate.emptySuccess();

        future.onSuccess(() -> worldTickDeferrer.deferWorldTick(() -> doWorldDeleting(issuer, world)))
                .onFailure(() -> issuer.sendError("Failed to teleport one or more players out of the world!"));
    }

    private void doWorldDeleting(MVCommandIssuer issuer, MultiverseWorld world) {
        worldManager.deleteWorld(world)
                .onSuccess(deletedWorldName -> {
                    Logging.fine("World delete success: " + deletedWorldName);
                    issuer.sendInfo(MVCorei18n.DELETE_SUCCESS, Replace.WORLD.with(deletedWorldName));
                }).onFailure(failure -> {
                    Logging.fine("World delete failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }

    @Service
    private static final class LegacyAlias extends DeleteCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(
                @NotNull CommandQueueManager commandQueueManager,
                @NotNull WorldManager worldManager,
                @NotNull PlayerWorldTeleporter playerWorldTeleporter,
                @NotNull WorldTickDeferrer worldTickDeferrer,
                @NotNull RemovePlayerFlags flags) {
            super(commandQueueManager, worldManager, playerWorldTeleporter, worldTickDeferrer, flags);
        }

        @Override
        @CommandAlias("mvdelete")
        void onDeleteCommand(MVCommandIssuer issuer, MultiverseWorld world, String[] flags) {
            super.onDeleteCommand(issuer, world, flags);
        }
    }
}
