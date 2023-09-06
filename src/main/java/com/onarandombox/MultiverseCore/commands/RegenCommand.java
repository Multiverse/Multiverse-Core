package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandValueFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import com.onarandombox.MultiverseCore.commandtools.queue.QueuedCommand;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.worldnew.MVWorld;
import com.onarandombox.MultiverseCore.worldnew.WorldManager;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.util.Collections;
import java.util.Random;

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
                .add(CommandFlag.builder("--keep-gamerules")
                        .addAlias("-k")
                        .build())
                .build());
    }

    @Subcommand("regen")
    @CommandPermission("multiverse.core.regen")
    @CommandCompletion("@mvworlds:scope=loaded @flags:groupName=mvregen")
    @Syntax("<world> --seed [seed] --keep-gamerules")
    @Description("{@@mv-core.regen.description}")
    public void onRegenCommand(BukkitCommandIssuer issuer,

                               @Syntax("<world>")
                               @Description("{@@mv-core.regen.world.description}")
                               MVWorld world,

                               @Optional
                               @Syntax("--seed [seed] --keep-gamerules")
                               @Description("{@@mv-core.regen.other.description}")
                               String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        this.commandManager.getCommandQueueManager().addToQueue(new QueuedCommand(
                issuer.getIssuer(),
                () -> {
                    issuer.sendInfo(MVCorei18n.REGEN_REGENERATING, "{world}", world.getName());
                    worldManager.regenWorld(world);
                    issuer.sendInfo(MVCorei18n.REGEN_SUCCESS, "{world}", world.getName());
                },
                this.commandManager.formatMessage(
                        issuer,
                        MessageType.INFO,
                        MVCorei18n.REGEN_PROMPT,
                        "{world}", world.getName())
        ));
    }
}
