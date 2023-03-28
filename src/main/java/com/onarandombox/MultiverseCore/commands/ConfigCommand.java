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
import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class ConfigCommand extends MultiverseCommand {

    private final MVCoreConfig config;

    @Inject
    public ConfigCommand(@NotNull MVCommandManager commandManager, @NotNull MVCoreConfig config) {
        super(commandManager);
        this.config = config;
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
        config.getProperty(name)
                .onSuccess(value -> issuer.sendMessage(name + "is currently set to " + value))
                .onFailure(throwable -> issuer.sendMessage("Unable to get " + name + ": " + throwable.getMessage()));
    }

    private void updateConfigValue(BukkitCommandIssuer issuer, String name, Object value) {
        config.setProperty(name, value)
                .onSuccess(success -> {
                    if (success) {
                        config.save();
                        issuer.sendMessage("Successfully set " + name + " to " + value);
                    } else {
                        issuer.sendMessage("Unable to set " + name + " to " + value);
                    }
                })
                .onFailure(throwable -> issuer.sendMessage("Unable to set " + name + " to " + value + ": " + throwable.getMessage()));
    }
}
