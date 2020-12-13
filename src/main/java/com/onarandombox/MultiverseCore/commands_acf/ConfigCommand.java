package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

@CommandAlias("mv")
@Subcommand("config")
@CommandPermission("multiverse.core.config")
public class ConfigCommand extends MultiverseCommand {

    public ConfigCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("show")
    @Description("Show Global MV Variables.")
    public void onShowCommand(CommandSender sender) {
        StringBuilder builder = new StringBuilder();
        Map<String, Object> serializedConfig = this.plugin.getMVConfig().serialize();

        for (Map.Entry<String, Object> entry : serializedConfig.entrySet()) {
            builder.append(ChatColor.GREEN)
                    .append(entry.getKey())
                    .append(ChatColor.WHITE).append(" = ").append(ChatColor.GOLD)
                    .append(entry.getValue().toString())
                    .append(ChatColor.WHITE).append(", ");
        }

        String message = builder.toString();
        message = message.substring(0, message.length() - 2);
        sender.sendMessage(message);
    }

    @Subcommand("set")
    @Syntax("<property> <value>")
    @CommandCompletion("@mvconfig")
    @Description("Set Global MV Variables.")
    public void onSetCommand(CommandSender sender, String property, @Single String value) {
        property = property.toLowerCase();

        if (!this.plugin.getMVConfig().setConfigProperty(property, value)) {
            sender.sendMessage(String.format("%sSetting '%s' to '%s' failed!", ChatColor.RED, property, value));
            return;
        }

        // special rule
        if (property.equalsIgnoreCase("firstspawnworld")) {
            // Don't forget to set the world!
            this.plugin.getMVWorldManager().setFirstSpawnWorld(value);
        }

        if (this.plugin.saveMVConfigs()) {
            sender.sendMessage(ChatColor.GREEN + "SUCCESS!" + ChatColor.WHITE + " Values were updated successfully!");
            this.plugin.loadConfigs();
        } else {
            sender.sendMessage(ChatColor.RED + "FAIL!" + ChatColor.WHITE + " Check your console for details!");
        }
    }
}