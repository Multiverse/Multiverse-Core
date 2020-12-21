package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("mv")
public class GeneratorCommand extends MultiverseCommand {

    public GeneratorCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("gens|generators")
    @CommandPermission("multiverse.core.generator")
    @Description("Shows a list of Loaded Generator Plugins.")
    public void onGeneratorCommand(@NotNull CommandSender sender) {
        //TODO: Figure out why this loggin message exist...
        Logging.info("PLEASE IGNORE the 'Plugin X does not contain any generators' message below!");

        List<String> generators = Arrays.stream(this.plugin.getServer().getPluginManager().getPlugins())
                .filter(Plugin::isEnabled)
                //TODO: Think what if they do not have a world named 'world'
                .filter(p -> p.getDefaultWorldGenerator("world", "") != null)
                .map(p -> p.getDescription().getName())
                .collect(Collectors.toList());

        if (generators.size() == 0) {
             sender.sendMessage(ChatColor.RED + "No Generator Plugins found.");
             return;
        }

        StringBuilder loadedGens = new StringBuilder();
        boolean altColor = false;
        for (String s : generators) {
            loadedGens.append(altColor ? ChatColor.YELLOW : ChatColor.WHITE)
                    .append(s)
                    .append(' ');
            altColor ^= true;
        }

        sender.sendMessage(ChatColor.AQUA + "--- Loaded Generator Plugins ---");
        sender.sendMessage(loadedGens.toString());
    }
}
