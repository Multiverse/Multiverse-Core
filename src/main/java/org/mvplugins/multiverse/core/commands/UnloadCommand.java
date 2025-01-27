package org.mvplugins.multiverse.core.commands;

import java.util.Collections;

import co.aikar.commands.CommandIssuer;
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

import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.flag.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.utils.result.Async;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.helpers.PlayerWorldTeleporter;
import org.mvplugins.multiverse.core.world.options.UnloadWorldOptions;

@Service
@CommandAlias("mv")
final class UnloadCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final PlayerWorldTeleporter playerWorldTeleporter;

    private final CommandFlag removePlayersFlag = flag(CommandFlag.builder("--remove-players")
            .addAlias("-r")
            .build());

    private final CommandFlag noSaveFlag = flag(CommandFlag.builder("--no-save")
            .addAlias("-n")
            .build());

    @Inject
    UnloadCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull PlayerWorldTeleporter playerWorldTeleporter) {
        super(commandManager);
        this.worldManager = worldManager;
        this.playerWorldTeleporter = playerWorldTeleporter;
    }

    @CommandAlias("mvunload")
    @Subcommand("unload")
    @CommandPermission("multiverse.core.unload")
    @CommandCompletion("@mvworlds @flags:groupName=mvunloadcommand")
    @Syntax("<world>")
    @Description("{@@mv-core.unload.description}")
    void onUnloadCommand(
            CommandIssuer issuer,

            @Syntax("<world>")
            @Description("{@@mv-core.unload.world.description}")
            LoadedMultiverseWorld world,

            @Optional
            @Syntax("[--remove-players] [--no-save]")
            @Description("{@@mv-core.gamerules.description.page}")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        MVCorei18n.UNLOAD_UNLOADING.sendInfo(issuer, Replace.WORLD.with(world.getAlias()));

        var future = parsedFlags.hasFlag(removePlayersFlag)
                ? playerWorldTeleporter.removeFromWorld(world)
                : Async.completedFuture(Collections.emptyList());

        future.thenRun(() -> doWorldUnloading(issuer, world, parsedFlags));
    }

    private void doWorldUnloading(CommandIssuer issuer, LoadedMultiverseWorld world, ParsedCommandFlags parsedFlags) {
        UnloadWorldOptions unloadWorldOptions = UnloadWorldOptions.world(world)
                .saveBukkitWorld(!parsedFlags.hasFlag(noSaveFlag));
        worldManager.unloadWorld(unloadWorldOptions)
                .onSuccess(loadedWorld -> {
                    Logging.fine("World unload success: " + loadedWorld);
                    MVCorei18n.UNLOAD_SUCCESS.sendInfo(issuer, Replace.WORLD.with(loadedWorld.getName()));
                }).onFailure(failure -> {
                    Logging.fine("World unload failure: " + failure);
                    failure.getFailureMessage().sendError(issuer);
                });
    }
}
