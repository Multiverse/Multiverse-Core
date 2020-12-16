package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commands_helper.WorldAndPage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class InfoCommand extends MultiverseCommand {

    public InfoCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("info")
    @CommandPermission("multiverse.core.info")
    @Syntax("[world] [page]")
    @CommandCompletion("@MVWorlds @range:1-3")
    @Description("")
    public void onInfoCommand(@NotNull CommandSender sender,
                              @NotNull @Flags("other,defaultself,fallbackself") MultiverseWorld world,
                              @Default("1") int page) {

        //TODO: The actual paged info
        sender.sendMessage(world.toString());
        sender.sendMessage("Page of " + page);
    }
}
