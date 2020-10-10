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

import java.io.File;
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
        this.setCommandUsage("/mv version " + ChatColor.GOLD + "[-b|-h|-p] [--include-plugin-list]");
        this.setArgRange(0, 2);
        this.addKey("mv version");
        this.addKey("mvver");
        this.addKey("mvv");
        this.addKey("mvversion");
        this.setPermission("multiverse.core.version",
                "Dumps version info to the console, optionally to pastebin.com with -b, to hastebin.com using -h, or to paste.gg with -p.", PermissionDefault.TRUE);
    }

    private String getLegacyString() {
        return "[Multiverse-Core] Multiverse-Core Version: " + this.plugin.getDescription().getVersion() + '\n'
                + "[Multiverse-Core] Bukkit Version: " + this.plugin.getServer().getVersion() + '\n'
                + "[Multiverse-Core] Loaded Worlds: " + this.plugin.getMVWorldManager().getMVWorlds() + '\n'
                + "[Multiverse-Core] Multiverse Plugins Loaded: " + this.plugin.getPluginCount() + '\n'
                +"[Multiverse-Core] Economy being used: " + plugin.getEconomist().getEconomyName() + '\n'
                + "[Multiverse-Core] Permissions Plugin: " + this.plugin.getMVPerms().getType() + '\n'
                + "[Multiverse-Core] Dumping Config Values: (version " + this.plugin.getMVConfig().getVersion() + ")" + '\n'
                + "[Multiverse-Core]   enforceaccess: " + plugin.getMVConfig().getEnforceAccess() + '\n'
                + "[Multiverse-Core]   prefixchat: " + plugin.getMVConfig().getPrefixChat() + '\n'
                + "[Multiverse-Core]   prefixchatformat: " + plugin.getMVConfig().getPrefixChatFormat() + '\n'
                + "[Multiverse-Core]   useasyncchat: " + plugin.getMVConfig().getUseAsyncChat() + '\n'
                + "[Multiverse-Core]   teleportintercept: " + plugin.getMVConfig().getTeleportIntercept() + '\n'
                + "[Multiverse-Core]   firstspawnoverride: " + plugin.getMVConfig().getFirstSpawnOverride() + '\n'
                + "[Multiverse-Core]   displaypermerrors: " + plugin.getMVConfig().getDisplayPermErrors() + '\n'
                + "[Multiverse-Core]   globaldebug: " + plugin.getMVConfig().getGlobalDebug() + '\n'
                + "[Multiverse-Core]   silentstart: " + plugin.getMVConfig().getSilentStart() + '\n'
                + "[Multiverse-Core]   messagecooldown: " + plugin.getMessaging().getCooldown() + '\n'
                + "[Multiverse-Core]   version: " + plugin.getMVConfig().getVersion() + '\n'
                + "[Multiverse-Core]   firstspawnworld: " + plugin.getMVConfig().getFirstSpawnWorld() + '\n'
                + "[Multiverse-Core]   teleportcooldown: " + plugin.getMVConfig().getTeleportCooldown() + '\n'
                + "[Multiverse-Core]   defaultportalsearch: " + plugin.getMVConfig().isUsingDefaultPortalSearch() + '\n'
                + "[Multiverse-Core]   portalsearchradius: " + plugin.getMVConfig().getPortalSearchRadius() + '\n'
                + "[Multiverse-Core]   autopurge: " + plugin.getMVConfig().isAutoPurgeEnabled() + '\n'
                + "[Multiverse-Core] Special Code: FRN002" + '\n';
    }

    private String getMarkdownString() {
        return "# Multiverse-Core" + '\n'
                + "## Overview" + '\n'
                + "| Name | Value |" + '\n'
                + "| --- | --- |" + '\n'
                + "| Multiverse-Core Version | `" + this.plugin.getDescription().getVersion() + "` |" + '\n'
                + "| Bukkit Version | `" + this.plugin.getServer().getVersion() + "` |" + '\n'
                + "| Loaded Worlds | `" + this.plugin.getMVWorldManager().getMVWorlds() + "` |" + '\n'
                + "| Multiverse Plugins Loaded | `" + this.plugin.getPluginCount() + "` |" + '\n'
                + "| Economy being used | `" + plugin.getEconomist().getEconomyName() + "` |" + '\n'
                + "| Permissions Plugin | `" + this.plugin.getMVPerms().getType() + "` |" + '\n'
                + "## Parsed Config" + '\n'
                + "These are what Multiverse thought the in-memory values of the config were." + "\n\n"
                + "| Config Key  | Value |" + '\n'
                + "| --- | --- |" + '\n'
                + "| version | `" + this.plugin.getMVConfig().getVersion() + "` |" + '\n'
                + "| messagecooldown | `" + plugin.getMessaging().getCooldown() + "` |" + '\n'
                + "| teleportcooldown | `" + plugin.getMVConfig().getTeleportCooldown() + "` |" + '\n'
                + "| worldnameprefix | `" + plugin.getMVConfig().getPrefixChat() + "` |" + '\n'
                + "| worldnameprefixFormat | `" + plugin.getMVConfig().getPrefixChatFormat() + "` |" + '\n'
                + "| enforceaccess | `" + plugin.getMVConfig().getEnforceAccess() + "` |" + '\n'
                + "| displaypermerrors | `" + plugin.getMVConfig().getDisplayPermErrors() + "` |" + '\n'
                + "| teleportintercept | `" + plugin.getMVConfig().getTeleportIntercept() + "` |" + '\n'
                + "| firstspawnoverride | `" + plugin.getMVConfig().getFirstSpawnOverride() + "` |" + '\n'
                + "| firstspawnworld | `" + plugin.getMVConfig().getFirstSpawnWorld() + "` |" + '\n'
                + "| debug | `" + plugin.getMVConfig().getGlobalDebug() + "` |" + '\n';
    }

    private void addVersionInfoToEvent(MVVersionEvent event) {
        // add the legacy version info
        event.appendVersionInfo(this.getLegacyString());

        // add the legacy file, but as markdown so it's readable
        // TODO Readd this in 5.0.0
        // event.putDetailedVersionInfo("version.md", this.getMarkdownString());

        // add config.yml
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");
        event.putDetailedVersionInfo("multiverse-core/config.yml", configFile);

        // add worlds.yml
        File worldsFile = new File(this.plugin.getDataFolder(), "worlds.yml");
        event.putDetailedVersionInfo("multiverse-core/worlds.yml", worldsFile);
    }

    @Override
    public void runCommand(final CommandSender sender, final List<String> args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            sender.sendMessage("Version info dumped to console. Please check your server logs.");
        }

        MVVersionEvent versionEvent = new MVVersionEvent();

        this.addVersionInfoToEvent(versionEvent);
        this.plugin.getServer().getPluginManager().callEvent(versionEvent);

        if (CommandHandler.hasFlag("--include-plugin-list", args)) {
            versionEvent.appendVersionInfo('\n' + "Plugins: " + getPluginList());
            versionEvent.putDetailedVersionInfo("plugins.txt", "Plugins: " + getPluginList());
        }

        final String versionInfo = versionEvent.getVersionInfo();
        versionEvent.putDetailedVersionInfo("version.txt", versionInfo);

        final Map<String, String> files = versionEvent.getDetailedVersionInfo();

        // log to console
        String[] lines = versionInfo.split("\\r?\\n");
        for (String line : lines) {
            if (!line.isEmpty()) {
                this.plugin.getServer().getLogger().info(line);
            }
        }

        BukkitRunnable logPoster = new BukkitRunnable() {
            @Override
            public void run() {
                if (args.size() > 0) {
                    String pasteUrl;
                    if (CommandHandler.hasFlag("-b", args)) {
                        // private post to pastebin
                        pasteUrl = postToService(PasteServiceType.PASTEBIN, true, versionInfo, files);
                    } else if (CommandHandler.hasFlag("-g", args)) {
                        // private post to github
                        pasteUrl = postToService(PasteServiceType.GITHUB, true, versionInfo, files);
                    } else if (CommandHandler.hasFlag("-h", args)) {
                        // private post to hastebin
                        pasteUrl = postToService(PasteServiceType.HASTEBIN, true, versionInfo, files);
                    } else if (CommandHandler.hasFlag("-p", args)) {
                        // private post to paste.gg
                        pasteUrl = postToService(PasteServiceType.PASTEGG, true, versionInfo, files);
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
