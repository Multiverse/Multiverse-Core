/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import buscript.Buscript;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@CommandAlias("mv")
public class ScriptCommand extends MultiverseCoreCommand {

    public ScriptCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("script")
    @CommandPermission("multiverse.core.script")
    @Syntax("<script> [player]")
    @CommandCompletion("@scripts @players")
    @Description("Runs a script.")
    public void onScriptCommand(@NotNull CommandSender sender,

                                @Syntax("<script>")
                                @Description("Script name that you want to run.")
                                @NotNull String targetScript,

                                @Syntax("[player]")
                                @Description("Player that you want to execute the script on.")
                                @NotNull @Flags("other,defaultself") Player player) {

        Buscript scriptAPI = this.plugin.getScriptAPI();
        if (scriptAPI == null) {
            throw new InvalidCommandArgument("Buscript failed to load while server was starting! Scripts cannot be run.", false);
        }

        File file = new File(scriptAPI.getScriptFolder(), targetScript);
        if (!file.exists()) {
            throw new InvalidCommandArgument("That script file does not exist in the Multiverse-Core scripts directory!");
        }

        scriptAPI.executeScript(file, targetScript, player);
        sender.sendMessage(String.format("Script '%s%s%s' finished!", ChatColor.GOLD, file.getName(), ChatColor.WHITE));
    }
}
