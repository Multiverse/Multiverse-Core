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
import com.google.common.collect.Lists;
import jakarta.inject.Inject;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.flag.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.generators.GeneratorProvider;
import org.mvplugins.multiverse.core.world.options.ImportWorldOptions;

@Service
@CommandAlias("mv")
final class ImportCommand extends CoreCommand {

    private final WorldManager worldManager;

    private GeneratorProvider generatorProvider;
    private final CommandValueFlag<String> generatorFlag = flag(CommandValueFlag
            .builder("--generator", String.class)
            .addAlias("-g")
            .completion(input -> generatorProvider.suggestGeneratorString(input))
            .build());

    private final CommandFlag noAdjustSpawnFlag = flag(CommandFlag.builder("--no-adjust-spawn")
            .addAlias("-n")
            .build());

    private final CommandValueFlag<String> biomeFlag = flag(CommandValueFlag.builder("--biome", String.class)
            .addAlias("-b")
            .build());

    @Inject
    ImportCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull GeneratorProvider generatorProvider) {
        super(commandManager);
        this.worldManager = worldManager;
        this.generatorProvider = generatorProvider;
    }

    @CommandAlias("mvimport|mvim")
    @Subcommand("import")
    @CommandPermission("multiverse.core.import")
    @CommandCompletion("@mvworlds:scope=potential @environments @flags:groupName=mvimportcommand")
    @Syntax("<name> <env> [--generator <generator[:id]> --adjust-spawn --biome <biome>]")
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
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendInfo(MVCorei18n.IMPORT_IMPORTING, Replace.WORLD.with(worldName));
        worldManager.importWorld(ImportWorldOptions.worldName(worldName)
                .biome(parsedFlags.flagValue(biomeFlag, ""))
                .environment(environment)
                .generator(parsedFlags.flagValue(generatorFlag, String.class))
                .useSpawnAdjust(!parsedFlags.hasFlag(noAdjustSpawnFlag)))
                .onSuccess(newWorld -> {
                    Logging.fine("World import success: " + newWorld);
                    issuer.sendInfo(MVCorei18n.IMPORT_SUCCESS, Replace.WORLD.with(newWorld.getName()));
                })
                .onFailure(failure -> {
                    Logging.fine("World import failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }
}
