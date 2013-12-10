/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * Lists valid known environments.
 */
public class EnvironmentCommand extends MultiverseCommand {

    public EnvironmentCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("List Environments");
        this.setCommandUsage("/mv env");
        this.setArgRange(0, 0);
        this.addKey("mvenv");
        this.addKey("mv env");
        this.addKey("mv type");
        this.addKey("mv environment");
        this.addKey("mv environments");
        this.addCommandExample("/mv env");
        this.setPermission("multiverse.core.list.environments", "Lists valid known environments/world types.", PermissionDefault.OP);
    }

    /**
     * Shows all valid known environments to a {@link CommandSender}.
     *
     * @param sender The {@link CommandSender}.
     */
    public static void showEnvironments(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Valid Environments are:");
        sender.sendMessage(ChatColor.GREEN + "NORMAL");
        sender.sendMessage(ChatColor.RED + "NETHER");
        sender.sendMessage(ChatColor.AQUA + "END");
    }
    /**
     * Shows all valid known world types to a {@link CommandSender}.
     *
     * @param sender The {@link CommandSender}.
     */
    public static void showWorldTypes(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "Valid World Types are:");
        sender.sendMessage(String.format("%sNORMAL%s, %sFLAT, %sLARGEBIOMES %sor %sVERSION_1_1",
                ChatColor.GREEN, ChatColor.WHITE, ChatColor.AQUA, ChatColor.RED, ChatColor.WHITE, ChatColor.GOLD));
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        EnvironmentCommand.showEnvironments(sender);
        EnvironmentCommand.showWorldTypes(sender);
    }

    /**
     * Converts a {@link String} into a {@link WorldType}.
     *
     * @param type The WorldType as a {@link String}
     * @return The WorldType as a {@link WorldType}
     */
    public static WorldType getWorldTypeFromString(String type) {
        // Don't reference the enum directly as there aren't that many, and we can be more forgiving to users this way
        if (type.equalsIgnoreCase("normal")) {
            type = "NORMAL";
        } else if (type.equalsIgnoreCase("flat")) {
            type = "FLAT";
        } else if (type.equalsIgnoreCase("largebiomes")) {
            type = "LARGE_BIOMES";
        } else if (type.equalsIgnoreCase("amplified")) {
            type = "AMPLIFIED";
        }
        try {
            // Now that we've converted a potentially unfriendly value
            // to a friendly one, get it from the ENUM!
            return WorldType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Converts a {@link String} into an {@link org.bukkit.World.Environment}.
     *
     * @param env The environment as {@link String}
     * @return The environment as {@link org.bukkit.World.Environment}
     */
    public static World.Environment getEnvFromString(String env) {
        env = env.toUpperCase();
        // Don't reference the enum directly as there aren't that many, and we can be more forgiving to users this way
        if (env.equalsIgnoreCase("HELL") || env.equalsIgnoreCase("NETHER"))
            env = "NETHER";

        if (env.equalsIgnoreCase("END") || env.equalsIgnoreCase("THEEND") || env.equalsIgnoreCase("STARWARS"))
            env = "THE_END";

        if (env.equalsIgnoreCase("NORMAL") || env.equalsIgnoreCase("WORLD"))
            env = "NORMAL";

        try {
            // Now that we've converted a potentially unfriendly value
            // to a friendly one, get it from the ENUM!
            return World.Environment.valueOf(env);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
