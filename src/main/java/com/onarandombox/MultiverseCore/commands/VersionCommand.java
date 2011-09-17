package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.event.MVVersionRequestEvent;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        logAndAddToPasteBinBuffer("Loaded Worlds: " + this.plugin.getWorldManager().getMVWorlds().size());
        logAndAddToPasteBinBuffer("Multiverse Plugins Loaded: " + this.plugin.getPluginCount());
        logAndAddToPasteBinBuffer("Economy being used: " + this.plugin.getBank().getEconUsed());
        logAndAddToPasteBinBuffer("Permissions Plugin: " + this.plugin.getPermissions().getType());
        logAndAddToPasteBinBuffer("Dumping Config Values: (version " + this.plugin.getConfig().getString("version", "NOT SET") + ")");
        logAndAddToPasteBinBuffer("messagecooldown: " + this.plugin.getConfig().getString("messagecooldown", "NOT SET"));
        logAndAddToPasteBinBuffer("teleportcooldown: " + this.plugin.getConfig().getString("teleportcooldown", "NOT SET"));
        logAndAddToPasteBinBuffer("worldnameprefix: " + this.plugin.getConfig().getString("worldnameprefix", "NOT SET"));
        logAndAddToPasteBinBuffer("opfallback: " + this.plugin.getConfig().getString("opfallback", "NOT SET"));
        logAndAddToPasteBinBuffer("disableautoheal: " + this.plugin.getConfig().getString("disableautoheal", "NOT SET"));
        logAndAddToPasteBinBuffer("fakepvp: " + this.plugin.getConfig().getString("fakepvp", "NOT SET"));
        logAndAddToPasteBinBuffer("Special Code: FRN001");

        MVVersionRequestEvent versionEvent = new MVVersionRequestEvent(pasteBinBuffer);
        this.plugin.getServer().getPluginManager().callEvent(versionEvent);
        pasteBinBuffer = versionEvent.getPasteBinBuffer();

        if (args.size() == 1 && args.get(0).equalsIgnoreCase("-p")) {
            String pasteBinUrl = postToPasteBin();
            sender.sendMessage("Version info dumped here: " + ChatColor.GREEN + pasteBinUrl);
            this.plugin.log(Level.INFO, "Version info dumped here: " + pasteBinUrl);
        }
    }

    private void logAndAddToPasteBinBuffer(String string) {
        this.pasteBinBuffer += "[Multiverse-Core] " + string + "\n";
        this.plugin.log(Level.INFO, string);
    }

    private String postToPasteBin() {
        try {
            String data = URLEncoder.encode("api_dev_key", "UTF-8") + "=" + URLEncoder.encode("d61d68d31e8e0392b59b50b277411c71", "UTF-8");
            data += "&" + URLEncoder.encode("api_option", "UTF-8") + "=" + URLEncoder.encode("paste", "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_code", "UTF-8") + "=" + URLEncoder.encode(this.pasteBinBuffer, "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_private", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8");
            data += "&" + URLEncoder.encode("api_paste_format", "UTF-8") + "=" + URLEncoder.encode("yaml", "UTF-8");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            data += "&" + URLEncoder.encode("api_paste_name", "UTF-8") + "=" + URLEncoder.encode("Multiverse 2 Dump " + dateFormat.format(date), "UTF-8");
            data += "&" + URLEncoder.encode("api_user_key", "UTF-8") + "=" + URLEncoder.encode("c052ac52d2b0db88d36cc32ca462d151", "UTF-8");
            URL url = new URL("http://pastebin.com/api/api_post.php");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String pasteBinUrl = "";
            while ((line = rd.readLine()) != null) {
                pasteBinUrl = line;
            }
            wr.close();
            rd.close();
            return pasteBinUrl;
        } catch (Exception e) {
            System.out.print(e);
            return "Error Posting to pastebin.com";
        }
    }
}
