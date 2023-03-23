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
    @CommandCompletion("@mvconfig") //TODO
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
                                String value
    ) {
        if (value == null) {
            issuer.sendMessage(name + "is currently set to " + config.getProperty(name));
            return;
        }
        config.setProperty(name, value);
        config.save();
        issuer.sendMessage("Set " + name + " to " + value);
    }
}
