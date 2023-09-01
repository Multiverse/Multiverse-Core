package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class LoadCommand extends MultiverseCommand {

    private final MVWorldManager worldManager;

    @Inject
    public LoadCommand(@NotNull MVCommandManager commandManager, @NotNull MVWorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("load")
    @CommandPermission("multiverse.core.load")
    @CommandCompletion("@mvworlds:scope=unloaded")
    @Syntax("<world>")
    @Description("{@@mv-core.load.description}")
    public void onLoadCommand(BukkitCommandIssuer issuer,

                              @Single
                              @Conditions("worldname:scope=unloaded")
                              @Syntax("<world>")
                              @Description("{@@mv-core.load.world.description}")
                              String worldName
    ) {
        issuer.sendInfo(MVCorei18n.LOAD_LOADING,
            "{world}", worldName);

        if (!this.worldManager.loadWorld(worldName)) {
            issuer.sendError(MVCorei18n.LOAD_FAILED,
                    "{world}", worldName);
            return;
        }
        issuer.sendInfo(MVCorei18n.LOAD_SUCCESS,
                "{world}", worldName);
    }
}
