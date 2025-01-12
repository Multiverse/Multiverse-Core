package org.mvplugins.multiverse.core.commands;

import java.util.Collections;

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
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.commandtools.queue.CommandQueuePayload;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.utils.WorldTickDeferrer;
import org.mvplugins.multiverse.core.utils.message.Message;
import org.mvplugins.multiverse.core.utils.result.Async;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.helpers.PlayerWorldTeleporter;

import static org.mvplugins.multiverse.core.utils.message.MessageReplacement.replace;

@Service
@CommandAlias("mv")
class DeleteCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final PlayerWorldTeleporter playerWorldTeleporter;
    private final WorldTickDeferrer worldTickDeferrer;

    private final CommandFlag REMOVE_PLAYERS_FLAG = flag(CommandFlag.builder("--remove-players")
            .addAlias("-r")
            .build());

    @Inject
    DeleteCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull PlayerWorldTeleporter playerWorldTeleporter,
            @NotNull WorldTickDeferrer worldTickDeferrer) {
        super(commandManager);
        this.worldManager = worldManager;
        this.playerWorldTeleporter = playerWorldTeleporter;
        this.worldTickDeferrer = worldTickDeferrer;
    }

    @CommandAlias("mvdelete")
    @Subcommand("delete")
    @CommandPermission("multiverse.core.delete")
    @CommandCompletion("@mvworlds:scope=loaded @flags:groupName=mvdeletecommand")
    @Syntax("<world>")
    @Description("{@@mv-core.delete.description}")
    void onDeleteCommand(
            MVCommandIssuer issuer,

            @Single
            @Conditions("worldname:scope=both")
            @Syntax("<world>")
            @Description("The world you want to delete.")
            MultiverseWorld world,

            @Optional
            @Syntax("[--remove-players]")
            @Description("")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        this.commandManager.getCommandQueueManager().addToQueue(CommandQueuePayload
                .issuer(issuer)
                .action(() -> runDeleteCommand(issuer, world, parsedFlags))
                .prompt(Message.of(MVCorei18n.DELETE_PROMPT, "",
                        replace("{world}").with(world.getName()))));
    }

    private void runDeleteCommand(MVCommandIssuer issuer, MultiverseWorld world, ParsedCommandFlags parsedFlags) {
        issuer.sendInfo(MVCorei18n.DELETE_DELETING, "{world}", world.getName());

        var future = parsedFlags.hasFlag(REMOVE_PLAYERS_FLAG) && world.isLoaded() && world instanceof LoadedMultiverseWorld loadedWorld
                ? playerWorldTeleporter.removeFromWorld(loadedWorld)
                : Async.completedFuture(Collections.emptyList());

        future.thenRun(() -> worldTickDeferrer.deferWorldTick(() -> doWorldDeleting(issuer, world)));
    }

    private void doWorldDeleting(MVCommandIssuer issuer, MultiverseWorld world) {
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
