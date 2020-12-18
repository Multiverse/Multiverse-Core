package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class CheckCommand extends MultiverseCommand {

    public CheckCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("check")
    @CommandPermission("multiverse.core.debug")
    @Syntax("[player] <destination>")
    @CommandCompletion("@players")
    @Description("Checks to see if a player can go to a destination. Prints debug if false.")
    public void onCheckCommand(@NotNull CommandSender sender,
                               @NotNull @Flags("other,defaultself,fallbackself") Player player,
                               @NotNull MVDestination destination) {

        this.plugin.getMVPerms().tellMeWhyICantDoThis(sender, player, destination);
    }
}
