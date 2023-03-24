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
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVConfig;
import com.onarandombox.MultiverseCore.commandtools.context.MVConfigValue;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class ConfigCommand extends MultiverseCoreCommand {
    private final MVConfig config;

    public ConfigCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
        this.config = plugin.getMVConfig();
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
        Object currentValue = config.getProperty(name);
        if (currentValue == null) {
            issuer.sendMessage("No such config option: " + name);
            return;
        }
        issuer.sendMessage(name + "is currently set to " + config.getProperty(name));
    }

    private void updateConfigValue(BukkitCommandIssuer issuer, String name, Object value) {
        if (!config.setProperty(name, value)) {
            issuer.sendMessage("Unable to set " + name + " to " + value);
            return;
        }
        config.save();
        issuer.sendMessage("Successfully set " + name + " to " + value);
    }
}
