package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandValueFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandException;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CreateCommand extends MultiverseCommand {

    public CreateCommand(@NotNull MultiverseCore plugin) {
        super(plugin);

        registerFlagGroup(CommandFlagGroup.builder("mvcreate")
                .add(CommandValueFlag.builder("--seed", String.class)
                        .addAlias("-s")
                        .completion(() -> Collections.singleton(String.valueOf(new Random().nextLong())))
                        .build())
                .add(CommandValueFlag.builder("--generator", String.class)
                        .addAlias("-g")
                        .completion(() -> Arrays.stream(Bukkit.getServer().getPluginManager().getPlugins())
                                .filter(Plugin::isEnabled)
                                .filter(genplugin -> this.plugin.getUnsafeCallWrapper().wrap(
                                        () -> genplugin.getDefaultWorldGenerator("world", ""),
                                        genplugin.getName(),
                                        "Get generator"
                                ) != null)
                                .map(genplugin -> genplugin.getDescription().getName())
                                .collect(Collectors.toList()))
                        .build())
                .add(CommandValueFlag.builder("--world-type", WorldType.class)
                        .addAlias("-t")
                        .context((value) -> {
                            try {
                                return WorldType.valueOf(value.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                throw new CommandException("Invalid world type: " + value);
                            }
                        })
                        .completion(() -> {
                            List<String> types = new ArrayList<>();
                            for (WorldType type : WorldType.values()) {
                                types.add(type.name().toLowerCase());
                            }
                            return types;
                        })
                        .build())
                .add(CommandFlag.builder("--adjust-spawn")
                        .addAlias("-n")
                        .build())
                .add(CommandFlag.builder("--no-structures")
                        .addAlias("-a")
                        .build())
                .build());
    }

    @Subcommand("create")
    @CommandPermission("multiverse.core.create")
    @CommandCompletion("WORLDNAME  @flags:groupName=mvcreate")
    @Syntax("<name> <env> -s [seed] -g [generator[:id]] -t [worldtype] [-n] -a [true|false]")
    @Description("") //TODO
    public void onCreateCommand(CommandIssuer issuer,

                                @Syntax("<name>")
                                @Description("") //TODO
                                String worldName,

                                @Syntax("<env>")
                                @Description("") //TODO
                                World.Environment environment,

                                @Optional
                                @Syntax("[world-flags]")
                                @Description("") //TODO
                                String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendMessage(worldName + " " + environment.toString());
        issuer.sendMessage("--seed: " + parsedFlags.hasFlag("--seed") + " " + String.valueOf(parsedFlags.flagValue("--seed", String.class)));
        issuer.sendMessage("--generator: " + parsedFlags.hasFlag("--generator") + " " + String.valueOf(parsedFlags.flagValue("--generator", String.class)));
        issuer.sendMessage("--world-type: " + parsedFlags.hasFlag("--world-type") + " " + String.valueOf(parsedFlags.flagValue("--world-type", WorldType.class)));
        issuer.sendMessage("--adjust-spawn: " + parsedFlags.hasFlag("--adjust-spawn") + " " + String.valueOf(parsedFlags.flagValue("--adjust-spawn", String.class)));
        issuer.sendMessage("--no-structures: " + parsedFlags.hasFlag("--no-structures") + " " + String.valueOf(parsedFlags.flagValue("--no-structures", String.class)));
    }
}
