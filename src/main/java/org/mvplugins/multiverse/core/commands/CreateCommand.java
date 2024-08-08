package org.mvplugins.multiverse.core.commands;

import java.util.Collections;
import java.util.Random;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;

@Service
@CommandAlias("mv")
class CreateCommand extends MultiverseCommand {

    private final WorldManager worldManager;
    private GeneratorProvider generatorProvider;

    private final CommandValueFlag<String> SEED_FLAG = flag(CommandValueFlag.builder("--seed", String.class)
            .addAlias("-s")
            .completion(input -> Collections.singleton(String.valueOf(new Random().nextLong())))
            .build());

    private final CommandValueFlag<String> GENERATOR_FLAG = flag(CommandValueFlag
            .builder("--generator", String.class)
            .addAlias("-g")
            .completion(input -> generatorProvider.suggestGeneratorString(input))
            .build());

    private final CommandValueFlag<WorldType> WORLD_TYPE_FLAG = flag(CommandValueFlag
            .enumBuilder("--world-type", WorldType.class)
            .addAlias("-t")
            .build());

    private final CommandFlag NO_ADJUST_SPAWN_FLAG = flag(CommandFlag.builder("--no-adjust-spawn")
            .addAlias("-n")
            .build());

    private final CommandFlag NO_STRUCTURES_FLAG = flag(CommandFlag.builder("--no-structures")
            .addAlias("-a")
            .build());

    @Inject
    CreateCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull GeneratorProvider generatorProvider) {
        super(commandManager);
        this.worldManager = worldManager;
        this.generatorProvider = generatorProvider;
    }

    @Subcommand("create")
    @CommandPermission("multiverse.core.create")
    @CommandCompletion("@empty @environments @flags:groupName=mvcreatecommand")
    @Syntax("<name> <environment> --seed [seed] --generator [generator[:id]] --world-type [worldtype] --adjust-spawn --no-structures")
    @Description("{@@mv-core.create.description}")
    void onCreateCommand(
            MVCommandIssuer issuer,

            @Syntax("<name>")
            @Description("{@@mv-core.create.name.description}")
            String worldName,

            @Syntax("<environment>")
            @Description("{@@mv-core.create.environment.description}")
            World.Environment environment,

            @Optional
            @Syntax("--seed [seed] --generator [generator[:id]] --world-type [worldtype] --adjust-spawn --no-structures")
            @Description("{@@mv-core.create.flags.description}")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES,
                "{worldName}", worldName);
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_ENVIRONMENT,
                "{environment}", environment.name());
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_SEED,
                "{seed}", parsedFlags.flagValue(SEED_FLAG, "RANDOM"));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_WORLDTYPE,
                "{worldType}", parsedFlags.flagValue(WORLD_TYPE_FLAG, WorldType.NORMAL).name());
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_ADJUSTSPAWN,
                "{adjustSpawn}", String.valueOf(!parsedFlags.hasFlag(NO_ADJUST_SPAWN_FLAG)));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_GENERATOR,
                "{generator}", parsedFlags.flagValue(GENERATOR_FLAG, ""));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_STRUCTURES,
                "{structures}", String.valueOf(!parsedFlags.hasFlag(NO_STRUCTURES_FLAG)));

        issuer.sendInfo(MVCorei18n.CREATE_LOADING);

        worldManager.createWorld(CreateWorldOptions.worldName(worldName)
                .environment(environment)
                .seed(parsedFlags.flagValue(SEED_FLAG))
                .worldType(parsedFlags.flagValue(WORLD_TYPE_FLAG, WorldType.NORMAL))
                .useSpawnAdjust(!parsedFlags.hasFlag(NO_ADJUST_SPAWN_FLAG))
                .generator(parsedFlags.flagValue(GENERATOR_FLAG, ""))
                .generateStructures(!parsedFlags.hasFlag(NO_STRUCTURES_FLAG)))
                .onSuccess(newWorld -> {
                    Logging.fine("World create success: " + newWorld);
                    issuer.sendInfo(MVCorei18n.CREATE_SUCCESS, "{world}", newWorld.getName());
                }).onFailure(failure -> {
                    Logging.fine("World create failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }
}
