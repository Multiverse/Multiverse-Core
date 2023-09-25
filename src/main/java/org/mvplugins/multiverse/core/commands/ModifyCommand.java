package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.configuration.handle.PropertyModifyAction;
import org.mvplugins.multiverse.core.configuration.handle.StringPropertyHandle;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
@CommandAlias("mv")
class ModifyCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    @Inject
    ModifyCommand(@NotNull MVCommandManager commandManager, WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("modify")
    @CommandPermission("multiverse.core.modify")
    @CommandCompletion("@mvworlds:scope=both @propsmodifyaction @mvworldpropsname @mvworldpropsvalue")
    @Syntax("[world] <set|add|remove|reset> <property> <value>")
    @Description("")
    void onModifyCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            @Description("")
            MultiverseWorld world,

            @Syntax("<set|add|remove|reset>")
            @Description("")
            PropertyModifyAction action,

            @Syntax("<property>")
            @Description("")
            String propertyName,

            @Optional
            @Syntax("[value]")
            @Description("")
            String propertyValue) {
        StringPropertyHandle worldPropertyHandle = world.getStringPropertyHandle();
        worldPropertyHandle.modifyProperty(propertyName, propertyValue, action).onSuccess(ignore -> {
            issuer.sendMessage("Property %s set to %s for world %s.".formatted(
                    propertyName,
                    worldPropertyHandle.getProperty(propertyName).getOrNull(),
                    world.getName()));
            worldManager.saveWorldsConfig();
        }).onFailure(exception -> {
            issuer.sendMessage("Failed to %s property %s to %s for world %s.".formatted(
                    action.name().toLowerCase(),
                    propertyName,
                    propertyValue,
                    world.getName()));
            issuer.sendMessage(exception.getMessage());
        });
    }
}
