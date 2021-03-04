package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandTools.display.ColorAlternator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RootCommand extends MultiverseCoreCommand {

    public RootCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @CommandAlias("mv")
    public void onRootCommand(@NotNull CommandSender sender) {
        this.plugin.getMVCommandManager().showPluginInfo(
                sender,
                this.plugin.getDescription(),
                new ColorAlternator(ChatColor.GOLD, ChatColor.YELLOW),
                "mv"
        );
    }
}
