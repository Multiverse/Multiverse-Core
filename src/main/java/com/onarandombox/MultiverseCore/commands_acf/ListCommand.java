package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class ListCommand extends MultiverseCommand {

    public ListCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.list.worlds")
    @Syntax("[page]")
    @Description("Displays a listing of all worlds that you can enter.")
    public void onListCommand(@NotNull CommandSender sender,
                              @Default("1") int page) {

        //TODO: Do the actual fancy list display
        sender.sendMessage(this.plugin.getMVWorldManager().getMVWorlds().toString());
        sender.sendMessage("Page of: " + page);
    }
}
