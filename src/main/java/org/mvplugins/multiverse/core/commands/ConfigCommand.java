package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.commandtools.context.MVConfigValue;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.exceptions.MultiverseException;

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
    @Description("") // TODO: Description
    public void onConfigCommand(MVCommandIssuer issuer,

                                @Syntax("<name>")
                                @Description("") // TODO: Description
                                String name,

                                @Optional
                                @Single
                                @Syntax("[new-value]")
                                @Description("") // TODO: Description
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
                .onFailure(e -> issuer.sendMessage(e.getMessage()));
    }

    private void updateConfigValue(MVCommandIssuer issuer, String name, Object value) {
        config.setProperty(name, value)
                .onSuccess(ignore -> {
                    config.save();
                    issuer.sendMessage("Successfully set " + name + " to " + value);
                })
                .onFailure(ignore -> issuer.sendMessage("Unable to set " + name + " to " + value + "."))
                .onFailure(MultiverseException.class, e -> Option.of(e.getMVMessage()).peek(issuer::sendError));
    }
}
