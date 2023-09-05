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
import com.onarandombox.MultiverseCore.worldnew.WorldManager;
import com.onarandombox.MultiverseCore.worldnew.generators.GeneratorProvider;
import com.onarandombox.MultiverseCore.worldnew.options.ImportWorldOptions;
import jakarta.inject.Inject;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class ImportCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    @Inject
    public ImportCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull WorldManager worldManager,
            @NotNull GeneratorProvider generatorProvider
            ) {
        super(commandManager);
        this.worldManager = worldManager;

        registerFlagGroup(CommandFlagGroup.builder("mvimport")
                .add(CommandValueFlag.builder("--generator", String.class)
                        .addAlias("-g")
                        .completion(generatorProvider::suggestGeneratorString)
                        .build())
                .add(CommandFlag.builder("--adjust-spawn")
                        .addAlias("-a")
                        .build())
                .build());
    }

    @Subcommand("import")
    @CommandPermission("multiverse.core.import")
    @CommandCompletion("@mvworlds:scope=potential  @flags:groupName=mvimport")
    @Syntax("<name> <env> --generator [generator[:id]] --adjust-spawn")
    @Description("{@@mv-core.import.description")
    public void onImportCommand(MVCommandIssuer issuer,

                                @Conditions("worldname:scope=new")
                                @Syntax("<name>")
                                @Description("{@@mv-core.import.name.description}")
                                String worldName,

                                @Syntax("<env>")
                                @Description("{@@mv-core.import.env.description}")
                                World.Environment environment,

                                @Optional
                                @Syntax("--generator [generator[:id]] --adjust-spawn")
                                @Description("{@@mv-core.import.other.description}")
                                String[] flags) {

        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendInfo(MVCorei18n.IMPORT_IMPORTING, "{world}", worldName);
        worldManager.importWorld(ImportWorldOptions.worldName(worldName)
                .environment(environment)
                .generator(parsedFlags.flagValue("--generator", String.class))
                .useSpawnAdjust(parsedFlags.hasFlag("--adjust-spawn"))
        ).onSuccess((success) -> {
            Logging.fine("World create success: " + success);
            issuer.sendInfo(success.getReasonMessage());
        }).onFailure((failure) -> {
            Logging.fine("World create failure: " + failure);
            issuer.sendError(failure.getReasonMessage());
        });
    }
}
