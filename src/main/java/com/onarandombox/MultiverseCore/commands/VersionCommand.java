/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.event.MVVersionEvent;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteFailedException;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteService;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceFactory;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceType;
import com.onarandombox.MultiverseCore.utils.webpaste.URLShortener;
import com.onarandombox.MultiverseCore.utils.webpaste.URLShortenerFactory;
import com.onarandombox.MultiverseCore.utils.webpaste.URLShortenerType;
import com.pneumaticraft.commandhandler.CommandHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dumps version info to the console.
 */
public class VersionCommand extends MultiverseCommand {
    private static final URLShortener SHORTENER = URLShortenerFactory.getService(URLShortenerType.BITLY);

    public VersionCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Multiverse Version");
        this.setCommandUsage("/mv version " + ChatColor.GOLD + "-[bhp] [--include-plugin-list]");
        this.setArgRange(0, 2);
        this.addKey("mv version");
        this.addKey("mvv");
        this.addKey("mvversion");
        this.setPermission("multiverse.core.version",
                "Dumps version info to the console, optionally to pastebin.com with -b, to hastebin.com using -h, or to paste.gg with -p.", PermissionDefault.TRUE);
    }

    private String getLegacyString() {
        return "[Multiverse-Core] Multiverse-Core Version: " + this.plugin.getDescription().getVersion() + System.lineSeparator() +
                "[Multiverse-Core] Bukkit Version: " + this.plugin.getServer().getVersion() + System.lineSeparator() +
                "[Multiverse-Core] Loaded Worlds: " + this.plugin.getMVWorldManager().getMVWorlds() + System.lineSeparator() +
                "[Multiverse-Core] Multiverse Plugins Loaded: " + this.plugin.getPluginCount() + System.lineSeparator() +
                "[Multiverse-Core] Economy being used: " + plugin.getEconomist().getEconomyName() + System.lineSeparator() +
                "[Multiverse-Core] Permissions Plugin: " + this.plugin.getMVPerms().getType() + System.lineSeparator() +
                "[Multiverse-Core] Dumping Config Values: (version " + this.plugin.getMVConfig().getVersion() + ")" + System.lineSeparator() +
                "[Multiverse-Core]   messagecooldown: " + plugin.getMessaging().getCooldown() + System.lineSeparator() +
                "[Multiverse-Core]   teleportcooldown: " + plugin.getMVConfig().getTeleportCooldown() + System.lineSeparator() +
                "[Multiverse-Core]   worldnameprefix: " + plugin.getMVConfig().getPrefixChat() + System.lineSeparator() +
                "[Multiverse-Core]   worldnameprefixFormat: " + plugin.getMVConfig().getPrefixChatFormat() + System.lineSeparator() +
                "[Multiverse-Core]   enforceaccess: " + plugin.getMVConfig().getEnforceAccess() + System.lineSeparator() +
                "[Multiverse-Core]   displaypermerrors: " + plugin.getMVConfig().getDisplayPermErrors() + System.lineSeparator() +
                "[Multiverse-Core]   teleportintercept: " + plugin.getMVConfig().getTeleportIntercept() + System.lineSeparator() +
                "[Multiverse-Core]   firstspawnoverride: " + plugin.getMVConfig().getFirstSpawnOverride() + System.lineSeparator() +
                "[Multiverse-Core]   firstspawnworld: " + plugin.getMVConfig().getFirstSpawnWorld() + System.lineSeparator() +
                "[Multiverse-Core]   debug: " + plugin.getMVConfig().getGlobalDebug() + System.lineSeparator() +
                "[Multiverse-Core] Special Code: FRN002" + System.lineSeparator();
    }

    private String getMarkdownString() {
        return "# Multiverse-Core" + System.lineSeparator() +
                "## Overview" + System.lineSeparator() +
                "| Name | Value |" + System.lineSeparator() +
                "| --- | --- |" + System.lineSeparator() +
                "| Multiverse-Core Version | `" + this.plugin.getDescription().getVersion() + "` |" + System.lineSeparator() +
                "| Bukkit Version | `" + this.plugin.getServer().getVersion() + "` |" + System.lineSeparator() +
                "| Loaded Worlds | `" + this.plugin.getMVWorldManager().getMVWorlds() + "` |" + System.lineSeparator() +
                "| Multiverse Plugins Loaded | `" + this.plugin.getPluginCount() + "` |" + System.lineSeparator() +
                "| Economy being used | `" + plugin.getEconomist().getEconomyName() + "` |" + System.lineSeparator() +
                "| Permissions Plugin | `" + this.plugin.getMVPerms().getType() + "` |" + System.lineSeparator() +
                "## Parsed Config" + System.lineSeparator() +
                "These are what Multiverse thought the in-memory values of the config were." + System.lineSeparator() + System.lineSeparator() +
                "| Config Key  | Value |" + System.lineSeparator() +
                "| --- | --- |" + System.lineSeparator() +
                "| version | `" + this.plugin.getMVConfig().getVersion() + "` |" + System.lineSeparator() +
                "| messagecooldown | `" + plugin.getMessaging().getCooldown() + "` |" + System.lineSeparator() +
                "| teleportcooldown | `" + plugin.getMVConfig().getTeleportCooldown() + "` |" + System.lineSeparator() +
                "| worldnameprefix | `" + plugin.getMVConfig().getPrefixChat() + "` |" + System.lineSeparator() +
                "| worldnameprefixFormat | `" + plugin.getMVConfig().getPrefixChatFormat() + "` |" + System.lineSeparator() +
                "| enforceaccess | `" + plugin.getMVConfig().getEnforceAccess() + "` |" + System.lineSeparator() +
                "| displaypermerrors | `" + plugin.getMVConfig().getDisplayPermErrors() + "` |" + System.lineSeparator() +
                "| teleportintercept | `" + plugin.getMVConfig().getTeleportIntercept() + "` |" + System.lineSeparator() +
                "| firstspawnoverride | `" + plugin.getMVConfig().getFirstSpawnOverride() + "` |" + System.lineSeparator() +
                "| firstspawnworld | `" + plugin.getMVConfig().getFirstSpawnWorld() + "` |" + System.lineSeparator() +
                "| debug | `" + plugin.getMVConfig().getGlobalDebug() + "` |" + System.lineSeparator();
    }

    private String readFile(final String filename) {
        StringBuilder result;
        try {
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            result = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append(System.lineSeparator());
            }
        } catch (FileNotFoundException e) {
            Logging.severe("Unable to find %s. Here's the traceback: %s", filename, e.getMessage());
            e.printStackTrace();
            result = new StringBuilder(String.format("ERROR: Could not load: %s", filename));
        } catch (IOException e) {
            Logging.severe("Something bad happend when reading %s. Here's the traceback: %s", filename, e.getMessage());
            e.printStackTrace();
            result = new StringBuilder(String.format("ERROR: Could not load: %s", filename));
        }
        return result.toString();
    }

    private Map<String, String> getVersionFiles() {
        Map<String, String> files = new HashMap<String, String>();

        // Add the legacy file, but as markdown so it's readable
        files.put("version.md", this.getMarkdownString());

        // Add the config.yml
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");
        files.put(configFile.getName(), this.readFile(configFile.getAbsolutePath()));

        // Add the worlds.yml
        File worldConfig = new File(this.plugin.getDataFolder(), "worlds.yml");
        files.put(worldConfig.getName(), this.readFile(worldConfig.getAbsolutePath()));
        return files;
    }

    @Override
    public void runCommand(final CommandSender sender, final List<String> args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            sender.sendMessage("Version info dumped to console. Please check your server logs.");
        }

        MVVersionEvent versionEvent = new MVVersionEvent(this.getLegacyString(), this.getVersionFiles());
        this.plugin.getServer().getPluginManager().callEvent(versionEvent);

        String versionInfo = versionEvent.getVersionInfo();
        Map<String, String> files = versionEvent.getDetailedVersionInfo();

        if (CommandHandler.hasFlag("--include-plugin-list", args)) {
            versionInfo = versionInfo + System.lineSeparator() + "Plugins: " + getPluginList();
            files.put("plugins.txt", "Plugins: " + getPluginList());
        }

        final String data = versionInfo;

        // log to console
        String[] lines = data.split(System.lineSeparator());
        for (String line : lines) {
            if (!line.isEmpty()) {
                Logging.info(line);
            }
        }

        BukkitRunnable logPoster = new BukkitRunnable() {
            @Override
            public void run() {
                if (args.size() > 0) {
                    String pasteUrl;
                    if (CommandHandler.hasFlag("-b", args)) {
                        // private post to pastebin
                        pasteUrl = postToService(PasteServiceType.PASTEBIN, true, data, files);
                    } else if (CommandHandler.hasFlag("-g", args)) {
                        // private post to github
                        pasteUrl = postToService(PasteServiceType.GITHUB, true, data, files);
                    } else if (CommandHandler.hasFlag("-h", args)) {
                        // private post to hastebin
                        pasteUrl = postToService(PasteServiceType.HASTEBIN, true, data, files);
                    } else if (CommandHandler.hasFlag("-p", args)) {
                        // private post to paste.gg
                        pasteUrl = postToService(PasteServiceType.PASTEGG, true, data, files);
                    } else {
                        return;
                    }

                    if (!(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage("Version info dumped here: " + ChatColor.GREEN + pasteUrl);
                    }
                    Logging.info("Version info dumped here: %s", pasteUrl);
                }
            }
        };

        // Run the log posting operation asynchronously, since we don't know how long it will take.
        logPoster.runTaskAsynchronously(this.plugin);
    }

    /**
     * Send the current contents of this.pasteBinBuffer to a web service.
     *
     * @param type       Service type to send paste data to.
     * @param isPrivate  Should the paste be marked as private.
     * @param pasteData  Legacy string only data to post to a service.
     * @param pasteFiles Map of filenames/contents of debug info.
     * @return URL of visible paste
     */
    private static String postToService(PasteServiceType type, boolean isPrivate, String pasteData, Map<String, String> pasteFiles) {
        PasteService ps = PasteServiceFactory.getService(type, isPrivate);

        try {
            String result;
            if (ps.supportsMultiFile()) {
                result = ps.postData(pasteFiles);
            } else {
                result = ps.postData(pasteData);
            }

            if (SHORTENER != null) return SHORTENER.shorten(result);
            return result;
        } catch (PasteFailedException e) {
            e.printStackTrace();
            return "Error posting to service.";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "That service isn't supported yet.";
        }
    }

    private String getPluginList() {
        return StringUtils.join(plugin.getServer().getPluginManager().getPlugins(), ", ");
    }
}
