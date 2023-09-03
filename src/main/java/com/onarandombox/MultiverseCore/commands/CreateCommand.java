package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.commandtools.MVCommandIssuer;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandValueFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.UnsafeCallWrapper;
import com.onarandombox.MultiverseCore.worldnew.WorldManager;
import com.onarandombox.MultiverseCore.worldnew.options.CreateWorldOptions;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@CommandAlias("mv")
public class CreateCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    @Inject
    public CreateCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull UnsafeCallWrapper unsafeCallWrapper
    ) {
        super(commandManager);
        this.worldManager = worldManager;

        registerFlagGroup(CommandFlagGroup.builder("mvcreate")
                .add(CommandValueFlag.builder("--seed", String.class)
                        .addAlias("-s")
                        .completion(() -> Collections.singleton(String.valueOf(new Random().nextLong())))
                        .build())
                .add(CommandValueFlag.builder("--generator", String.class)
                        .addAlias("-g")
                        .completion(() -> Arrays.stream(Bukkit.getServer().getPluginManager().getPlugins())
                                .filter(Plugin::isEnabled)
                                .filter(genplugin -> unsafeCallWrapper.wrap(
                                        () -> genplugin.getDefaultWorldGenerator("world", ""),
                                        genplugin.getName(),
                                        "Get generator"
                                ) != null)
                                .map(genplugin -> genplugin.getDescription().getName())
                                .collect(Collectors.toList()))
                        .build())
                .add(CommandValueFlag.enumBuilder("--world-type", WorldType.class)
                        .addAlias("-t")
                        .build())
                .add(CommandFlag.builder("--no-adjust-spawn")
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
    @Description("{@@mv-core.create.description}")
    public void onCreateCommand(MVCommandIssuer issuer,

                                @Conditions("worldname:scope=new")
                                @Syntax("<name>")
                                @Description("{@@mv-core.create.name.description}")
                                String worldName,

                                @Syntax("<environment>")
                                @Description("{@@mv-core.create.environment.description}")
                                World.Environment environment,

                                @Optional
                                @Syntax("--seed [seed] --generator [generator[:id]] --world-type [worldtype] --adjust-spawn --no-structures")
                                @Description("{@@mv-core.create.flags.description}")
                                String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES, "{worldName}", worldName);
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_ENVIRONMENT, "{environment}", environment.name());
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_SEED, "{seed}", parsedFlags.flagValue("--seed", "RANDOM", String.class));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_WORLDTYPE, "{worldType}", parsedFlags.flagValue("--world-type", WorldType.NORMAL, WorldType.class).name());
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_ADJUSTSPAWN, "{adjustSpawn}", String.valueOf(!parsedFlags.hasFlag("--no-adjust-spawn")));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_GENERATOR, "{generator}", parsedFlags.flagValue("--generator", "", String.class));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_STRUCTURES, "{structures}", String.valueOf(!parsedFlags.hasFlag("--no-structures")));

        issuer.sendInfo(MVCorei18n.CREATE_LOADING);

        worldManager.createWorld(CreateWorldOptions.worldName(worldName)
                .environment(environment)
                .seed(parsedFlags.flagValue("--seed", String.class))
                .worldType(parsedFlags.flagValue("--world-type", WorldType.NORMAL, WorldType.class))
                .useSpawnAdjust(!parsedFlags.hasFlag("--no-adjust-spawn"))
                .generator(parsedFlags.flagValue("--generator", "", String.class))
                .generateStructures(!parsedFlags.hasFlag("--no-structures"))
        ).onSuccess((success) -> {
            Logging.fine("World create success: " + success);
            issuer.sendInfo(success.getReasonMessage());
        }).onFailure((failure) -> {
            Logging.fine("World create failure: " + failure);
            issuer.sendError(failure.getReasonMessage());
        });
    }
}
