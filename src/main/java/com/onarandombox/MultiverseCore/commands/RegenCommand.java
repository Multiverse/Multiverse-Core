package com.onarandombox.MultiverseCore.commands;

import java.util.Collections;
import java.util.Random;

import co.aikar.commands.BukkitCommandIssuer;
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
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class RegenCommand extends MultiverseCommand {
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
    @Description("Regenerates a world on your server. The previous state will be lost PERMANENTLY.")
    public void onRegenCommand(BukkitCommandIssuer issuer,

                               @Conditions("worldname:scope=both")
                               @Syntax("<world>")
                               @Description("World that you want to regen.")
                               String worldName,

                               @Optional
                               @Syntax("--seed [seed] --keep-gamerules")
                               @Description("Other world settings. See: http://gg.gg/nn8lk")
                               String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        this.plugin.getCommandManager().getCommandQueueManager().addToQueue(new QueuedCommand(
                issuer.getIssuer(),
                () -> {
                    issuer.sendMessage(String.format("Regenerating world '%s'...", worldName));
                    if (!this.plugin.getMVWorldManager().regenWorld(
                            worldName,
                            parsedFlags.hasFlag("--seed"),
                            !parsedFlags.hasFlagValue("--seed"),
                            parsedFlags.flagValue("--seed", String.class),
                            parsedFlags.hasFlag("--keep-gamerules")
                    )) {
                        issuer.sendMessage(String.format("%sThere was an issue regenerating '%s'! Please check console for errors.", ChatColor.RED, worldName));
                        return;
                    }
                    issuer.sendMessage(String.format("%sWorld %s was regenerated!", ChatColor.GREEN, worldName));
                },
                "Are you sure you want to regenerate world '" + worldName + "'?"
        ));
    }
}
