package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.worldnew.MVWorld;
import com.onarandombox.MultiverseCore.worldnew.WorldManager;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class CloneCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    @Inject
    public CloneCommand(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("clone")
    @CommandPermission("multiverse.core.clone")
    @CommandCompletion("@mvworlds:scope=both @empty")
    @Syntax("<world> <new world name>")
    @Description("{@@mv-core.clone.description}")
    public void onCloneCommand(CommandIssuer issuer,

                               @Syntax("<world>")
                               @Description("{@@mv-core.clone.world.description}")
                               MVWorld world,

                               @Single
                               @Syntax("<new world name>")
                               @Description("{@@mv-core.clone.newWorld.description}")
                               String newWorldName
    ) {
        issuer.sendInfo(MVCorei18n.CLONE_CLONING, "{world}", world.getName(), "{newWorld}", newWorldName);
        worldManager.cloneWorld(world, newWorldName);
        issuer.sendInfo(MVCorei18n.CLONE_SUCCESS, "{world}", newWorldName);
    }
}
