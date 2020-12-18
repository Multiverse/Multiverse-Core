package com.onarandombox.MultiverseCore.commands_acf;

import buscript.Buscript;
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
public class ScriptCommand extends MultiverseCommand {

    public ScriptCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("script")
    @CommandPermission("multiverse.core.script")
    @Syntax("<script> [player]")
    @CommandCompletion(" @players")
    @Description("Runs a script.")
    public void onScriptCommand(@NotNull CommandSender sender,
                                @NotNull String targetScript,
                                @NotNull @Flags("other|defaultself") Player player) {

        Buscript scriptAPI = this.plugin.getScriptAPI();
        if (scriptAPI == null) {
            sender.sendMessage("Buscript failed to load while the server was starting. Scripts cannot be run.");
            return;
        }

        File file = new File(scriptAPI.getScriptFolder(), targetScript);
        if (!file.exists()) {
            sender.sendMessage("That script file does not exist in the Multiverse-Core scripts directory!");
            return;
        }

        scriptAPI.executeScript(file, targetScript, player);
        sender.sendMessage(String.format("Script '%s%s%s' finished!", ChatColor.GOLD, file.getName(), ChatColor.WHITE));
    }
}
