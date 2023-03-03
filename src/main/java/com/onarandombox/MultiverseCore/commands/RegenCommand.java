package com.onarandombox.MultiverseCore.commands;

import java.util.Collections;
import java.util.Random;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandValueFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import com.onarandombox.MultiverseCore.commandtools.queue.QueuedCommand;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class RegenCommand extends MultiverseCoreCommand {
    public RegenCommand(@NotNull MultiverseCore plugin) {
        super(plugin);

        registerFlagGroup(CommandFlagGroup.builder("mvregen")
                .add(CommandValueFlag.builder("--seed", String.class)
                        .addAlias("-s")
                        .completion(() -> Collections.singleton(String.valueOf(new Random().nextLong())))
                        .optional()
                        .build())
                .add(CommandFlag.builder("--keep-gamerules")
                        .addAlias("-k")
                        .build())
                .build());
    }

    @Subcommand("regen")
    @CommandPermission("multiverse.core.regen")
    @CommandCompletion("@mvworlds:scope=both @flags:groupName=mvregen")
    @Syntax("<world> --seed [seed] --keep-gamerules")
    @Description("{@@mv-core.regen.description}")
    public void onRegenCommand(BukkitCommandIssuer issuer,

                               @Conditions("validWorldName:scope=both")
                               @Syntax("<world>")
                               @Description("{@@mv-core.regen.world.description}")
                               String worldName,

                               @Optional
                               @Syntax("--seed [seed] --keep-gamerules")
                               @Description("{@@mv-core.regen.other.description}")
                               String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        this.plugin.getMVCommandManager().getCommandQueueManager().addToQueue(new QueuedCommand(
                issuer.getIssuer(),
                () -> {
                    issuer.sendInfo(MVCorei18n.REGEN_REGENERATING,
                            "{world}", worldName);
                    if (!this.plugin.getMVWorldManager().regenWorld(
                            worldName,
                            parsedFlags.hasFlag("--seed"),
                            !parsedFlags.hasFlagValue("--seed"),
                            parsedFlags.flagValue("--seed", String.class),
                            parsedFlags.hasFlag("--keep-gamerules")
                    )) {
                        issuer.sendError(MVCorei18n.REGEN_FAILED,
                                "{world}", worldName);
                        return;
                    }
                    issuer.sendInfo(MVCorei18n.REGEN_SUCCESS,
                            "{world}", worldName);
                },
                this.plugin.getMVCommandManager().formatMessage(
                        issuer,
                        MessageType.INFO,
                        MVCorei18n.REGEN_PROMPT,
                        "{world}", worldName)
        ));
    }
}
