package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.List;

public class VanillaImportCommand extends MultiverseCommand {
    private MVWorldManager worldManager;

    public VanillaImportCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Import Vanilla World");
        this.setCommandUsage("/mv vanillaimport" + ChatColor.GREEN + " {NAME}");
        this.setArgRange(1, 1); // SUPPRESS CHECKSTYLE: MagicNumberCheck
        this.addKey("mvvanillaimport");
        this.addKey("mv vanillaimport");
        this.setPermission("multiverse.core.vanillaimport", "Imports a new world with conversion from vanilla to bukkit.", PermissionDefault.OP);
        this.worldManager = this.plugin.getMVWorldManager();
    }

    private String trimWorldName(String userInput) {
        // Removes relative paths.
        return userInput.replaceAll("^[./\\\\]+", "");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = trimWorldName(args.get(0));
        File worldFolder = new File(this.plugin.getServer().getWorldContainer(), worldName);

        boolean hasDat = false;
        boolean hasNether = false;
        boolean hasEnd = false;

        if (worldFolder.isDirectory()) {
            for (File file : worldFolder.listFiles()) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".dat")) {
                    hasDat = true;
                }
                if (file.isDirectory() && file.getName().equals("DIM-1")) {
                    hasNether = true;
                }
                if (file.isDirectory() && file.getName().equals("DIM1")) {
                    hasEnd = true;
                }
            }
        }

        if (!(hasDat && hasNether && hasEnd)) {
            if (!hasDat) {
                sender.sendMessage(String.format("'%s' does not appear to be a world. It is lacking a .dat file.",
                        worldName));
            }
            if (!hasNether) {
                sender.sendMessage(String.format("'%s' does not have a nether world! Missing DIM-1 folder.",
                        worldName));
            }
            if (!hasEnd) {
                sender.sendMessage(String.format("'%s' does not have a nether world! Missing DIM1 folder.",
                        worldName));
            }
            return;
        }

        sender.sendMessage("Importing vanilla world...");
        if (this.plugin.getMVWorldManager().convertVanillaWorld(worldName)) {
            sender.sendMessage(ChatColor.GREEN + "Vanilla world imported!");
        }
        else {
            sender.sendMessage(ChatColor.RED + "Vanilla world could NOT be imported! See console for more details");
        }
    }
}
