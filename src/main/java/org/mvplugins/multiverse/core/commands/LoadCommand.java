package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.worldnew.WorldManager;

@Service
@CommandAlias("mv")
public class LoadCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    @Inject
    public LoadCommand(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("load")
    @CommandPermission("multiverse.core.load")
    @CommandCompletion("@mvworlds:scope=unloaded")
    @Syntax("<world>")
    @Description("{@@mv-core.load.description}")
    public void onLoadCommand(MVCommandIssuer issuer,

                              @Single
                              @Conditions("worldname:scope=unloaded")
                              @Syntax("<world>")
                              @Description("{@@mv-core.load.world.description}")
                              String worldName
    ) {
        issuer.sendInfo(MVCorei18n.LOAD_LOADING, "{world}", worldName);
        worldManager.loadWorld(worldName)
                .onSuccess(newWorld -> {
                    Logging.fine("World load success: " + newWorld);
                    issuer.sendInfo(MVCorei18n.LOAD_SUCCESS, "{world}", newWorld.getName());
                }).onFailure(failure -> {
                    Logging.fine("World load failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }
}
