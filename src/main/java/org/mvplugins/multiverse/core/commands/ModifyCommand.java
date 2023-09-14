package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Optional;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.configuration.handle.ConfigModifyType;
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

    /* /mv modify [world] <set|add|remove|reset> <property> <value> */
    @Subcommand("modify")
    @CommandPermission("multiverse.core.modify")
    @CommandCompletion("@mvworlds:scope=both @configmodifytype @mvworldpropsname @mvworldpropsvalue")
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
            ConfigModifyType configModifyType,

            @Syntax("<property>")
            @Description("")
            String propertyName,

            @Optional
            @Single
            @Syntax("[value]")
            @Description("")
            String propertyValue) {
        Logging.fine("ModifyCommand.onModifyCommand: world=%s, configModifyType=%s, propertyName=%s, propertyValue=%s",
                world, configModifyType, propertyName, propertyValue);

        world.modifyProperty(configModifyType, propertyName, propertyValue).onSuccess(ignore -> {
            issuer.sendMessage("Property " + propertyName + " set to " + world.getProperty(propertyName).getOrNull()
                    + " for world " + world.getName() + ".");
            worldManager.saveWorldsConfig();
        }).onFailure(exception -> {
            issuer.sendMessage("Failed to " + configModifyType.name().toLowerCase() + " property " + propertyName
                    + " to " + propertyValue + " for world " + world.getName() + ".");
            issuer.sendMessage(exception.getMessage());
        });
    }
}
