package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.CommandIssuer;
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

import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
@CommandAlias("mv")
final class LoadCommand extends CoreCommand {

    private final WorldManager worldManager;

    @Inject
    LoadCommand(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @CommandAlias("mvload")
    @Subcommand("load")
    @CommandPermission("multiverse.core.load")
    @CommandCompletion("@mvworlds:scope=unloaded")
    @Syntax("<world>")
    @Description("{@@mv-core.load.description}")
    void onLoadCommand(
            CommandIssuer issuer,

            @Single
            @Conditions("worldname:scope=unloaded")
            @Syntax("<world>")
            @Description("{@@mv-core.load.world.description}")
            String worldName) {
        MVCorei18n.LOAD_LOADING.sendInfo(issuer, Replace.WORLD.with(worldName));
        worldManager.loadWorld(worldName)
                .onSuccess(newWorld -> {
                    Logging.fine("World load success: " + newWorld);
                    MVCorei18n.LOAD_SUCCESS.sendInfo(issuer, Replace.WORLD.with(newWorld.getName()));
                }).onFailure(failure -> {
                    Logging.fine("World load failure: " + failure);
                    failure.getFailureMessage().sendError(issuer);
                });
    }
}
