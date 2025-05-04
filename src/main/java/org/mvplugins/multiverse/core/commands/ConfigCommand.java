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

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.exceptions.MultiverseException;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;

@Service
class ConfigCommand extends CoreCommand {

    private final CoreConfig config;

    @Inject
    ConfigCommand(@NotNull CoreConfig config) {
        this.config = config;
    }

    @Subcommand("config")
    @CommandPermission("multiverse.core.config")
    @CommandCompletion("@mvconfigs @mvconfigvalues")
    @Syntax("<name> [value]")
    @Description("{@@mv-core.config.description}")
    void onConfigCommand(
            MVCommandIssuer issuer,

            @Syntax("<name>")
            @Description("{@@mv-core.config.name.description}")
            String name,

            @Optional
            @Syntax("[value]")
            @Description("{@@mv-core.config.value.description}")
            String value) {
        if (value == null) {
            showConfigValue(issuer, name);
            return;
        }
        updateConfigValue(issuer, name, value);
    }

    private void showConfigValue(MVCommandIssuer issuer, String name) {
        config.getStringPropertyHandle().getProperty(name)
                .onSuccess(value -> issuer.sendMessage(MVCorei18n.CONFIG_SHOW_SUCCESS,
                                Replace.NAME.with(name),
                                Replace.VALUE.with(value)))
                .onFailure(e -> issuer.sendMessage(MVCorei18n.CONFIG_SHOW_ERROR,
                        Replace.NAME.with(name),
                        Replace.ERROR.with(e)));
    }

    private void updateConfigValue(MVCommandIssuer issuer, String name, String value) {
        config.getStringPropertyHandle().setPropertyString(name, value)
                .onSuccess(ignore -> {
                    config.save();
                    issuer.sendMessage(MVCorei18n.CONFIG_SET_SUCCESS,
                            Replace.NAME.with(name),
                            Replace.VALUE.with(value));
                })
                .onFailure(e -> issuer.sendMessage(MVCorei18n.CONFIG_SET_ERROR,
                        Replace.NAME.with(name),
                        Replace.VALUE.with(value),
                        Replace.ERROR.with(e)));
    }

    @Service
    private final static class LegacyAlias extends ConfigCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull CoreConfig config) {
            super(config);
        }

        @Override
        @CommandAlias("mvconfig|mvconf")
        void onConfigCommand(MVCommandIssuer issuer, String name, String value) {
            super.onConfigCommand(issuer, name, value);
        }
    }
}
