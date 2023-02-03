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
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.flags.FlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.MVFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.MVValueFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedFlags;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandException;
import org.bukkit.plugin.Plugin;

@CommandAlias("mv")
public class CreateCommand extends MultiverseCommand {

    public CreateCommand(MultiverseCore plugin) {
        super(plugin);

        this.plugin.getCommandManager().getFlagsManager().registerFlagGroup(FlagGroup.builder("mvcreate")
                .add(MVValueFlag.builder("--seed", String.class)
                        .addAlias("-s")
                        .completion(() -> Collections.singleton(String.valueOf(new Random().nextLong())))
                        .build())
                .add(MVValueFlag.builder("--generator", String.class)
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
                .add(MVValueFlag.builder("--world-type", WorldType.class)
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
                .add(MVFlag.builder("--adjust-spawn")
                        .addAlias("-n")
                        .build())
                .add(MVFlag.builder("--no-structures")
                        .addAlias("-a")
                        .build())
                .build());
    }

    @Subcommand("create")
    @CommandPermission("multiverse.core.create")
    @Syntax("<name> <env> -s [seed] -g [generator[:id]] -t [worldtype] [-n] -a [true|false]")
    @CommandCompletion("WORLDNAME  @flags:groupName=mvcreate")
    @Description("") //TODO
    public void onCreateCommand(CommandIssuer issuer,

                                @Syntax("<name>")
                                @Description("") //TODO
                                @Flags("trim")
                                String worldName,

                                @Syntax("<env>")
                                @Description("") //TODO
                                World.Environment environment,

                                @Syntax("[world-flags]")
                                @Description("") //TODO
                                @Optional String[] flags
    ) {
        ParsedFlags parsedFlags = this.plugin.getCommandManager().getFlagsManager().parse("mvcreate", flags);

        issuer.sendMessage(worldName + " " + environment.toString());
        issuer.sendMessage("--seed: " + parsedFlags.hasFlag("--seed") + " " + String.valueOf(parsedFlags.flagValue("--seed", String.class)));
        issuer.sendMessage("--generator: " + parsedFlags.hasFlag("--generator") + " " + String.valueOf(parsedFlags.flagValue("--generator", String.class)));
        issuer.sendMessage("--world-type: " + parsedFlags.hasFlag("--world-type") + " " + String.valueOf(parsedFlags.flagValue("--world-type", WorldType.class)));
        issuer.sendMessage("--adjust-spawn: " + parsedFlags.hasFlag("--adjust-spawn") + " " + String.valueOf(parsedFlags.flagValue("--adjust-spawn", String.class)));
        issuer.sendMessage("--no-structures: " + parsedFlags.hasFlag("--no-structures") + " " + String.valueOf(parsedFlags.flagValue("--no-structures", String.class)));
    }
}
