package org.mvplugins.multiverse.core.commands;

import java.util.Collections;

import co.aikar.commands.ACFUtil;
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

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.command.flag.FlagBuilder;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.utils.result.Attempt.Failure;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.biomeprovider.BiomeProviderFactory;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;
import org.mvplugins.multiverse.core.world.reasons.CreateFailureReason;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
class CreateCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final CreateCommand.Flags flags;

    @Inject
    CreateCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull Flags flags
    ) {
        super(commandManager);
        this.worldManager = worldManager;
        this.flags = flags;
    }

    @Subcommand("create")
    @CommandPermission("multiverse.core.create")
    @CommandCompletion("@empty @environments @flags:groupName=mvcreate")
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
            String[] flagStrings) {
        Logging.severe("Running /mv create");
        ParsedCommandFlags parsedFlags = flags.parse(flagStrings);

        messageWorldDetails(issuer, worldName, environment, parsedFlags);

        issuer.sendInfo(MVCorei18n.CREATE_LOADING);

        worldManager.createWorld(CreateWorldOptions.worldName(worldName)
                .biome(parsedFlags.flagValue(flags.biomeFlag, ""))
                .environment(environment)
                .seed(parsedFlags.flagValue(flags.seedFlag))
                .worldType(parsedFlags.flagValue(flags.worldTypeFlag, WorldType.NORMAL))
                .useSpawnAdjust(!parsedFlags.hasFlag(flags.noAdjustSpawnFlag))
                .generator(parsedFlags.flagValue(flags.generatorFlag, ""))
                .generatorSettings(parsedFlags.flagValue(flags.generatorSettingsFlag, ""))
                .generateStructures(!parsedFlags.hasFlag(flags.noStructuresFlag)))
                .onSuccess(newWorld -> messageSuccess(issuer, newWorld))
                .onFailure(failure -> messageFailure(issuer, failure));
    }

    private void messageWorldDetails(MVCommandIssuer issuer, String worldName,
                                     World.Environment environment, ParsedCommandFlags parsedFlags) {
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES,
                replace("{worldName}").with(worldName));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_ENVIRONMENT,
                replace("{environment}").with(environment.name()));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_SEED,
                replace("{seed}").with(parsedFlags.flagValue(flags.seedFlag, "RANDOM")));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_WORLDTYPE,
                replace("{worldType}").with(parsedFlags.flagValue(flags.worldTypeFlag, WorldType.NORMAL).name()));
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_ADJUSTSPAWN,
                replace("{adjustSpawn}").with(String.valueOf(!parsedFlags.hasFlag(flags.noAdjustSpawnFlag))));
        if (parsedFlags.hasFlag(flags.biomeFlag)) {
            issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_BIOME,
                    replace("{biome}").with(parsedFlags.flagValue(flags.biomeFlag)));
        }
        if (parsedFlags.hasFlag(flags.generatorFlag)) {
            issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_GENERATOR,
                    replace("{generator}").with(parsedFlags.flagValue(flags.generatorFlag)));
        }
        if (parsedFlags.hasFlag(flags.generatorSettingsFlag)) {
            issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_GENERATORSETTINGS,
                    replace("{generatorSettings}").with(parsedFlags.flagValue(flags.generatorSettingsFlag)));
        }
        issuer.sendInfo(MVCorei18n.CREATE_PROPERTIES_STRUCTURES,
                replace("{structures}").with(String.valueOf(!parsedFlags.hasFlag(flags.noStructuresFlag))));
    }

    private void messageSuccess(MVCommandIssuer issuer, LoadedMultiverseWorld newWorld) {
        Logging.fine("World create success: " + newWorld);
        issuer.sendInfo(MVCorei18n.CREATE_SUCCESS, Replace.WORLD.with(newWorld.getName()));
    }

    private void messageFailure(MVCommandIssuer issuer, Failure<LoadedMultiverseWorld, CreateFailureReason> failure) {
        Logging.fine("World create failure: " + failure);
        issuer.sendError(failure.getFailureMessage());
    }

    @Service
    private static final class Flags extends FlagBuilder {

        private GeneratorProvider generatorProvider;
        private BiomeProviderFactory biomeProviderFactory;

        @Inject
        private Flags(
                @NotNull CommandFlagsManager flagsManager,
                @NotNull GeneratorProvider generatorProvider,
                @NotNull BiomeProviderFactory biomeProviderFactory
        ) {
            super("mvcreate", flagsManager);
            this.generatorProvider = generatorProvider;
            this.biomeProviderFactory = biomeProviderFactory;
        }

        private final CommandValueFlag<String> seedFlag = flag(CommandValueFlag.builder("--seed", String.class)
                .addAlias("-s")
                .completion(input -> Collections.singleton(String.valueOf(ACFUtil.RANDOM.nextLong())))
                .build());

        private final CommandValueFlag<String> generatorFlag = flag(CommandValueFlag
                .builder("--generator", String.class)
                .addAlias("-g")
                .completion(input -> generatorProvider.suggestGeneratorString(input))
                .build());

        private final CommandValueFlag<String> generatorSettingsFlag = flag(CommandValueFlag
                .builder("--generator-settings", String.class)
                .addAlias("-gs")
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

        private final CommandValueFlag<String> biomeFlag = flag(CommandValueFlag.builder("--biome", String.class)
                .addAlias("-b")
                .completion(input -> biomeProviderFactory.suggestBiomeString(input))
                .build());
    }

    @Service
    private static final class LegacyAlias extends CreateCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(
                @NotNull MVCommandManager commandManager,
                @NotNull WorldManager worldManager,
                @NotNull Flags flags
        ) {
            super(commandManager, worldManager, flags);
        }

        @Override
        @CommandAlias("mvcreate|mvc")
        void onCreateCommand(MVCommandIssuer issuer, String worldName, World.Environment environment, String[] flags) {
            Logging.severe("Legacy alias used: /mvcreate");
            super.onCreateCommand(issuer, worldName, environment, flags);
        }

        @Override
        public boolean doFlagRegistration() {
            return false;
        }
    }
}
