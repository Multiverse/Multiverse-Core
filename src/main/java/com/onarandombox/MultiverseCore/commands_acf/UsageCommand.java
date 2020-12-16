package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class UsageCommand extends MultiverseCommand {

    public UsageCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("help")
    @HelpCommand
    @CommandPermission("multiverse.core.help")
    // @Syntax("[page]")
    @Description("Show Multiverse Command usage.")
    public void onUsageCommand(@NotNull CommandSender sender,
                               @NotNull CommandHelp help) {

        //TODO: Paging
        help.setPerPage(10);
        help.showHelp();
    }
}
