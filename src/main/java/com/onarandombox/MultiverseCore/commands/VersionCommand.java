/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandtools.flag.FlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flag.FlagResult;
import com.onarandombox.MultiverseCore.commandtools.flag.CoreFlags;
import com.onarandombox.MultiverseCore.event.MVVersionEvent;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteFailedException;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteService;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceFactory;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceType;
import com.onarandombox.MultiverseCore.utils.webpaste.URLShortener;
import com.onarandombox.MultiverseCore.utils.webpaste.URLShortenerFactory;
import com.onarandombox.MultiverseCore.utils.webpaste.URLShortenerType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

@CommandAlias("mv")
public class VersionCommand extends MultiverseCoreCommand {

    private static final URLShortener SHORTENER = URLShortenerFactory.getService(URLShortenerType.BITLY);

    public VersionCommand(MultiverseCore plugin) {
        super(plugin);
        this.setFlagGroup(FlagGroup.of(
                CoreFlags.PASTE_SERVICE_TYPE,
                CoreFlags.INCLUDE_PLUGIN_LIST
        ));
    }

    @Subcommand("version")
    @CommandPermission("multiverse.core.version")
    @Syntax("--paste [pastebin|hastebin|pastegg] [--include-plugin-list]")
    @CommandCompletion("@flags")
    @Description("Dumps version info to the console, optionally to pastal service.")
    public void onVersionCommand(@NotNull CommandSender sender,

                                 @NotNull
                                 @Syntax("[paste-service]")
                                 @Description("Website to upload your version info to.")
                                 String[] flagsArray) {

        FlagResult flags = FlagResult.parse(flagsArray, this.getFlagGroup());

        MVVersionEvent versionEvent = new MVVersionEvent();
        this.addVersionInfoToEvent(versionEvent);
        this.plugin.getServer().getPluginManager().callEvent(versionEvent);

        if (flags.getValue(CoreFlags.INCLUDE_PLUGIN_LIST)) {
            versionEvent.appendVersionInfo('\n' + "Plugins: " + getPluginList());
            versionEvent.putDetailedVersionInfo("plugins.txt", "Plugins: " + getPluginList());
        }

        String versionInfo = versionEvent.getVersionInfo();
        versionEvent.putDetailedVersionInfo("version.txt", versionInfo);

        Map<String, String> files = versionEvent.getDetailedVersionInfo();

        logToConsole(versionInfo);

        PasteServiceType pasteType = flags.getValue(CoreFlags.PASTE_SERVICE_TYPE);
        if (pasteType == PasteServiceType.NONE && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage("Version info dumped to console! Please check your server logs.");
            return;
        }

        BukkitRunnable logPoster = new BukkitRunnable() {
            @Override
            public void run() {
                String pasteUrl = postToService(pasteType, true, versionInfo, files);
                sender.sendMessage("Version info dumped here: " + ChatColor.GREEN + pasteUrl);
            }
        };

        // Run the log posting operation asynchronously, since we don't know how long it will take.
        logPoster.runTaskAsynchronously(this.plugin);
    }

    private void logToConsole(String versionInfo) {
        Arrays.stream(versionInfo.split("\\r?\\n"))
                .filter(line -> !line.isEmpty())
                .forEach(this.plugin.getServer().getLogger()::info);
    }

    private void addVersionInfoToEvent(MVVersionEvent event) {
        // add the legacy version info
        event.appendVersionInfo(this.getLegacyString());

        // add the legacy file, but as markdown so it's readable
        // TODO API: Readd this in 5.0.0
        // event.putDetailedVersionInfo("version.md", this.getMarkdownString());

        // add config.yml
        File configFile = new File(this.plugin.getDataFolder(), "config.yml");
        event.putDetailedVersionInfo("multiverse-core/config.yml", configFile);

        // add worlds.yml
        File worldsFile = new File(this.plugin.getDataFolder(), "worlds.yml");
        event.putDetailedVersionInfo("multiverse-core/worlds.yml", worldsFile);
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
            String result = (ps.supportsMultiFile())
                    ? ps.postData(pasteFiles)
                    : ps.postData(pasteData);

            return (SHORTENER != null) ? SHORTENER.shorten(result) : result;
        }
        catch (PasteFailedException e) {
            e.printStackTrace();
            return String.format("%sError posting to service.", ChatColor.RED);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
            return String.format("%sThat service isn't supported yet.", ChatColor.RED);
        }
    }

    private String getLegacyString() {
        return "[Multiverse-Core] Multiverse-Core Version: " + this.plugin.getDescription().getVersion() + '\n'
                + "[Multiverse-Core] Bukkit Version: " + this.plugin.getServer().getVersion() + '\n'
                + "[Multiverse-Core] Loaded Worlds: " + this.plugin.getMVWorldManager().getMVWorlds() + '\n'
                + "[Multiverse-Core] Multiverse Plugins Loaded: " + this.plugin.getPluginCount() + '\n'
                + "[Multiverse-Core] Economy being used: " + plugin.getEconomist().getEconomyName() + '\n'
                + "[Multiverse-Core] Permissions Plugin: " + this.plugin.getMVPerms().getType() + '\n'
                + "[Multiverse-Core] Dumping Config Values: (version " + this.plugin.getMVConfig().getVersion() + ")" + '\n'
                + "[Multiverse-Core]   enforceaccess: " + this.plugin.getMVConfig().getEnforceAccess() + '\n'
                + "[Multiverse-Core]   prefixchat: " + this.plugin.getMVConfig().getPrefixChat() + '\n'
                + "[Multiverse-Core]   prefixchatformat: " + this.plugin.getMVConfig().getPrefixChatFormat() + '\n'
                + "[Multiverse-Core]   useasyncchat: " + this.plugin.getMVConfig().getUseAsyncChat() + '\n'
                + "[Multiverse-Core]   teleportintercept: " + this.plugin.getMVConfig().getTeleportIntercept() + '\n'
                + "[Multiverse-Core]   firstspawnoverride: " + this.plugin.getMVConfig().getFirstSpawnOverride() + '\n'
                + "[Multiverse-Core]   displaypermerrors: " + this.plugin.getMVConfig().getDisplayPermErrors() + '\n'
                + "[Multiverse-Core]   globaldebug: " + this.plugin.getMVConfig().getGlobalDebug() + '\n'
                + "[Multiverse-Core]   enablebuscript: " + plugin.getMVConfig().getEnableBuscript() + '\n'
                + "[Multiverse-Core]   silentstart: " + this.plugin.getMVConfig().getSilentStart() + '\n'
                + "[Multiverse-Core]   messagecooldown: " + this.plugin.getMessaging().getCooldown() + '\n'
                + "[Multiverse-Core]   version: " + this.plugin.getMVConfig().getVersion() + '\n'
                + "[Multiverse-Core]   firstspawnworld: " + this.plugin.getMVConfig().getFirstSpawnWorld() + '\n'
                + "[Multiverse-Core]   teleportcooldown: " + this.plugin.getMVConfig().getTeleportCooldown() + '\n'
                + "[Multiverse-Core]   defaultportalsearch: " +this. plugin.getMVConfig().isUsingDefaultPortalSearch() + '\n'
                + "[Multiverse-Core]   portalsearchradius: " + this.plugin.getMVConfig().getPortalSearchRadius() + '\n'
                + "[Multiverse-Core]   autopurge: " +this. plugin.getMVConfig().isAutoPurgeEnabled() + '\n'
                + "[Multiverse-Core] Special Code: FRN002" + '\n';
    }

    private String getPluginList() {
        return StringUtils.join(this.plugin.getServer().getPluginManager().getPlugins(), ", ");
    }
}
