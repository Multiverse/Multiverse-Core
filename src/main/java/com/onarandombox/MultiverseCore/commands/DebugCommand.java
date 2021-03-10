package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class DebugCommand extends MultiverseCoreCommand {

    public DebugCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @Description("Show the current debug level.")
    public void onShowDebugCommand(@NotNull CommandSender sender) {
        this.displayDebugMode(sender);
    }

    @Subcommand("debug")
    @CommandPermission("multiverse.core.debug")
    @Syntax("<level>")
    @CommandCompletion("@toggles|@range:3")
    @Description("Change debug level.")
    public void onChangeDebugCommand(@NotNull CommandSender sender,

                                     @Conditions("debuglevel")
                                     @Syntax("<level>")
                                     @Description("Debug level to set to.")
                                     int level) {

        this.plugin.getMVConfig().setGlobalDebug(level);
        this.saveMVConfigs(sender);
        this.displayDebugMode(sender);
    }

    private void displayDebugMode(@NotNull CommandSender sender) {
        final int debugLevel = this.plugin.getMVConfig().getGlobalDebug();
        if (debugLevel == 0) {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.RED + "OFF");
            return;
        }
        sender.sendMessage("Multiverse Debug mode is at level " + ChatColor.GREEN + debugLevel);
        Logging.fine("Multiverse Debug ENABLED");
    }
}
