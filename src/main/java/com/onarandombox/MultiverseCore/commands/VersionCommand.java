/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.event.MVVersionRequestEvent;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteFailedException;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteService;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceFactory;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class VersionCommand extends MultiverseCommand {

    public VersionCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("Multiverse Version");
        this.setCommandUsage("/mv version " + ChatColor.GOLD + "-p");
        this.setArgRange(0, 1);
        this.addKey("mv version");
        this.addKey("mvv");
        this.addKey("mvversion");
        this.setPermission("multiverse.core.version", "Dumps version info to the console, optionally to pastebin.com with a -p.", PermissionDefault.TRUE);
    }

    private String pasteBinBuffer = "";

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        if (sender instanceof Player) {
            sender.sendMessage("Version info dumped to console. Please check your server logs.");
        }

        logAndAddToPasteBinBuffer("Multiverse-Core Version: " + this.plugin.getDescription().getVersion());
        logAndAddToPasteBinBuffer("Bukkit Version: " + this.plugin.getServer().getVersion());
        logAndAddToPasteBinBuffer("Loaded Worlds: " + this.plugin.getMVWorldManager().getMVWorlds().size());
        logAndAddToPasteBinBuffer("Multiverse Plugins Loaded: " + this.plugin.getPluginCount());
        logAndAddToPasteBinBuffer("Economy being used: " + this.plugin.getBank().getEconUsed());
        logAndAddToPasteBinBuffer("Permissions Plugin: " + this.plugin.getMVPerms().getType());
        logAndAddToPasteBinBuffer("Dumping Config Values: (version " + this.plugin.getMVConfiguration().getString("version", "NOT SET") + ")");
        logAndAddToPasteBinBuffer("messagecooldown: " + "Not yet IMPLEMENTED");
        logAndAddToPasteBinBuffer("teleportcooldown: " + "Not yet IMPLEMENTED");
        logAndAddToPasteBinBuffer("worldnameprefix: " + MultiverseCore.PrefixChat);
        logAndAddToPasteBinBuffer("enforceaccess: " + MultiverseCore.EnforceAccess);
        logAndAddToPasteBinBuffer("enforcegamemodes: " + MultiverseCore.EnforceGameModes);
        logAndAddToPasteBinBuffer("debug: " + MultiverseCore.GlobalDebug);
        logAndAddToPasteBinBuffer("Special Code: FRN002");

        MVVersionRequestEvent versionEvent = new MVVersionRequestEvent(pasteBinBuffer);
        this.plugin.getServer().getPluginManager().callEvent(versionEvent);
        pasteBinBuffer = versionEvent.getPasteBinBuffer();

        if (args.size() == 1 && args.get(0).equalsIgnoreCase("-p")) {
            String pasteBinUrl = this.postToService(PasteServiceType.PASTIE, true); // private post to pastie
            sender.sendMessage("Version info dumped here: " + ChatColor.GREEN + pasteBinUrl);
            this.plugin.log(Level.INFO, "Version info dumped here: " + pasteBinUrl);
        }
    }

    private void logAndAddToPasteBinBuffer(String string) {
        this.pasteBinBuffer += "[Multiverse-Core] " + string + "\n";
        this.plugin.log(Level.INFO, string);
    }

    /**
     * Send the current contents of this.pasteBinBuffer to a web service.
     *
     * @param type Service type to send to
     * @return URL of visible paste
     */
    private String postToService(PasteServiceType type, boolean isPrivate) {
        PasteService ps = PasteServiceFactory.getService(type, isPrivate);
        try {
            return ps.postData(ps.encodeData(this.pasteBinBuffer), ps.getPostURL());
        } catch (PasteFailedException e) {
            System.out.print(e);
            return "Error posting to service";
        }
    }
}
