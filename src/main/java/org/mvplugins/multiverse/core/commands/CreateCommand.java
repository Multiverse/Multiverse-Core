package org.mvplugins.multiverse.core.commands;

import java.util.Collections;
import java.util.Random;

import co.aikar.commands.ACFUtil;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
@CommandAlias("mv")
final class CreateCommand extends CoreCommand {

    private final WorldManager worldManager;
    private GeneratorProvider generatorProvider;

    private final CommandValueFlag<String> seedFlag = flag(CommandValueFlag.builder("--seed", String.class)
            .addAlias("-s")
            .completion(input -> Collections.singleton(String.valueOf(ACFUtil.RANDOM.nextLong())))
            .build());

    private final CommandValueFlag<String> generatorFlag = flag(CommandValueFlag
            .builder("--generator", String.class)
            .addAlias("-g")
            .completion(input -> generatorProvider.suggestGeneratorString(input))
            .build());

    private final CommandValueFlag<WorldType> worldTypeFlag = flag(CommandValueFlag
            .enumBuilder("--world-type", WorldType.class)
            .addAlias("-t")
            .build());

    private final CommandFlag noAdjustSpawnFlag = flag(CommandFlag.builder("--no-adjust-spawn")
            .addAlias("-n")
            .build());

    private final CommandFlag noStructuresFlag = flag(CommandFlag.builder("--no-structures")
            .addAlias("-a")
            .build());

    private final CommandValueFlag<Biome> biomeFlag = flag(CommandValueFlag.builder("--biome", Biome.class)
            .addAlias("-b")
            .completion(input -> Lists.newArrayList(Registry.BIOME).stream()
                    .filter(biome -> biome != Biome.CUSTOM)
                    .map(biome -> biome.getKey().getKey())
                    .toList())
            .context(biomeStr -> Registry.BIOME.get(NamespacedKey.minecraft(biomeStr)))
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

    @CommandAlias("mvcreate|mvc")
    @Subcommand("create")
    @CommandPermission("multiverse.core.create")
    @CommandCompletion("@empty @environments @flags:groupName=mvcreatecommand")
    @Syntax("<name> <environment> [--seed <seed> --generator <generator[:id]> --world-type <worldtype> --adjust-spawn "
            + "--no-structures --biome <biome>]")
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
            @Syntax("[--seed <seed> --generator <generator[:id]> --world-type <worldtype> --adjust-spawn "
                    + "--no-structures --biome <biome>]")
            @Description("{@@mv-core.create.flags.description}")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES,
                replace("{worldName}").with(worldName));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_ENVIRONMENT,
                replace("{environment}").with(environment.name()));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_SEED,
                replace("{seed}").with(parsedFlags.flagValue(seedFlag, "RANDOM")));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_WORLDTYPE,
                replace("{worldType}").with(parsedFlags.flagValue(worldTypeFlag, WorldType.NORMAL).name()));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_ADJUSTSPAWN,
                replace("{adjustSpawn}").with(String.valueOf(!parsedFlags.hasFlag(noAdjustSpawnFlag))));
        if (parsedFlags.hasFlag(biomeFlag)) {
            issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_BIOME,
                    replace("{biome}").with(parsedFlags.flagValue(biomeFlag, Biome.CUSTOM).name()));
        }
        if (parsedFlags.hasFlag(generatorFlag)) {
            issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_GENERATOR,
                    replace("{generator}").with(parsedFlags.flagValue(generatorFlag)));
        }
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_STRUCTURES,
                replace("{structures}").with(String.valueOf(!parsedFlags.hasFlag(noStructuresFlag))));

        issuer.sendInfo(MVCorei18n.CREATE_LOADING);

        worldManager.createWorld(CreateWorldOptions.worldName(worldName)
                .biome(parsedFlags.flagValue(biomeFlag, Biome.CUSTOM))
                .environment(environment)
                .seed(parsedFlags.flagValue(seedFlag))
                .worldType(parsedFlags.flagValue(worldTypeFlag, WorldType.NORMAL))
                .useSpawnAdjust(!parsedFlags.hasFlag(noAdjustSpawnFlag))
                .generator(parsedFlags.flagValue(generatorFlag, ""))
                .generateStructures(!parsedFlags.hasFlag(noStructuresFlag)))
                .onSuccess(newWorld -> {
                    Logging.fine("World create success: " + newWorld);
                    issuer.sendInfo(MVCorei18n.CREATE_SUCCESS, Replace.WORLD.with(newWorld.getName()));
                }).onFailure(failure -> {
                    Logging.fine("World create failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }
}
