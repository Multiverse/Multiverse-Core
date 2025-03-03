package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.exceptions.MultiverseException;

@Service
@CommandAlias("mv")
final class ConfigCommand extends CoreCommand {

    private final MVCoreConfig config;

    @Inject
    ConfigCommand(@NotNull MVCommandManager commandManager, @NotNull MVCoreConfig config) {
        super(commandManager);
        this.config = config;
    }

    @CommandAlias("mvconfig|mvconf")
    @Subcommand("config")
    @CommandPermission("multiverse.core.config")
    @CommandCompletion("@mvconfigs @mvconfigvalues")
    @Syntax("<name> [value]")
    @Description("Show or set a config value.")
    void onConfigCommand(
            MVCommandIssuer issuer,

            @Syntax("<name>")
            @Description("The name of the config to set or show.")
            String name,

            @Optional
            @Syntax("[value]")
            @Description("The value to set the config to. If not specified, the current value will be shown.")
            String value) {
        if (value == null) {
            showConfigValue(issuer, name);
            return;
        }
        updateConfigValue(issuer, name, value);
    }

    private void showConfigValue(MVCommandIssuer issuer, String name) {
        config.getStringPropertyHandle().getProperty(name)
                .onSuccess(value -> issuer.sendMessage(name + "is currently set to " + value))
                .onFailure(e -> issuer.sendMessage(e.getMessage()));
    }

    private void updateConfigValue(MVCommandIssuer issuer, String name, String value) {
        // TODO: Update with localization
        config.getStringPropertyHandle().setPropertyString(name, value)
                .onSuccess(ignore -> {
                    config.save();
                    issuer.sendMessage("Successfully set " + name + " to " + value);
                })
                .onFailure(ignore -> issuer.sendMessage("Unable to set " + name + " to " + value + "."))
                .onFailure(MultiverseException.class, e -> Option.of(e.getMVMessage()).peek(issuer::sendError));
    }
}
