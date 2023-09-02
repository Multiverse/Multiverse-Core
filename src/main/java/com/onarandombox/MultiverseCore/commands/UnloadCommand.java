package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandIssuer;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.worldnew.WorldManager;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class UnloadCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    @Inject
    public UnloadCommand(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("unload")
    @CommandPermission("multiverse.core.unload")
    @CommandCompletion("@mvworlds")
    @Syntax("<world>")
    @Description("{@@mv-core.unload.description}")
    public void onUnloadCommand(MVCommandIssuer issuer,

                                @Syntax("<world>")
                                @Description("{@@mv-core.unload.world.description}")
                                String worldName // TODO: Use world object
    ) {
        issuer.sendInfo(MVCorei18n.UNLOAD_UNLOADING, "{world}", worldName);
        worldManager.unloadWorld(worldName)
                .onSuccess((success) -> {
                    Logging.fine("World unload success: " + success);
                    issuer.sendInfo(success.getReasonMessage());
                }).onFailure((failure) -> {
                    Logging.fine("World unload failure: " + failure);
                    issuer.sendError(failure.getReasonMessage());
                });
    }
}
