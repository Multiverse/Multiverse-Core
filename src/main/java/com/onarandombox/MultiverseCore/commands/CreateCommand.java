/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.localization.MultiverseMessage;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates a new world and loads it.
 */
public class CreateCommand extends MultiverseCommand {
    private MVWorldManager worldManager;

    public CreateCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Create World");
        this.setCommandUsage(String.format("/mv create %s{NAME} {ENV} %s-s [SEED] -g [GENERATOR[:ID]] -t [WORLDTYPE] [-n] -a [true|false]",
                ChatColor.GREEN, ChatColor.GOLD));
        this.setArgRange(2, 11); // SUPPRESS CHECKSTYLE: MagicNumberCheck
        this.addKey("mvcreate");
        this.addKey("mvc");
        this.addKey("mv create");
        this.setPermission("multiverse.core.create", "Creates a new world and loads it.", PermissionDefault.OP);
        this.addCommandExample("/mv create " + ChatColor.GOLD + "world" + ChatColor.GREEN + " normal");
        this.addCommandExample("/mv create " + ChatColor.GOLD + "lavaland" + ChatColor.RED + " nether");
        this.addCommandExample("/mv create " + ChatColor.GOLD + "starwars" + ChatColor.AQUA + " end");
        this.addCommandExample("/mv create " + ChatColor.GOLD + "flatroom" + ChatColor.GREEN + " normal" + ChatColor.AQUA + " -t flat");
        this.addCommandExample("/mv create " + ChatColor.GOLD + "gargamel" + ChatColor.GREEN + " normal" + ChatColor.DARK_AQUA + " -s gargamel");
        this.addCommandExample("/mv create " + ChatColor.GOLD + "moonworld" + ChatColor.GREEN + " normal" + ChatColor.DARK_AQUA + " -g BukkitFullOfMoon");
        this.worldManager = this.plugin.getMVWorldManager();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String worldName = args.get(0);
        File worldFile = new File(this.plugin.getServer().getWorldContainer(), worldName);
        String env = args.get(1);
        String seed = CommandHandler.getFlag("-s", args);
        String generator = CommandHandler.getFlag("-g", args);
        boolean allowStructures = true;
        String structureString = CommandHandler.getFlag("-a", args);
        if (structureString != null) {
            allowStructures = Boolean.parseBoolean(structureString);
        }
        String typeString = CommandHandler.getFlag("-t", args);
        boolean useSpawnAdjust = true;
        for (String s : args) {
            if (s.equalsIgnoreCase("-n")) {
                useSpawnAdjust = false;
            }
        }

        if (this.worldManager.isMVWorld(worldName)) {
            this.messaging.sendMessage(sender, MultiverseMessage.CMD_CREATE_WORLDEXISTS, worldName);
            return;
        }

        if (worldFile.exists()) {
            this.messaging.sendMessage(sender, MultiverseMessage.CMD_CREATE_FILEEXISTS);
            return;
        }

        Environment environment = EnvironmentCommand.getEnvFromString(env);
        if (environment == null) {
            this.messaging.sendMessage(sender, MultiverseMessage.CMD_CREATE_INVALIDENV);
            EnvironmentCommand.showEnvironments(sender);
            return;
        }

        // If they didn't specify a type, default to NORMAL
        if (typeString == null) {
            typeString = "NORMAL";
        }
        WorldType type = EnvironmentCommand.getWorldTypeFromString(typeString);
        if (type == null) {
            this.messaging.sendMessage(sender, MultiverseMessage.CMD_CREATE_INVALIDTYPE);
            EnvironmentCommand.showWorldTypes(sender);
            return;
        }
        // Determine if the generator is valid. #918
        if (generator != null) {
            List<String> genarray = new ArrayList<String>(Arrays.asList(generator.split(":")));
            if (genarray.size() < 2) {
                // If there was only one arg specified, pad with another empty one.
                genarray.add("");
            }
            if (this.worldManager.getChunkGenerator(genarray.get(0), genarray.get(1), "test") == null) {
                // We have an invalid generator.
                this.messaging.sendMessage(sender, MultiverseMessage.CMD_CREATE_INVALIDGEN, generator);
                return;
            }
        }
        Command.broadcastCommandMessage(sender, this.plugin.getMessageProvider().getMessage(MultiverseMessage.CMD_CREATE_START, worldName));

        if (this.worldManager.addWorld(worldName, environment, seed, type, allowStructures, generator, useSpawnAdjust)) {
            Command.broadcastCommandMessage(sender, this.plugin.getMessageProvider().getMessage(MultiverseMessage.CMD_CREATE_COMPLETE));
        } else {
            Command.broadcastCommandMessage(sender, this.plugin.getMessageProvider().getMessage(MultiverseMessage.CMD_CREATE_FAILED));
        }
    }
}
