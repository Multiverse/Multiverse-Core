package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;

@CommandAlias("mv")
public class CreateCommand extends MultiverseCommand {

    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("create")
    @CommandPermission("multiverse.core.info")
    @Syntax("<name> <env> -s [seed] -g [generator[:id]] -t [worldtype] [-n] -a [true|false]")
    @CommandCompletion("")
    @Description("")
    public void onCreateCommand(CommandSender sender, String worldName, World.Environment environment) {
        sender.sendMessage("World name: " + worldName);
        sender.sendMessage("World environment: " + environment.toString());
    }
}
