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
import com.onarandombox.MultiverseCore.utils.webpaste.BitlyURLShortener;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteFailedException;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteService;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceFactory;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceType;
import com.onarandombox.MultiverseCore.utils.webpaste.URLShortener;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dumps version info to the console.
 */
public class VersionCommand extends MultiverseCommand {
    private static final URLShortener SHORTENER = new BitlyURLShortener();

    public VersionCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Multiverse Version");
        this.setCommandUsage("/mv version " + ChatColor.GOLD + "-[pbg]");
        this.setArgRange(0, 1);
        this.addKey("mv version");
        this.addKey("mvv");
        this.addKey("mvversion");
        this.setPermission("multiverse.core.version",
                "Dumps version info to the console, optionally to pastie.org with -p or pastebin.com with a -b.", PermissionDefault.TRUE);
    }

    private String getLegacyString() {
        StringBuilder legacyFile = new StringBuilder();
        legacyFile.append("[Multiverse-Core] Multiverse-Core Version: ").append(this.plugin.getDescription().getVersion()).append('\n');
        legacyFile.append("[Multiverse-Core] Bukkit Version: ").append(this.plugin.getServer().getVersion()).append('\n');
        legacyFile.append("[Multiverse-Core] Loaded Worlds: ").append(this.plugin.getMVWorldManager().getMVWorlds()).append('\n');
        legacyFile.append("[Multiverse-Core] Multiverse Plugins Loaded: ").append(this.plugin.getPluginCount()).append('\n');
        legacyFile.append("[Multiverse-Core] Economy being used: ").append(plugin.getEconomist().getEconomyName()).append('\n');
        legacyFile.append("[Multiverse-Core] Permissions Plugin: ").append(this.plugin.getMVPerms().getType()).append('\n');
        legacyFile.append("[Multiverse-Core] Dumping Config Values: (version ")
                .append(this.plugin.getMVConfig().getVersion()).append(")").append('\n');
        legacyFile.append("[Multiverse-Core]  messagecooldown: ").append(plugin.getMessaging().getCooldown()).append('\n');
        legacyFile.append("[Multiverse-Core]  teleportcooldown: ").append(plugin.getMVConfig().getTeleportCooldown()).append('\n');
        legacyFile.append("[Multiverse-Core]  worldnameprefix: ").append(plugin.getMVConfig().getPrefixChat()).append('\n');
        legacyFile.append("[Multiverse-Core]  worldnameprefixFormat: ").append(plugin.getMVConfig().getPrefixChatFormat()).append('\n');
        legacyFile.append("[Multiverse-Core]  enforceaccess: ").append(plugin.getMVConfig().getEnforceAccess()).append('\n');
        legacyFile.append("[Multiverse-Core]  displaypermerrors: ").append(plugin.getMVConfig().getDisplayPermErrors()).append('\n');
        legacyFile.append("[Multiverse-Core]  teleportintercept: ").append(plugin.getMVConfig().getTeleportIntercept()).append('\n');
        legacyFile.append("[Multiverse-Core]  firstspawnoverride: ").append(plugin.getMVConfig().getFirstSpawnOverride()).append('\n');
        legacyFile.append("[Multiverse-Core]  firstspawnworld: ").append(plugin.getMVConfig().getFirstSpawnWorld()).append('\n');
        legacyFile.append("[Multiverse-Core]  debug: ").append(plugin.getMVConfig().getGlobalDebug()).append('\n');
        legacyFile.append("[Multiverse-Core] Special Code: FRN002").append('\n');
        return legacyFile.toString();
    }

    private String getMarkdownString() {
        StringBuilder markdownString = new StringBuilder();
        markdownString.append("# Multiverse-Core\n");
        markdownString.append("## Overview\n");
        markdownString.append("| Name | Value |\n");
        markdownString.append("| --- | --- |\n");
        markdownString.append("| Multiverse-Core Version | `").append(this.plugin.getDescription().getVersion()).append("` |\n");
        markdownString.append("| Bukkit Version | `").append(this.plugin.getServer().getVersion()).append("` |\n");
        //markdownString.append("| Loaded Worlds | `").append(this.plugin.getMVWorldManager().getMVWorlds()).append("` |\n");
        markdownString.append("| Multiverse Plugins Loaded | `").append(this.plugin.getPluginCount()).append("` |\n");
        markdownString.append("| Economy being used | `").append(plugin.getEconomist().getEconomyName()).append("` |\n");
        markdownString.append("| Permissions Plugin | `").append(this.plugin.getMVPerms().getType()).append("` |\n");
        markdownString.append("## Parsed Config\n");
        markdownString.append("These are what Multiverse thought the in-memory values of the config were.\n\n");
        markdownString.append("| Config Key  | Value |\n");
        markdownString.append("| --- | --- |\n");
        markdownString.append("| version | `").append(this.plugin.getMVConfig().getVersion()).append("` |\n");
        markdownString.append("| messagecooldown | `").append(plugin.getMessaging().getCooldown()).append("` |\n");
        markdownString.append("| teleportcooldown | `").append(plugin.getMVConfig().getTeleportCooldown()).append("` |\n");
        markdownString.append("| worldnameprefix | `").append(plugin.getMVConfig().getPrefixChat()).append("` |\n");
        markdownString.append("| worldnameprefixFormat | `").append(plugin.getMVConfig().getPrefixChatFormat()).append("` |\n");
        markdownString.append("| enforceaccess | `").append(plugin.getMVConfig().getEnforceAccess()).append("` |\n");
        markdownString.append("| displaypermerrors | `").append(plugin.getMVConfig().getDisplayPermErrors()).append("` |\n");
        markdownString.append("| teleportintercept | `").append(plugin.getMVConfig().getTeleportIntercept()).append("` |\n");
        markdownString.append("| firstspawnoverride | `").append(plugin.getMVConfig().getFirstSpawnOverride()).append("` |\n");
        markdownString.append("| firstspawnworld | `").append(plugin.getMVConfig().getFirstSpawnWorld()).append("` |\n");
        markdownString.append("| debug | `").append(plugin.getMVConfig().getGlobalDebug()).append("` |\n");
        return markdownString.toString();
    }

    private String readFile(final String filename) {
        String result;
        try {
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            result = "";
            while ((line = bufferedReader.readLine()) != null) {
                result += line + '\n';
            }
        } catch (FileNotFoundException e) {
            Logging.severe("Unable to find %s. Here's the traceback: %s", filename, e.getMessage());
            e.printStackTrace();
            result = String.format("ERROR: Could not load: %s", filename);
        } catch (IOException e) {
            Logging.severe("Something bad happend when reading %s. Here's the traceback: %s", filename, e.getMessage());
            e.printStackTrace();
            result = String.format("ERROR: Could not load: %s", filename);
        }
        return result;
    }

    private Map<String, String> getVersionFiles() {
        Map<String, String> files = new HashMap<String, String>();

        // Add the legacy file, but as markdown so it's readable
        files.put("version.md", this.getMarkdownString());

        // Add the config.yml
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");
        files.put(configFile.getName(), this.readFile(configFile.getAbsolutePath()));

        // Add the config.yml
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
        final Map<String, String> files = this.getVersionFiles();
        this.plugin.getServer().getPluginManager().callEvent(versionEvent);

        // log to console
        final String data = versionEvent.getVersionInfo();
        String[] lines = data.split("\n");
        for (String line : lines) {
            Logging.info(line);
        }

        BukkitRunnable logPoster = new BukkitRunnable() {
            @Override
            public void run() {
                if (args.size() == 1) {
                    String pasteUrl;
                    if (args.get(0).equalsIgnoreCase("-p")) {
                        // private post to pastie
                        pasteUrl = postToService(PasteServiceType.PASTIE, true, data, files);
                    } else if (args.get(0).equalsIgnoreCase("-b")) {
                        // private post to pastebin
                        pasteUrl = postToService(PasteServiceType.PASTEBIN, true, data, files);
                    } else if (args.get(0).equalsIgnoreCase("-g")) {
                        // private post to github
                        pasteUrl = postToService(PasteServiceType.GITHUB, true, data, files);
                    } else {
                        return;
                    }

                    sender.sendMessage("Version info dumped here: " + ChatColor.GREEN + pasteUrl);
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
    private static String postToService(PasteServiceType type, boolean isPrivate, String pasteData,
                                        Map<String, String> pasteFiles) {
        PasteService ps = PasteServiceFactory.getService(type, isPrivate);
        try {
            String result;
            if (ps.supportsMultiFile()) {
                result = ps.postData(ps.encodeData(pasteFiles), ps.getPostURL());
            } else {
                result = ps.postData(ps.encodeData(pasteData), ps.getPostURL());
            }
            return SHORTENER.shorten(result);
        } catch (PasteFailedException e) {
            System.out.print(e);
            return "Error posting to service";
        }
    }
}
