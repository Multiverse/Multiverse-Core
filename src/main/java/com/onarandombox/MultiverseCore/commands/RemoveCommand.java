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
public class RemoveCommand extends MultiverseCommand {

    private final MVWorldManager worldManager;

    @Inject
    public RemoveCommand(@NotNull MVCommandManager commandManager, @NotNull MVWorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("remove")
    @CommandPermission("multiverse.core.remove")
    @CommandCompletion("@mvworlds:scope=both")
    @Syntax("<world>")
    @Description("{@@mv-core.remove.description}")
    public void onRemoveCommand(BukkitCommandIssuer issuer,

                                @Single
                                @Conditions("mvworlds:scope=both")
                                @Syntax("<world>")
                                @Description("{@@mv-core.remove.world.description}")
                                String worldName
    ) {
        if (!this.worldManager.removeWorldFromConfig(worldName)) {
            issuer.sendError(MVCorei18n.REMOVE_FAILED);
            return;
        }
        issuer.sendInfo(MVCorei18n.REMOVE_SUCCESS,
                "{world}", worldName);
    }
}
