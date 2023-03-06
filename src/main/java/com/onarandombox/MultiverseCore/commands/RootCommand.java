package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

public class RootCommand extends MultiverseCoreCommand {
    public RootCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @CommandAlias("mv")
    public void onRootCommand(CommandIssuer issuer) {
        PluginDescriptionFile description = this.plugin.getDescription();
        issuer.sendInfo(MVCorei18n.ROOT_TITLE,
                "{name}", description.getName(),
                "{version}", description.getVersion());
        issuer.sendInfo(MVCorei18n.ROOT_HELP);
    }
}
