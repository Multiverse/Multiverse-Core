package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.api.MVConfig;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.commandtools.context.MVConfigValue;
import com.onarandombox.MultiverseCore.config.MVCoreConfigProvider;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class ConfigCommand extends MultiverseCommand {

    private final MVCoreConfigProvider configProvider;

    @Inject
    public ConfigCommand(@NotNull MVCommandManager commandManager, @NotNull MVCoreConfigProvider configProvider) {
        super(commandManager);
        this.configProvider = configProvider;
    }

    private MVConfig getConfig() {
        return configProvider.getConfig();
    }

    @Subcommand("config")
    @CommandPermission("multiverse.core.config")
    @CommandCompletion("@mvconfigs")
    @Syntax("<name> [new-value]")
    @Description("") //TODO
    public void onConfigCommand(BukkitCommandIssuer issuer,

                                @Syntax("<name>")
                                @Description("") //TODO
                                String name,

                                @Optional
                                @Single
                                @Syntax("[new-value]")
                                @Description("") //TODO
                                MVConfigValue value
    ) {
        if (value == null) {
            showConfigValue(issuer, name);
            return;
        }
        updateConfigValue(issuer, name, value.getValue());
    }

    private void showConfigValue(BukkitCommandIssuer issuer, String name) {
        Object currentValue = getConfig().getProperty(name);
        if (currentValue == null) {
            issuer.sendMessage("No such config option: " + name);
            return;
        }
        issuer.sendMessage(name + "is currently set to " + getConfig().getProperty(name));
    }

    private void updateConfigValue(BukkitCommandIssuer issuer, String name, Object value) {
        if (!getConfig().setProperty(name, value)) {
            issuer.sendMessage("Unable to set " + name + " to " + value);
            return;
        }
        getConfig().save();
        issuer.sendMessage("Successfully set " + name + " to " + value);
    }
}
