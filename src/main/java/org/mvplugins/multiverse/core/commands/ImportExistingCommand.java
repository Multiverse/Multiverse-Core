package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.Subcommand;
import jakarta.inject.Inject;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
final class ImportExistingCommand extends CoreCommand {

    private final WorldManager worldManager;

    @Inject
    ImportExistingCommand(@NotNull WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Subcommand("import-existing")
    void onImportLoadedCommand(
            MVCommandIssuer issuer,
            World world
    ) {
        worldManager.importBukkitWorld(world)
                .onSuccess(newWorld ->
                        issuer.sendInfo(MVCorei18n.IMPORT_SUCCESS, MessageReplacement.Replace.WORLD.with(newWorld.getName())))
                .onFailure(failure ->
                        issuer.sendError(failure.getFailureMessage()));
    }
}
