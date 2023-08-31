package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import jakarta.inject.Inject;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
public class RootCommand extends MultiverseCommand {

    private final Plugin plugin;

    @Inject
    public RootCommand(@NotNull MVCommandManager commandManager, @NotNull MultiverseCore plugin) {
        super(commandManager);
        this.plugin = plugin;
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
