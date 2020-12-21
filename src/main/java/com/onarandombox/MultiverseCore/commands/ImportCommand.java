package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandTools.WorldFlags;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class ImportCommand extends MultiverseCommand {

    public ImportCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("import")
    @CommandPermission("multiverse.core.import")
    @Syntax("<world> <env> -g [generator[:id]] [-n]")
    @CommandCompletion("@potentialWorlds @environments")
    @Description("Imports a new world of the specified type.")
    public void onImportCommand(@NotNull CommandSender sender,
                                @NotNull @Flags("trim") @Conditions("importableWorldName") String worldName,
                                @NotNull World.Environment environment,
                                @NotNull WorldFlags flags) {

        String resultMessage = (this.plugin.getMVWorldManager().addWorld(worldName,
                environment,
                null,
                null,
                null,
                flags.getGenerator(),
                flags.isSpawnAdjust())
        )
                ? ChatColor.GREEN + "Import complete!"
                : ChatColor.RED + "Failed! See console for more details.";

        Command.broadcastCommandMessage(sender, resultMessage);
    }
}
