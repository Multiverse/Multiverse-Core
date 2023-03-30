package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.commandtools.MVCommandIssuer;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.commandtools.context.MVConfigValue;
import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import com.onarandombox.MultiverseCore.exceptions.MultiverseException;
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
    public void onConfigCommand(MVCommandIssuer issuer,

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

    private void showConfigValue(MVCommandIssuer issuer, String name) {
        config.getProperty(name)
                .onSuccess(value -> issuer.sendMessage(name + "is currently set to " + value))
                .onFailure(e -> issuer.sendMessage("Unable to get " + name + ": " + e.getMessage()));
    }

    private void updateConfigValue(MVCommandIssuer issuer, String name, Object value) {
        config.setProperty(name, value)
                .onSuccess(ignore -> {
                    config.save();
                    issuer.sendMessage("Successfully set " + name + " to " + value);
                })
                .onFailure(e -> {
                    issuer.sendMessage("Unable to set " + name + " to " + value + ".");
                    if (e instanceof MultiverseException) {
                        var message = ((MultiverseException) e).getMVMessage();
                        if (message != null) {
                            issuer.sendError(message);
                        }
                    }
                });
    }
}
