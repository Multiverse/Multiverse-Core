package com.onarandombox.MultiverseCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.config.Configuration;

enum EnglishChatColor {
    AQUA("AQUA", ChatColor.AQUA),
    BLACK("BLACK", ChatColor.BLACK),
    BLUE("BLUE", ChatColor.BLUE),
    DARKAQUA("DARKAQUA", ChatColor.DARK_AQUA),
    DARKBLUE("DARKBLUE", ChatColor.DARK_BLUE),
    DARKGRAY("DARKGRAY", ChatColor.DARK_GRAY),
    DARKGREEN("DARKGREEN", ChatColor.DARK_GREEN),
    DARKPURPLE("DARKPURPLE", ChatColor.DARK_PURPLE),
    DARKRED("DARKRED", ChatColor.DARK_RED),
    GOLD("GOLD", ChatColor.GOLD),
    GRAY("GRAY", ChatColor.GRAY),
    GREEN("GREEN", ChatColor.GREEN),
    LIGHTPURPLE("LIGHTPURPLE", ChatColor.LIGHT_PURPLE),
    RED("RED", ChatColor.RED),
    YELLOW("YELLOW", ChatColor.YELLOW),
    WHITE("WHITE", ChatColor.WHITE);
    private ChatColor color;
    private String text;

    EnglishChatColor(String name, ChatColor color) {
        this.color = color;
        this.text = name;
    }

    public String getText() {
        return this.text;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public static EnglishChatColor fromString(String text) {
        if (text != null) {
            for (EnglishChatColor c : EnglishChatColor.values()) {
                if (text.equalsIgnoreCase(c.text)) {
                    return c;
                }
            }
        }
        return EnglishChatColor.WHITE;
    }
}

public class MVWorld {

    private MultiverseCore plugin; // Hold the Plugin Instance.
    private Configuration config; // Hold the Configuration File.

    private World world; // The World Instance.
    private Environment environment; // Hold the Environment type EG Environment.NETHER / Environment.NORMAL
    private Long seed;

    private String name; // The Worlds Name, EG its folder name.
    private String alias = ""; // Short Alias for the World, this will be used in Chat Prefixes.
    private ChatColor aliasColor; // Color for this world

    private boolean allowAnimals; // Does this World allow Animals to Spawn?
    // public List<String> animals = new ArrayList<String>(); // Contain a list of Animals which we want to ignore the Spawn Setting.

    private boolean allowMonsters; // Does this World allow Monsters to Spawn?
    // public List<String> monsters = new ArrayList<String>(); // Contain a list of Monsters which we want to ignore the Spawn Setting.

    private Boolean pvp; // Does this World allow PVP?
    private Boolean fakepvp;

    private String respawnWorld; // Contains the name of the World to respawn the player to

    private List<Integer> blockBlacklist; // Contain a list of Blocks which we won't allow on this World.

    private HashMap<String, List<String>> masterList;

    private Double scaling; // How stretched/compressed distances are
    /**
     * The generator as a string. This is used only for reporting. ex: BukkitFullOfMoon:GenID
     */
    private String generator;
    private Permission permission;

    public MVWorld(World world, Configuration config, MultiverseCore instance, Long seed, String generatorString) {
        this.config = config;
        this.plugin = instance;

        // Set local values that CANNOT be changed by user
        this.world = world;
        this.name = world.getName();
        this.generator = generatorString;
        this.seed = seed;
        this.environment = world.getEnvironment();

        // Initialize our lists
        this.initLists();

        // Write these files to the config (once it's saved)
        if (generatorString != null) {
            config.setProperty("worlds." + this.name + ".generator", this.generator);
        }
        if (seed != null) {
            config.setProperty("worlds." + this.name + ".seed", this.seed);
        }
        config.setProperty("worlds." + this.name + ".environment", this.environment.toString());

        // Set local values that CAN be changed by the user
        this.setAlias(config.getString("worlds." + this.name + ".alias.name", ""));
        this.setAliasColor(config.getString("worlds." + this.name + ".alias.color", ChatColor.WHITE.toString()));
        this.setPvp(config.getBoolean("worlds." + this.name + ".pvp", true));
        this.setScaling(config.getDouble("worlds." + this.name + ".scale", 1.0));
        this.setRespawnToWorld(config.getString("worlds." + this.name + ".respawnworld", ""));

        this.setAnimals(config.getBoolean("worlds." + this.name + ".animals.spawn", true));
        this.setMonsters(config.getBoolean("worlds." + this.name + ".monsters.spawn", true));
        this.getMobExceptions();

        this.getWorldBlacklist().addAll(config.getStringList("worlds." + this.name + ".worldblacklist", new ArrayList<String>()));
        this.getBlockBlacklist().addAll(config.getIntList("worlds." + this.name + ".blockblacklist", new ArrayList<Integer>()));
        this.translateTempSpawn(config);

        config.save();
        this.permission = new Permission("multiverse.access." + this.getName(), "Allows access to " + this.getName(), PermissionDefault.TRUE);
        try {
        this.plugin.getServer().getPluginManager().addPermission(this.permission);
        } catch (IllegalArgumentException e) {
        }
        // The following 3 lines will add some sample data to new worlds created.
        // if (config.getIntList("worlds." + name + ".blockBlacklist", new ArrayList<Integer>()).size() == 0) {
        // addSampleData();
        // }
    }

    private void translateTempSpawn(Configuration config) {
        String tempspawn = config.getString("worlds." + this.name + ".tempspawn", "");
        if (tempspawn.length() > 0) {
            String[] coordsString = tempspawn.split(":");
            if (coordsString.length >= 3) {
                int[] coords = new int[3];
                try {
                    for (int i = 0; i < 3; i++) {

                        coords[i] = Integer.parseInt(coordsString[i]);
                    }
                    this.world.setSpawnLocation(coords[0], coords[1], coords[2]);
                } catch (NumberFormatException e) {
                    this.plugin.log(Level.WARNING, "A MV1 spawn value was found, but it could not be migrated. Format Error. Sorry.");
                }
            } else {
                this.plugin.log(Level.WARNING, "A MV1 spawn value was found, but it could not be migrated. Format Error. Sorry.");
            }

            this.config.removeProperty("worlds." + this.name + ".tempspawn");
        }
    }

    public String getColoredWorldString() {
        ChatColor color = this.getAliasColor();
        if (color == null) {
            if (this.environment == Environment.NETHER) {
                color = ChatColor.RED;
            } else if (this.environment == Environment.NORMAL) {
                color = ChatColor.GREEN;
            } else if (this.environment == Environment.SKYLANDS) {
                color = ChatColor.AQUA;
            }
        }
        return this.getAliasColor() + this.getAlias() + ChatColor.WHITE;
    }

    private void getMobExceptions() {
        List<String> temp;
        temp = this.config.getStringList("worlds." + this.name + ".animals.exceptions", new ArrayList<String>());
        // Add Animals to the exclusion list

        for (String s : temp) {
            this.masterList.get("animals").add(s.toUpperCase());
        }
        temp = this.config.getStringList("worlds." + this.name + ".monsters.exceptions", new ArrayList<String>());
        // Add Monsters to the exclusion list
        for (String s : temp) {
            this.masterList.get("monsters").add(s.toUpperCase());
        }
    }

    public World getCBWorld() {
        return this.world;
    }

    private void initLists() {
        this.masterList = new HashMap<String, List<String>>();
        this.blockBlacklist = new ArrayList<Integer>();
        // Only int list, we don't need to add it to the masterlist
        this.masterList.put("worldblacklist", new ArrayList<String>());
        this.masterList.put("animals", new ArrayList<String>());
        this.masterList.put("monsters", new ArrayList<String>());
    }

    public void addSampleData() {
        this.getMonsterList().add("creeper");

        this.getAnimalList().add("pig");

        this.blockBlacklist.add(49);

        this.getWorldBlacklist().add("world5");
        this.getWorldBlacklist().add("A world with spaces");

        this.config.setProperty("worlds." + this.name + ".animals.exceptions", this.getAnimalList());
        this.config.setProperty("worlds." + this.name + ".monsters.exceptions", this.getMonsterList());
        this.config.setProperty("worlds." + this.name + ".blockblacklist", this.getBlockBlacklist());
        this.config.setProperty("worlds." + this.name + ".worldblacklist", this.getWorldBlacklist());
        this.config.save();
    }

    public boolean clearVariable(String property) {
        if (property.equalsIgnoreCase("blockblacklist")) {
            this.blockBlacklist.clear();
        } else if (this.masterList.keySet().contains(property)) {
            this.masterList.get(property).clear();
        } else {
            return false;
        }
        this.config.setProperty("worlds." + this.name + "." + property.toLowerCase(), this.blockBlacklist);
        this.config.save();
        return true;
    }

    public boolean addToList(String list, String value) {
        if (list.equalsIgnoreCase("blockblacklist")) {
            try {
                int intVal = Integer.parseInt(value);
                return addToList(list, intVal);
            } catch (Exception e) {
            }
        } else if (this.masterList.keySet().contains(list)) {

            if (list.equalsIgnoreCase("animals") || list.equalsIgnoreCase("monsters")) {
                this.masterList.get(list).add(value.toUpperCase());
                this.config.setProperty("worlds." + this.name + "." + list.toLowerCase() + ".exceptions", this.masterList.get(list));
                this.syncMobs();
            } else {
                this.masterList.get(list).add(value);
                this.config.setProperty("worlds." + this.name + "." + list.toLowerCase(), this.masterList.get(list));
            }
            this.config.save();
            return true;
        }
        return false;
    }

    public boolean removeFromList(String list, String value) {
        if (list.equalsIgnoreCase("blockblacklist")) {
            try {
                int intVal = Integer.parseInt(value);
                return removeFromList(list, intVal);
            } catch (Exception e) {
            }
        }
        if (this.masterList.keySet().contains(list)) {

            if (list.equalsIgnoreCase("animals") || list.equalsIgnoreCase("monsters")) {
                this.masterList.get(list).remove(value.toUpperCase());
                this.config.setProperty("worlds." + this.name + "." + list.toLowerCase() + ".exceptions", this.masterList.get(list));
                this.syncMobs();
            } else {
                this.masterList.get(list).remove(value);
                this.config.setProperty("worlds." + this.name + "." + list.toLowerCase(), this.masterList.get(list));
            }
            this.config.save();
            return true;
        }
        return false;
    }

    private void syncMobs() {

        if (this.getAnimalList().isEmpty()) {
            this.world.setSpawnFlags(this.world.getAllowMonsters(), this.allowAnimals);
        } else {
            this.world.setSpawnFlags(this.world.getAllowMonsters(), true);
        }
        if (this.getMonsterList().isEmpty()) {
            this.world.setSpawnFlags(this.allowMonsters, this.world.getAllowAnimals());
        } else {
            this.world.setSpawnFlags(true, this.world.getAllowAnimals());
        }
        this.plugin.getWorldPurger().purgeWorld(null, this);
    }

    private boolean addToList(String list, Integer value) {
        if (list.equalsIgnoreCase("blockblacklist")) {
            this.blockBlacklist.add(value);
            this.config.setProperty("worlds." + this.name + ".blockblacklist", this.blockBlacklist);
            this.config.save();
        }
        return false;

    }

    private boolean removeFromList(String list, Integer value) {
        if (list.equalsIgnoreCase("blockblacklist")) {
            this.blockBlacklist.remove(value);
            this.config.setProperty("worlds." + this.name + ".blockblacklist", this.blockBlacklist);
            this.config.save();
        }
        return false;

    }

    private boolean setVariable(String name, boolean value) {
        if (name.equalsIgnoreCase("pvp")) {
            this.setPvp(value);
        } else if (name.equalsIgnoreCase("animals")) {

            this.setAnimals(value);
        } else if (name.equalsIgnoreCase("monsters")) {
            this.setMonsters(value);
        } else {
            return false;
        }
        return true;
    }

    private boolean setVariable(String name, double value) {
        if (name.equalsIgnoreCase("scaling")) {
            this.setScaling(value);
            return true;
        }

        return false;
    }

    /**
     * This is the one people have access to. It'll handle the rest.
     *
     * @param name
     * @param value
     * @return
     */
    public boolean setVariable(String name, String value) {
        if (name.equalsIgnoreCase("alias")) {
            this.setAlias(value);
            return true;
        }
        if (name.equalsIgnoreCase("respawn")) {
            this.setRespawnToWorld(value);
            return true;
        }
        if (name.equalsIgnoreCase("aliascolor") || name.equalsIgnoreCase("color")) {
            this.setAliasColor(value);
            return true;
        }
        try {
            boolean boolValue = Boolean.parseBoolean(value);
            return this.setVariable(name, boolValue);
        } catch (Exception e) {
        }

        try {
            double doubValue = Double.parseDouble(value);
            return this.setVariable(name, doubValue);
        } catch (Exception e) {
        }

        return false;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Long getSeed() {
        return this.seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public String getName() {
        return this.name;
    }

    public String getAlias() {
        if (this.alias == null || this.alias.length() == 0) {
            return this.name;
        }
        return this.alias;

    }

    public void setAlias(String alias) {
        this.alias = alias;
        this.config.setProperty("worlds." + this.name + ".alias.name", alias);
        this.config.save();
    }

    public Boolean allowAnimalSpawning() {
        return this.allowAnimals;
    }

    private void setAnimals(Boolean animals) {
        this.allowAnimals = animals;
        // If animals are a boolean, then we can turn them on or off on the server
        // If there are ANY exceptions, there will be something spawning, so turn them on
        this.config.setProperty("worlds." + this.name + ".animals.spawn", animals);
        this.config.save();
        this.syncMobs();
    }

    public List<String> getAnimalList() {
        return this.masterList.get("animals");
    }

    public Boolean allowMonsterSpawning() {
        return this.allowMonsters;
    }

    private void setMonsters(Boolean monsters) {
        this.allowMonsters = monsters;
        // If monsters are a boolean, then we can turn them on or off on the server
        // If there are ANY exceptions, there will be something spawning, so turn them on
        this.config.setProperty("worlds." + this.name + ".monsters.spawn", monsters);
        this.config.save();
        this.syncMobs();
    }

    public List<String> getMonsterList() {
        return this.masterList.get("monsters");
    }

    public Boolean getPvp() {
        return this.pvp;
    }

    public void setPvp(Boolean pvp) {
        this.fakepvp = this.plugin.getConfig().getBoolean("fakepvp", false);
        if (this.fakepvp) {
            this.world.setPVP(true);
        } else {
            this.world.setPVP(pvp);
        }
        this.pvp = pvp;
        this.config.setProperty("worlds." + this.name + ".pvp", pvp);
        this.config.save();

    }

    public List<Integer> getBlockBlacklist() {
        return this.blockBlacklist;
    }

    public List<String> getWorldBlacklist() {
        return this.masterList.get("worldblacklist");
    }

    public Double getScaling() {
        return this.scaling;
    }

    public void setScaling(Double scaling) {
        if (scaling <= 0) {
            // Disallow negative or 0 scalings.
            scaling = 1.0;
        }
        this.scaling = scaling;
        this.config.setProperty("worlds." + this.name + ".scaling", scaling);
        this.config.save();
    }

    /**
     * Sets the chat color from a string.
     *
     * @param aliasColor
     */
    public void setAliasColor(String aliasColor) {
        EnglishChatColor color = EnglishChatColor.fromString(aliasColor);
        if (color == null) {
            color = EnglishChatColor.WHITE;
        }
        this.aliasColor = color.getColor();
        this.config.setProperty("worlds." + this.name + ".alias.color", color.getText());
        this.config.save();
        return;
    }

    public boolean isValidAliasColor(String aliasColor) {
        return (EnglishChatColor.fromString(aliasColor) != null);
    }

    public ChatColor getAliasColor() {
        return this.aliasColor;
    }

    public boolean clearList(String property) {
        if (property.equalsIgnoreCase("blockblacklist")) {
            this.blockBlacklist.clear();
            this.config.setProperty("worlds." + this.name + ".blockblacklist", this.blockBlacklist);
            this.config.save();
            return true;
        } else if (this.masterList.containsKey(property)) {
            this.masterList.get(property).clear();
            this.config.setProperty("worlds." + this.name + "." + property.toLowerCase(), this.masterList.get(property));
            this.config.save();
            return true;
        }
        return false;
    }

    public boolean getFakePVP() {
        return this.fakepvp;
    }

    public String getRespawnToWorld() {
        return this.respawnWorld;
    }

    public void setRespawnToWorld(String respawnToWorld) {
        this.respawnWorld = respawnToWorld;
        this.config.setProperty("worlds."+this.name+".respawnworld", respawnToWorld);
    }
}
