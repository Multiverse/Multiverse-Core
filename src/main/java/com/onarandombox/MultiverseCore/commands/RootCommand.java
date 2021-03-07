package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.displaytools.ColorAlternator;
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
                ColorAlternator.with(ChatColor.GOLD, ChatColor.YELLOW),
                "mv"
        );
    }
}
