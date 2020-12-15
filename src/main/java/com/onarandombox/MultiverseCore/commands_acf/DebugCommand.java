package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
@Subcommand("debug")
@CommandPermission("multiverse.core.debug")
public class DebugCommand extends MultiverseCommand {

    public DebugCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Description("Show the current debug level.")
    public void showDebugCommand(@NotNull CommandSender sender) {
        displayDebugMode(sender);
    }

    @Syntax("<level>")
    @Description("Change debug level.")
    public void changeDebugCommand(@NotNull CommandSender sender,
                                   @NotNull String debugLevel) {

        int parsedLevel = parseDebugLevel(debugLevel);
        if (parsedLevel == -1) {
            sender.sendMessage(ChatColor.RED + "Error" + ChatColor.WHITE
                    + " setting debug level. Please use a number 0-3 " + ChatColor.AQUA + "(3 being many many messages!)");
            return;
        }

        this.plugin.getMVConfig().setGlobalDebug(parsedLevel);
        if (!this.plugin.saveMVConfigs()) {
            sender.sendMessage(ChatColor.RED + "Error saving changes to config! See console for more info.");
        }

        displayDebugMode(sender);
    }

    private int parseDebugLevel(String debugLevel) {
        if (debugLevel.equalsIgnoreCase("off")) {
            return 0;
        }
        if (debugLevel.equalsIgnoreCase("on")) {
            return 1;
        }

        try {
            int parsedLevel = Integer.parseInt(debugLevel);
            return (parsedLevel > 3 || parsedLevel < 0) ? parsedLevel : -1;
        }
        catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private void displayDebugMode(CommandSender sender) {
        final int debugLevel = this.plugin.getMVConfig().getGlobalDebug();
        if (debugLevel == 0) {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.RED + "OFF");
        }
        else {
            sender.sendMessage("Multiverse Debug mode is " + ChatColor.GREEN + debugLevel);
            Logging.fine("Multiverse Debug ENABLED");
        }
    }
}
