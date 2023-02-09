package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
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
import com.onarandombox.MultiverseCore.locale.MVCorei18n;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
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
                                throw new InvalidCommandArgument("Invalid world type: " + value);
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
    @CommandCompletion("@empty  @flags:groupName=mvcreate")
    @Syntax("<name> <environment> --seed [seed] --generator [generator[:id]] --world-type [worldtype] --adjust-spawn --no-structures")
    @Description("{@@mv-core.create_description}")
    public void onCreateCommand(BukkitCommandIssuer issuer,

                                @Conditions("worldname:scope=new")
                                @Syntax("<name>")
                                @Description("{@@mv-core.create_name_description}")
                                String worldName,

                                @Syntax("<environment>")
                                @Description("{@@mv-core.create_environment_description}")
                                World.Environment environment,

                                @Optional
                                @Syntax("--seed [seed] --generator [generator[:id]] --world-type [worldtype] --adjust-spawn --no-structures")
                                @Description("{@@mv-core.create_flags_description}")
                                String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES, "{worldName}", worldName);
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_ENVIRONMENT, "{environment}", environment.name());
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_SEED, "{seed}", parsedFlags.flagValue("--seed", "RANDOM", String.class));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_WORLDTYPE, "{worldType}", parsedFlags.flagValue("--world-type", WorldType.NORMAL, WorldType.class).name());
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_ADJUSTSPAWN, "{adjustSpawn}", String.valueOf(parsedFlags.hasFlag("--adjust-spawn")));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_GENERATOR, "{generator}", parsedFlags.flagValue("--generator", "null", String.class));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_STRUCTURES, "{structures}", String.valueOf(!parsedFlags.hasFlag("--no-structures")));

        issuer.sendInfo(MVCorei18n.CREATE_LOADING);

        if (!worldManager.addWorld(
                worldName,
                environment,
                parsedFlags.flagValue("--seed", String.class),
                parsedFlags.flagValue("--world-type", WorldType.NORMAL, WorldType.class),
                parsedFlags.hasFlag("--adjust-spawn"),
                parsedFlags.flagValue("--generator", String.class),
                parsedFlags.hasFlag("--no-structures")
        )) {
            issuer.sendError(MVCorei18n.CREATE_FAILED, "{worldName}", worldName);
            return;
        }
        issuer.sendInfo(MVCorei18n.CREATE_SUCCESS, "{worldName}", worldName);
    }
}
