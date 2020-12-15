package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("mv")
public class InfoCommand extends MultiverseCommand {

    public InfoCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("info")
    @CommandPermission("multiverse.core.info")
    @Syntax("[world] [page]")
    @CommandCompletion("@mvworlds")
    @Description("")
    public void onInfoCommand(CommandSender sender, MultiverseWorld world, @Default("1") int page) {
        ShowWorldInfo(sender, world, page);
    }

    private void ShowWorldInfo(CommandSender sender, MultiverseWorld world, int page) {
        sender.sendMessage(world.toString());
        sender.sendMessage("Page of " + page);
    }
}
