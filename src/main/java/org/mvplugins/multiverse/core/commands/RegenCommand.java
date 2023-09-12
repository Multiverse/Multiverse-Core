package org.mvplugins.multiverse.core.commands;

import java.util.Collections;
import java.util.Random;

import co.aikar.commands.MessageType;
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

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlagGroup;
import org.mvplugins.multiverse.core.commandtools.flags.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.commandtools.queue.QueuedCommand;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.WorldManager;
import org.mvplugins.multiverse.core.worldnew.options.RegenWorldOptions;

@Service
@CommandAlias("mv")
public class RegenCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    @Inject
    public RegenCommand(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;

        registerFlagGroup(CommandFlagGroup.builder("mvregen")
                .add(CommandValueFlag.builder("--seed", String.class)
                        .addAlias("-s")
                        .completion((input) -> Collections.singleton(String.valueOf(new Random().nextLong())))
                        .optional()
                        .build())
                .add(CommandFlag.builder("--reset-world-config")
                        .addAlias("-wc")
                        .build())
                .add(CommandFlag.builder("--reset-gamerules")
                        .addAlias("-gm")
                        .build())
                .add(CommandFlag.builder("--reset-world-border")
                        .addAlias("-wb")
                        .build())
                .build());
    }

    @Subcommand("regen")
    @CommandPermission("multiverse.core.regen")
    @CommandCompletion("@mvworlds:scope=loaded @flags:groupName=mvregen")
    @Syntax("<world> --seed [seed] --keep-gamerules")
    @Description("{@@mv-core.regen.description}")
    public void onRegenCommand(MVCommandIssuer issuer,

                               @Syntax("<world>")
                               @Description("{@@mv-core.regen.world.description}")
                               LoadedMultiverseWorld world,

                               @Optional
                               @Syntax("--seed [seed] --reset-gamerules")
                               @Description("{@@mv-core.regen.other.description}")
                               String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        this.commandManager.getCommandQueueManager().addToQueue(new QueuedCommand(
                issuer.getIssuer(),
                () -> {
                    issuer.sendInfo(MVCorei18n.REGEN_REGENERATING, "{world}", world.getName());
                    worldManager.regenWorld(RegenWorldOptions.world(world)
                            .randomSeed(parsedFlags.hasFlag("--seed"))
                            .seed(parsedFlags.flagValue("--seed", String.class))
                            .keepWorldConfig(!parsedFlags.hasFlag("--reset-world-config"))
                            .keepGameRule(!parsedFlags.hasFlag("--reset-gamerules"))
                            .keepWorldBorder(!parsedFlags.hasFlag("--reset-world-border"))
                    ).onSuccess(newWorld -> {
                        Logging.fine("World regen success: " + newWorld);
                        issuer.sendInfo(MVCorei18n.REGEN_SUCCESS, "{world}", newWorld.getName());
                    }).onFailure(failure -> {
                        Logging.fine("World regen failure: " + failure);
                        issuer.sendError(failure.getFailureMessage());
                    });
                },
                this.commandManager.formatMessage(
                        issuer,
                        MessageType.INFO,
                        MVCorei18n.REGEN_PROMPT,
                        "{world}", world.getName())
        ));
    }
}