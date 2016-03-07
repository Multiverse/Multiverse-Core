/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.FancyText;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.utils.FancyColorScheme;
import com.onarandombox.MultiverseCore.utils.FancyHeader;
import com.onarandombox.MultiverseCore.utils.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.ArrayList;
import java.util.List;

// Will use when we can compile with JDK 6
//import com.sun.xml.internal.ws.util.StringUtils;

/**
 * Returns detailed information about a world.
 */
public class InfoCommand extends MultiverseCommand {
    private MVWorldManager worldManager;

    public InfoCommand(MultiverseCore plugin) {
        super(plugin);
        this.setName("World Information");
        this.setCommandUsage("/mv info" + ChatColor.GOLD + " [WORLD] [PAGE]");
        this.setArgRange(0, 2);
        this.addKey("mvinfo");
        this.addKey("mvi");
        this.addKey("mv info");
        this.addCommandExample("/mv info " + ChatColor.GOLD + "1");
        this.addCommandExample("/mv info " + ChatColor.GOLD + "3");
        this.setPermission("multiverse.core.info", "Returns detailed information on the world.", PermissionDefault.OP);
        this.worldManager = this.plugin.getMVWorldManager();
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        // Check if the command was sent from a Player.
        String worldName = "";
        int pageNum = 0;

        if (args.size() == 0) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                worldName = p.getWorld().getName();
            } else {
                sender.sendMessage("You must enter a" + ChatColor.LIGHT_PURPLE + " world" + ChatColor.WHITE + " from the console!");
                return;
            }
        } else if (args.size() == 1) {
            if (this.worldManager.isMVWorld(args.get(0))) {
                // then we have a world!
                worldName = args.get(0);
            } else if (this.worldManager.getUnloadedWorlds().contains(args.get(0))) {
                sender.sendMessage("That world exists, but it is unloaded!");
                sender.sendMessage(String.format("You can load it with: %s/mv load %s", ChatColor.AQUA, args.get(0)));
                return;
            } else {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    worldName = p.getWorld().getName();
                    try {
                        pageNum = Integer.parseInt(args.get(0)) - 1;
                    } catch (NumberFormatException e) {
                        sender.sendMessage("That world does not exist.");
                        return;
                    }
                } else {
                    sender.sendMessage("You must enter a" + ChatColor.LIGHT_PURPLE + " world" + ChatColor.WHITE + " from the console!");
                    return;
                }
            }
        } else if (args.size() == 2) {
            worldName = args.get(0);
            try {
                pageNum = Integer.parseInt(args.get(1)) - 1;
            } catch (NumberFormatException e) {
                pageNum = 0;
            }
        }

        if (this.worldManager.isMVWorld(worldName)) {
            Player p = null;
            if (sender instanceof Player) {
                p = (Player) sender;
            }
            showPage(pageNum, sender, this.buildEntireCommand(this.worldManager.getMVWorld(worldName), p));
        } else if (this.worldManager.getUnloadedWorlds().contains(worldName)) {
            sender.sendMessage("That world exists, but it is unloaded!");
            sender.sendMessage(String.format("You can load it with: %s/mv load %s", ChatColor.AQUA, worldName));
        } else if (this.plugin.getServer().getWorld(worldName) != null) {
            sender.sendMessage("That world exists, but Multiverse does not know about it!");
            sender.sendMessage("You can import it with" + ChatColor.AQUA + "/mv import " + ChatColor.GREEN + worldName + ChatColor.LIGHT_PURPLE + "{ENV}");
            sender.sendMessage("For available environments type " + ChatColor.GREEN + "/mv env");
        }
    }

    private List<List<FancyText>> buildEntireCommand(MultiverseWorld world, Player p) {
        List<FancyText> message = new ArrayList<FancyText>();
        List<List<FancyText>> worldInfo = new ArrayList<List<FancyText>>();
        // Page 1
        FancyColorScheme colors = new FancyColorScheme(ChatColor.AQUA, ChatColor.AQUA, ChatColor.GOLD, ChatColor.WHITE);
        message.add(new FancyHeader("General Info", colors));
        message.add(new FancyMessage("World Name: ", world.getName(), colors));
        message.add(new FancyMessage("World Alias: ", world.getColoredWorldString(), colors));
        message.add(new FancyMessage("Game Mode: ", world.getGameMode().toString(), colors));
        message.add(new FancyMessage("Difficulty: ", world.getDifficulty().toString(), colors));

        //message.add(new FancyMessage("Game Mode: ", StringUtils.capitalize(world.getGameMode().toString()), colors));
        Location spawn = world.getSpawnLocation();
        message.add(new FancyMessage("Spawn Location: ", plugin.getLocationManipulation().strCoords(spawn), colors));
        message.add(new FancyMessage("World Scale: ", String.valueOf(world.getScaling()), colors));
        message.add(new FancyMessage("World Seed: ", String.valueOf(world.getSeed()), colors));
        if (world.getPrice() > 0) {
            message.add(new FancyMessage("Price to enter this world: ",
                    plugin.getEconomist().formatPrice(world.getPrice(), world.getCurrency()),
                    colors));
        } else if (world.getPrice() < 0) {
            message.add(new FancyMessage("Reward for entering this world: ",
                    plugin.getEconomist().formatPrice(-world.getPrice(), world.getCurrency()),
                    colors));
        } else {
            message.add(new FancyMessage("Price to enter this world: ", ChatColor.GREEN + "FREE!", colors));
        }

        if (world.getRespawnToWorld() != null) {
            MultiverseWorld respawn = this.worldManager.getMVWorld(world.getRespawnToWorld());
            if (respawn != null) {
                message.add(new FancyMessage("Players will respawn in: ", respawn.getColoredWorldString(), colors));
            } else {
                message.add(new FancyMessage("Players will respawn in: ", ChatColor.RED + "!!INVALID!!", colors));
            }

        }

        worldInfo.add(message);
        // Page 2
        message = new ArrayList<FancyText>();
        message.add(new FancyHeader("More World Settings", colors));
        message.add(new FancyMessage("World Type: ", world.getWorldType().toString(), colors));
        message.add(new FancyMessage("Structures: ", world.getCBWorld().canGenerateStructures() + "", colors));
        message.add(new FancyMessage("Weather: ", world.isWeatherEnabled() + "", colors));
        message.add(new FancyMessage("Players will get hungry: ", world.getHunger() + "", colors));
        message.add(new FancyMessage("Keep spawn in memory: ", world.isKeepingSpawnInMemory() + "", colors));
        message.add(new FancyHeader("PVP Settings", colors));
        message.add(new FancyMessage("Multiverse Setting: ", world.isPVPEnabled() + "", colors));
        message.add(new FancyMessage("Bukkit Setting: ", world.getCBWorld().getPVP() + "", colors));
        worldInfo.add(message);
        // Page 3
        message = new ArrayList<FancyText>();
        message.add(new FancyHeader("Monster Settings", colors));
        message.add(new FancyMessage("Multiverse Setting: ", world.canMonstersSpawn() + "", colors));
        message.add(new FancyMessage("Bukkit Setting: ", world.getCBWorld().getAllowMonsters() + "", colors));
        if (world.getMonsterList().size() > 0) {
            if (world.canMonstersSpawn()) {
                message.add(new FancyMessage("Monsters that" + ChatColor.RED + " CAN NOT "
                        + ChatColor.GREEN + "spawn: ", toCommaSeperated(world.getMonsterList()), colors));
            } else {
                message.add(new FancyMessage("Monsters that" + ChatColor.GREEN + " CAN SPAWN: ", toCommaSeperated(world.getMonsterList()), colors));
            }
        } else {
            message.add(new FancyMessage("Monsters that CAN spawn: ", world.canMonstersSpawn() ? "ALL" : "NONE", colors));
        }
        worldInfo.add(message);

        // Page 4
        message = new ArrayList<FancyText>();
        message.add(new FancyHeader("Animal Settings", colors));
        message.add(new FancyMessage("Multiverse Setting: ", world.canAnimalsSpawn() + "", colors));
        message.add(new FancyMessage("Bukkit Setting: ", world.getCBWorld().getAllowAnimals() + "", colors));
        if (world.getAnimalList().size() > 0) {
            if (world.canAnimalsSpawn()) {
                message.add(new FancyMessage("Animals that" + ChatColor.RED + " CAN NOT "
                        + ChatColor.GREEN + "spawn: ", toCommaSeperated(world.getAnimalList()), colors));
            } else {
                message.add(new FancyMessage("Animals that" + ChatColor.GREEN + " CAN SPAWN: ", toCommaSeperated(world.getAnimalList()), colors));
            }
        } else {
            message.add(new FancyMessage("Animals that CAN spawn: ", world.canAnimalsSpawn() ? "ALL" : "NONE", colors));
        }
        worldInfo.add(message);

        return worldInfo;
    }

    private static String toCommaSeperated(List<String> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        String result = list.get(0);

        for (int i = 1; i < list.size() - 1; i++) {
            result += ", " + list.get(i);
        }
        result += " and " + list.get(list.size() - 1);
        return result;
    }

    /**
     * Gets a "positive" or "negative" {@link ChatColor}.
     *
     * @param positive Whether this {@link ChatColor} should be "positive".
     * @return The {@link ChatColor}.
     */
    protected ChatColor getChatColor(boolean positive) {
        return positive ? ChatColor.GREEN : ChatColor.RED;
    }

    private static void showPage(int page, CommandSender sender, List<List<FancyText>> doc) {
        page = page < 0 ? 0 : page;
        page = page > doc.size() - 1 ? doc.size() - 1 : page;
        boolean altColor = false;
        boolean appendedPageNum = false;
        if (sender instanceof Player) {
            List<FancyText> list = doc.get(page);
            for (FancyText fancyT : list) {
                if (fancyT instanceof FancyMessage) {
                    FancyMessage text = (FancyMessage) fancyT;
                    text.setAltColor(altColor);
                    altColor = !altColor;
                    sender.sendMessage(text.getFancyText());
                } else if (fancyT instanceof FancyHeader) {
                    FancyHeader text = (FancyHeader) fancyT;
                    if (!appendedPageNum) {
                        text.appendText(ChatColor.DARK_PURPLE + " [ Page " + (page + 1) + " of " + doc.size() + " ]");
                        appendedPageNum = true;
                    }
                    sender.sendMessage(text.getFancyText());
                    altColor = false;
                }
            }

        } else {
            for (List<FancyText> list : doc) {
                for (FancyText fancyT : list) {
                    if (fancyT instanceof FancyMessage) {
                        FancyMessage text = (FancyMessage) fancyT;
                        text.setAltColor(altColor);
                        altColor = !altColor;
                        sender.sendMessage(text.getFancyText());
                    } else {
                        FancyText text = fancyT;
                        if (appendedPageNum) {
                            sender.sendMessage(" ");
                        } else {
                            appendedPageNum = true;
                        }
                        sender.sendMessage(text.getFancyText());
                        altColor = false;
                    }
                }
            }
        }
    }

}
