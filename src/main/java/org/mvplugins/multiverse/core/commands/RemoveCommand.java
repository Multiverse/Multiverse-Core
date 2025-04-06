package org.mvplugins.multiverse.core.commands;

import java.util.Collections;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.utils.result.AsyncAttemptsAggregate;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.helpers.PlayerWorldTeleporter;

@Service
class RemoveCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final PlayerWorldTeleporter playerWorldTeleporter;

    private final CommandFlag removePlayersFlag = flag(CommandFlag.builder("--remove-players")
            .addAlias("-r")
            .build());

    @Inject
    RemoveCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull PlayerWorldTeleporter playerWorldTeleporter) {
        super(commandManager);
        this.worldManager = worldManager;
        this.playerWorldTeleporter = playerWorldTeleporter;
    }

    @Subcommand("remove")
    @CommandPermission("multiverse.core.remove")
    @CommandCompletion("@mvworlds:scope=both @flags:groupName=mvremovecommand")
    @Syntax("<world>")
    @Description("{@@mv-core.remove.description}")
    void onRemoveCommand(
            MVCommandIssuer issuer,

            @Syntax("<world>")
            @Description("{@@mv-core.remove.world.description}")
            MultiverseWorld world,

            @Optional
            @Syntax("[--remove-players]")
            @Description("")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        var future = parsedFlags.hasFlag(removePlayersFlag)
                ? worldManager.getLoadedWorld(world).map(playerWorldTeleporter::removeFromWorld).getOrElse(AsyncAttemptsAggregate::emptySuccess)
                : AsyncAttemptsAggregate.emptySuccess();

        future.onSuccess(() -> doWorldRemoving(issuer, world))
                .onFailure(() -> issuer.sendError("Failed to teleport one or more players out of the world!"));
    }

    private void doWorldRemoving(MVCommandIssuer issuer, MultiverseWorld world) {
        worldManager.removeWorld(world)
                .onSuccess(removedWorldName -> {
                    Logging.fine("World remove success: " + removedWorldName);
                    issuer.sendInfo(MVCorei18n.REMOVE_SUCCESS, Replace.WORLD.with(removedWorldName));
                }).onFailure(failure -> {
                    Logging.fine("World remove failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }

    @Service
    private static final class LegacyAlias extends RemoveCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager, @NotNull PlayerWorldTeleporter playerWorldTeleporter) {
            super(commandManager, worldManager, playerWorldTeleporter);
        }

        @Override
        @CommandAlias("mvremove")
        void onRemoveCommand(MVCommandIssuer issuer, MultiverseWorld world, String[] flags) {
            super.onRemoveCommand(issuer, world, flags);
        }

        @Override
        public boolean doFlagRegistration() {
            return false;
        }
    }
}
