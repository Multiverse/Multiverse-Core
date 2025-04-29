package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.World;
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
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;
import org.mvplugins.multiverse.core.world.options.ImportWorldOptions;

@Service
class ImportCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final ImportCommand.Flags flags;

    @Inject
    ImportCommand(@NotNull WorldManager worldManager, @NotNull Flags flags) {
        this.worldManager = worldManager;
        this.flags = flags;
    }

    @Subcommand("import")
    @CommandPermission("multiverse.core.import")
    @CommandCompletion("@mvworlds:scope=potential @environments @flags:groupName=" + Flags.NAME)
    @Syntax("<name> <environment> [--generator <generator[:id]> --adjust-spawn --biome <biome>]")
    @Description("{@@mv-core.import.description}")
    void onImportCommand(
            MVCommandIssuer issuer,

            @Conditions("worldname:scope=new")
            @Syntax("<name>")
            @Description("{@@mv-core.import.name.description}")
            String worldName,

            @Syntax("<env>")
            @Description("{@@mv-core.import.env.description}")
            World.Environment environment,

            @Optional
            @Syntax("[--generator <generator[:id]> --adjust-spawn --biome <biome>]")
            @Description("{@@mv-core.import.other.description}")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        issuer.sendInfo(MVCorei18n.IMPORT_IMPORTING, Replace.WORLD.with(worldName));
        worldManager.importWorld(ImportWorldOptions.worldName(worldName)
                        .biome(parsedFlags.flagValue(flags.biome, ""))
                        .environment(environment)
                        .generator(parsedFlags.flagValue(flags.generator, String.class))
                        .useSpawnAdjust(!parsedFlags.hasFlag(flags.noAdjustSpawn)))
                .onSuccess(newWorld -> {
                    Logging.fine("World import success: " + newWorld);
                    issuer.sendInfo(MVCorei18n.IMPORT_SUCCESS, Replace.WORLD.with(newWorld.getName()));
                })
                .onFailure(failure -> {
                    Logging.fine("World import failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }

    @Service
    private static final class Flags extends FlagBuilder {

        private static final String NAME = "mvimport";

        private GeneratorProvider generatorProvider;

        @Inject
        private Flags(@NotNull CommandFlagsManager flagsManager, @NotNull GeneratorProvider generatorProvider) {
            super(NAME, flagsManager);
            this.generatorProvider = generatorProvider;
        }

        private final CommandValueFlag<String> generator = flag(CommandValueFlag
                .builder("--generator", String.class)
                .addAlias("-g")
                .completion(input -> generatorProvider.suggestGeneratorString(input))
                .build());

        private final CommandFlag noAdjustSpawn = flag(CommandFlag.builder("--no-adjust-spawn")
                .addAlias("-n")
                .build());

        private final CommandValueFlag<String> biome = flag(CommandValueFlag.builder("--biome", String.class)
                .addAlias("-b")
                .build());
    }

    @Service
    private static final class LegacyAlias extends ImportCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull WorldManager worldManager, @NotNull Flags flags) {
            super(worldManager, flags);
        }

        @Override
        @CommandAlias("mvimport|mvim")
        void onImportCommand(MVCommandIssuer issuer, String worldName, World.Environment environment, String[] flags) {
            super.onImportCommand(issuer, worldName, environment, flags);
        }
    }
}
