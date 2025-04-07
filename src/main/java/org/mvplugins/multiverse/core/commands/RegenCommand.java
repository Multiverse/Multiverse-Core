package org.mvplugins.multiverse.core.commands;

import java.util.Collections;
import java.util.List;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.CommandValueFlag;
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
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.helpers.PlayerWorldTeleporter;
import org.mvplugins.multiverse.core.world.options.RegenWorldOptions;

@Service
class RegenCommand extends CoreCommand {

    @NotNull
    private final CommandQueueManager commandQueueManager;
    private final WorldManager worldManager;
    private final PlayerWorldTeleporter playerWorldTeleporter;
    private final WorldTickDeferrer worldTickDeferrer;
    private final RegenCommand.Flags flags;

    @Inject
    RegenCommand(
            @NotNull CommandQueueManager commandQueueManager,
            @NotNull WorldManager worldManager,
            @NotNull PlayerWorldTeleporter playerWorldTeleporter,
            @NotNull WorldTickDeferrer worldTickDeferrer,
            @NotNull Flags flags
    ) {
        this.commandQueueManager = commandQueueManager;
        this.worldManager = worldManager;
        this.playerWorldTeleporter = playerWorldTeleporter;
        this.worldTickDeferrer = worldTickDeferrer;
        this.flags = flags;
    }

    @Subcommand("regen")
    @CommandPermission("multiverse.core.regen")
    @CommandCompletion("@mvworlds:scope=loaded @flags:groupName=" + Flags.NAME)
    @Syntax("<world> [--seed [seed] --reset-world-config --reset-gamerules --reset-world-border --remove-players]")
    @Description("{@@mv-core.regen.description}")
    void onRegenCommand(
            MVCommandIssuer issuer,

            @Syntax("<world>")
            @Description("{@@mv-core.regen.world.description}")
            LoadedMultiverseWorld world,

            @Optional
            @Syntax("[--seed [seed] --reset-world-config --reset-gamerules --reset-world-border --remove-players]")
            @Description("{@@mv-core.regen.other.description}")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        commandQueueManager.addToQueue(CommandQueuePayload
                .issuer(issuer)
                .action(() -> runRegenCommand(issuer, world, parsedFlags))
                .prompt(Message.of(MVCorei18n.REGEN_PROMPT, "",
                        Replace.WORLD.with(world.getName()))));
    }

    private void runRegenCommand(MVCommandIssuer issuer, LoadedMultiverseWorld world, ParsedCommandFlags parsedFlags) {
        issuer.sendInfo(MVCorei18n.REGEN_REGENERATING, Replace.WORLD.with(world.getName()));
        List<Player> worldPlayers = world.getPlayers().getOrElse(Collections.emptyList());

        var future = parsedFlags.hasFlag(flags.removePlayers)
                ? playerWorldTeleporter.removeFromWorld(world)
                : AsyncAttemptsAggregate.emptySuccess();

        // todo: using future will hide stacktrace
        future.onSuccess(() -> worldTickDeferrer
                        .deferWorldTick(() -> doWorldRegening(issuer, world, parsedFlags, worldPlayers)))
                .onFailure(() -> issuer.sendError("Failed to teleport one or more players out of the world!"));
    }

    private void doWorldRegening(
            MVCommandIssuer issuer,
            LoadedMultiverseWorld world,
            ParsedCommandFlags parsedFlags,
            List<Player> worldPlayers) {
        //todo: Change biome on regen
        RegenWorldOptions regenWorldOptions = RegenWorldOptions.world(world)
                .randomSeed(parsedFlags.hasFlag(flags.seed))
                .seed(parsedFlags.flagValue(flags.seed))
                .keepWorldConfig(!parsedFlags.hasFlag(flags.resetWorldConfig))
                .keepGameRule(!parsedFlags.hasFlag(flags.resetGamerules))
                .keepWorldBorder(!parsedFlags.hasFlag(flags.resetWorldBorder));

        worldManager.regenWorld(regenWorldOptions).onSuccess(newWorld -> {
            Logging.fine("World regen success: " + newWorld);
            issuer.sendInfo(MVCorei18n.REGEN_SUCCESS, Replace.WORLD.with(newWorld.getName()));
            if (parsedFlags.hasFlag(flags.removePlayers)) {
                playerWorldTeleporter.teleportPlayersToWorld(worldPlayers, newWorld);
            }
        }).onFailure(failure -> {
            Logging.warning("World regen failure: " + failure);
            issuer.sendError(failure.getFailureMessage());
        });
    }

    @Service
    private static final class Flags extends RemovePlayerFlags {

        private static final String NAME = "mvregen";

        @Inject
        private Flags(@NotNull CommandFlagsManager flagsManager) {
            super(NAME, flagsManager);
        }

        private final CommandValueFlag<String> seed = flag(CommandValueFlag.builder("--seed", String.class)
                .addAlias("-s")
                .completion(input -> Collections.singleton(String.valueOf(ACFUtil.RANDOM.nextLong())))
                .optional()
                .build());

        private final CommandFlag resetWorldConfig = flag(CommandFlag.builder("--reset-world-config")
                .addAlias("-wc")
                .build());

        private final CommandFlag resetGamerules = flag(CommandFlag.builder("--reset-gamerules")
                .addAlias("-gm")
                .build());

        private final CommandFlag resetWorldBorder = flag(CommandFlag.builder("--reset-world-border")
                .addAlias("-wb")
                .build());
    }

    @Service
    private static final class LegacyAlias extends RegenCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(
                @NotNull CommandQueueManager commandQueueManager,
                @NotNull WorldManager worldManager,
                @NotNull PlayerWorldTeleporter playerWorldTeleporter,
                @NotNull WorldTickDeferrer worldTickDeferrer,
                @NotNull Flags flags
        ) {
            super(commandQueueManager, worldManager, playerWorldTeleporter, worldTickDeferrer, flags);
        }

        @Override
        @CommandAlias("mvregen")
        void onRegenCommand(MVCommandIssuer issuer, LoadedMultiverseWorld world, String[] flags) {
            super.onRegenCommand(issuer, world, flags);
        }
    }
}
