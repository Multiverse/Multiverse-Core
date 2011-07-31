package com.onarandombox.MultiverseCore.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.utils.FancyColorScheme;
import com.onarandombox.utils.FancyHeader;
import com.onarandombox.utils.FancyMessage;
import com.onarandombox.utils.FancyText;

public class InfoCommand extends MultiverseCommand {

    private static final int CMDS_PER_PAGE = 9;
    private List<String> monsterNames = new ArrayList<String>();
    private List<String> animalNames = new ArrayList<String>();

    public InfoCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("World Information");
        this.setCommandUsage("/mv info" + ChatColor.GOLD + "[PAGE] [WORLD]");
        this.setArgRange(0, 1);
        this.addKey("mvinfo");
        this.addKey("mvi");
        this.addKey("mv info");
        this.setPermission("multiverse.core.info", "Returns detailed information on the world.", PermissionDefault.OP);
        this.generateNames();
    }

    private void generateNames() {
        monsterNames.add("Wolf");
        monsterNames.add("Creeper");
        monsterNames.add("Skeleton");
        monsterNames.add("Spider");
        monsterNames.add("Slime");
        monsterNames.add("Spider Jockey");
        monsterNames.add("Ghast");
        monsterNames.add("Zombie");
        monsterNames.add("Zombie Pigman");

        animalNames.add("Pig");
        animalNames.add("Sheep");
        animalNames.add("Cow");
        animalNames.add("Chicken");
        animalNames.add("Squid");
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        String worldName = "";
        if (sender instanceof Player && args.size() == 0) {
            worldName = ((Player) sender).getWorld().getName();
        } else if (args.size() == 0) {
            sender.sendMessage("You must enter a" + ChatColor.GOLD + " world" + ChatColor.WHITE + " from the console!");
            return;
        } else {
            worldName = args.get(0);
        }
        if (this.plugin.isMVWorld(worldName)) {
            showPage(1, sender, this.buildEntireCommand(this.plugin.getMVWorld(worldName)));
        } else if (this.plugin.getServer().getWorld(worldName) != null) {
            sender.sendMessage("That world exists, but multiverse does not know about it!");
            sender.sendMessage("You can import it with" + ChatColor.AQUA + "/mv import " + ChatColor.GREEN + worldName + ChatColor.LIGHT_PURPLE + "{ENV}");
            sender.sendMessage("For available environments type " + ChatColor.GREEN + "/mv env");
        }
        // Leaving this in so we can have a laugh about it.

        // else {
        // sender.sendMessage("That world does not exist!");
        // sender.sendMessage("You can create it with" + ChatColor.AQUA + "/mv create " + ChatColor.GREEN + worldName + ChatColor.LIGHT_PURPLE + "{ENV}");
        // sender.sendMessage("For available environments type " + ChatColor.GREEN + "/mv env");
        // }
    }

    private List<FancyText> buildEntireCommand(MVWorld world) {
        List<FancyText> message = new ArrayList<FancyText>();
        // World Name: 1
        FancyColorScheme colors = new FancyColorScheme(ChatColor.AQUA, ChatColor.AQUA, ChatColor.GOLD, ChatColor.WHITE);
        message.add(new FancyHeader("General Info", colors));
        message.add(new FancyMessage("World Name: ", world.getName(), colors));
        message.add(new FancyMessage("World Alias: ", world.getColoredWorldString(), colors));
        message.add(new FancyMessage("World Scale: ", world.getScaling().toString(), colors));
        message.add(new FancyHeader("PVP Settings", colors));
        message.add(new FancyMessage("Multiverse Setting: ", world.getPvp().toString(), colors));
        message.add(new FancyMessage("Bukkit Setting: ", world.getCBWorld().getPVP() + "", colors));
        message.add(new FancyMessage("Fake PVP Enabled: ", world.getFakePVP() + "", colors));
        // Next is a blank
        message.add(new FancyMessage(" ", "", colors));
        message.add(new FancyMessage(ChatColor.DARK_PURPLE + "X more pages", "", colors));

        message.add(new FancyHeader("Monster Settings", colors));
        message.add(new FancyMessage("Multiverse Setting: ", world.allowMonsterSpawning().toString(), colors));
        message.add(new FancyMessage("Bukkit Setting: ", world.getCBWorld().getAllowMonsters() + "", colors));
        boolean warnings = false;
        if (MultiverseCore.MobsDisabledInDefaultWorld) {
            message.add(new FancyMessage(ChatColor.RED + "WARNING: ", "Monsters WILL NOT SPAWN IN THIS WORLD.", colors));
            message.add(new FancyMessage(ChatColor.RED + "WARNING: ", "Check your server log for more details.", colors));
        }
        if (world.getMonsterList().size() > 0) {
            if (world.allowMonsterSpawning()) {
                message.add(new FancyMessage("Monsters That" + ChatColor.RED + " CAN NOT " + ChatColor.GREEN + "spawn: ", toCommaSeperated(world.getMonsterList()), colors));
            } else {
                message.add(new FancyMessage("Monsters That" + ChatColor.GREEN + " CAN SPAWN: ", toCommaSeperated(world.getMonsterList()), colors));
            }
        } else {
            message.add(new FancyMessage("Monsters That CAN spawn: ", world.allowMonsterSpawning() ? "ALL" : "NONE", colors));
        }

        return message;
    }

    private String toCommaSeperated(List<String> list) {
        String result = list.get(0);

        for (int i = 1; i < list.size() - 1; i++) {
            result += ", " + list.get(i);
        }
        result += " and " + list.get(list.size() - 1);
        return result;
    }

    protected ChatColor getChatColor(boolean positive) {
        return positive ? ChatColor.GREEN : ChatColor.RED;
    }

    private void showPage(int page, CommandSender sender, List<FancyText> messages) {
        int start = (sender instanceof Player) ? (page - 1) * CMDS_PER_PAGE : 0;
        int end = (sender instanceof Player) ? start + CMDS_PER_PAGE : messages.size();
        boolean altColor = false;
        for (int i = start; i < end; i++) {
            // For consistancy, print some extra lines if it's a player:
            if (i < messages.size()) {

                if (messages.get(i) instanceof FancyMessage) {
                    FancyMessage text = (FancyMessage) messages.get(i);
                    text.setAltColor(altColor);
                    altColor = !altColor;
                    sender.sendMessage(text.getFancyText());
                } else {
                    FancyText text = messages.get(i);
                    sender.sendMessage(text.getFancyText());
                    altColor = false;
                }

            } else if (sender instanceof Player) {
                sender.sendMessage(" ");
            }
        }
    }

}
