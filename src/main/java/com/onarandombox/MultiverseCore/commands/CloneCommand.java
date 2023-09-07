package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
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
import com.onarandombox.MultiverseCore.worldnew.MVWorld;
import com.onarandombox.MultiverseCore.worldnew.WorldManager;
import com.onarandombox.MultiverseCore.worldnew.options.CloneWorldOptions;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.util.Collections;
import java.util.Random;

@Service
@CommandAlias("mv")
public class CloneCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    @Inject
    public CloneCommand(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;

        registerFlagGroup(CommandFlagGroup.builder("mvclone")
                .add(CommandFlag.builder("--reset-world-config")
                        .addAlias("-wc")
                        .build())
                .add(CommandFlag.builder("--reset-gamerules")
                        .addAlias("-gm")
                        .build())
                .add(CommandFlag.builder("--reset-world-border")
                        .addAlias("-wb")
                        .build())
                .build());
    }

    @Subcommand("clone")
    @CommandPermission("multiverse.core.clone")
    @CommandCompletion("@mvworlds:scope=both @empty")
    @Syntax("<world> <new world name>")
    @Description("{@@mv-core.clone.description}")
    public void onCloneCommand(MVCommandIssuer issuer,

                               @Syntax("<world>")
                               @Description("{@@mv-core.clone.world.description}")
                               MVWorld world,

                               @Syntax("<new world name>")
                               @Description("{@@mv-core.clone.newWorld.description}")
                               String newWorldName,

                               @Optional
                               @Syntax("") // TODO
                               @Description("{@@mv-core.regen.other.description}")
                               String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendInfo(MVCorei18n.CLONE_CLONING, "{world}", world.getName(), "{newWorld}", newWorldName);
        worldManager.cloneWorld(CloneWorldOptions.fromTo(world, newWorldName)
                .keepWorldConfig(!parsedFlags.hasFlag("--reset-world-config"))
                .keepGameRule(!parsedFlags.hasFlag("--reset-gamerules"))
                .keepWorldBorder(!parsedFlags.hasFlag("--reset-world-border"))
        ).onSuccess((success) -> {
            Logging.fine("World remove success: " + success);
            issuer.sendInfo(success.getReasonMessage());
        }).onFailure((failure) -> {
            Logging.fine("World remove failure: " + failure);
            issuer.sendError(failure.getReasonMessage());
        });
        issuer.sendInfo(MVCorei18n.CLONE_SUCCESS, "{world}", newWorldName);
    }
}
