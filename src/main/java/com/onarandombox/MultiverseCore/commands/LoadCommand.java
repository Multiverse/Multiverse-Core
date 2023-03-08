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
    @Description("Loads a world. World must be already in worlds.yml, else please use /mv import.")
    public void onLoadCommand(BukkitCommandIssuer issuer,

                              @Single
                              @Conditions("validWorldName:scope=unloaded")
                              @Syntax("<world>")
                              @Description("Name of world you want to load.")
                              String worldName
    ) {
        issuer.sendMessage(String.format("Loading world '%s'...", worldName));

        if (!this.worldManager.loadWorld(worldName)) {
            issuer.sendMessage(String.format("Error trying to load world '%s'!", worldName));
            return;
        }
        issuer.sendMessage(String.format("Loaded world '%s'!", worldName));
    }
}
