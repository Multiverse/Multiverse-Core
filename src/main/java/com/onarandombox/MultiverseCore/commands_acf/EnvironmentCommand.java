package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("mv")
public class EnvironmentCommand extends MultiverseCommand {

    public EnvironmentCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("env|environments")
    @CommandPermission("multiverse.core.list.environments")
    @Description("Lists valid known environments/world types.")
    public void onEnvironmentCommand(CommandSender sender) {
        showEnvironments(sender);
        showWorldTypes(sender);
    }

    /**
     * Shows all valid known environments to a {@link CommandSender}.
     *
     * @param sender The {@link CommandSender}.
     */
    private void showEnvironments(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Valid Environments are:");
        sender.sendMessage(ChatColor.GREEN + "NORMAL");
        sender.sendMessage(ChatColor.RED + "NETHER");
        sender.sendMessage(ChatColor.AQUA + "END");
    }
    /**
     * Shows all valid known world types to a {@link CommandSender}.
     *
     * @param sender The {@link CommandSender}.
     */
    private void showWorldTypes(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Valid World Types are:");
        sender.sendMessage(String.format("%sNORMAL%s, %sFLAT, %sLARGEBIOMES %sor %sAMPLIFIED",
                ChatColor.GREEN, ChatColor.WHITE, ChatColor.AQUA, ChatColor.RED, ChatColor.WHITE, ChatColor.GOLD));
    }
}
