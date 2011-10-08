/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.utils.EnglishChatColor;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.config.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;


public class MVWorld implements MultiverseWorld {

    private MultiverseCore plugin; // Hold the Plugin Instance.
    private Configuration config; // Hold the Configuration File.

    private World world; // The World Instance.
    private Environment environment; // Hold the Environment type EG Environment.NETHER / Environment.NORMAL
    private Long seed;

    private String name; // The Worlds Name, EG its folder name.
    private String alias = ""; // Short Alias for the World, this will be used in Chat Prefixes.
    private ChatColor aliasColor; // Color for this world

    private boolean allowAnimals; // Does this World allow Animals to Spawn?
    private boolean allowMonsters; // Does this World allow Monsters to Spawn?

    private boolean keepSpawnInMemory; // Does the World have the spawn loaded all the time?

    private boolean pvp; // Does this World allow PVP?
    private boolean fakePVP; // Should this world have fakePVP on? (used for PVP zones)

    private GameMode gameMode = GameMode.SURVIVAL;

    private String respawnWorld; // Contains the name of the World to respawn the player to


    private HashMap<String, List<String>> masterList;

    private Double scaling; // How stretched/compressed distances are
    private Double price; // How much does it cost to enter this world
    private int currency = -1; // What is the currency
    private boolean hunger = true;
    private Permission permission;
    private Permission exempt;

    private boolean canSave = false; // Prevents all the setters from constantly saving to the config when being called from the constructor.
    private boolean allowWeather;
    private Location spawnLocation;

    public MVWorld(World world, Configuration config, MultiverseCore instance, Long seed, String generatorString) {
        this.config = config;
        this.plugin = instance;

        // Set local values that CANNOT be changed by user
        this.world = world;
        this.name = world.getName();
        this.seed = seed;
        this.environment = world.getEnvironment();

        // Initialize our lists
        this.initLists();

        // Write these files to the config (once it's saved)
        if (generatorString != null) {
            config.setProperty("worlds." + this.name + ".generator", generatorString);
        }
        if (seed != null) {
            config.setProperty("worlds." + this.name + ".seed", this.seed);
        }
        config.setProperty("worlds." + this.name + ".environment", this.environment.toString());

        // Set local values that CAN be changed by the user
        this.setAlias(config.getString("worlds." + this.name + ".alias.name", ""));
        this.setAliasColor(config.getString("worlds." + this.name + ".alias.color", ChatColor.WHITE.toString()));
        this.setFakePVPMode(config.getBoolean("worlds." + this.name + ".fakepvp", false));
        this.setPVPMode(config.getBoolean("worlds." + this.name + ".pvp", true));
        this.setScaling(config.getDouble("worlds." + this.name + ".scale", this.getDefaultScale(this.environment)));
        this.setRespawnToWorld(config.getString("worlds." + this.name + ".respawnworld", ""));
        this.setEnableWeather(config.getBoolean("worlds." + this.name + ".allowweather", true));
        this.setDifficulty(config.getString("worlds." + this.name + ".difficulty", "EASY"));

        this.setAnimals(config.getBoolean("worlds." + this.name + ".animals.spawn", true));
        this.setMonsters(config.getBoolean("worlds." + this.name + ".monsters.spawn", true));
        this.setPrice(config.getDouble("worlds." + this.name + ".entryfee.amount", 0.0));
        this.setCurrency(config.getInt("worlds." + this.name + ".entryfee.currency", -1));
        this.setHunger(config.getBoolean("worlds." + this.name + ".hunger", true));
        this.getMobExceptions();

        this.setGameMode(config.getString("worlds." + this.name + ".gamemode", GameMode.SURVIVAL.toString()));

        this.setSpawnInMemory(config.getBoolean("worlds." + this.name + ".keepspawninmemory", true));

        this.getWorldBlacklist().addAll(config.getStringList("worlds." + this.name + ".worldblacklist", new ArrayList<String>()));
        this.translateTempSpawn(config);
        this.readSpawnFromConfig(this.getCBWorld());
        this.canSave = true;
        saveConfig();

        this.permission = new Permission("multiverse.access." + this.getName(), "Allows access to " + this.getName(), PermissionDefault.OP);
        this.exempt = new Permission("multiverse.exempt." + this.getName(), "A player who has this does not pay to enter this world, or use any MV portals in it " + this.getName(), PermissionDefault.OP);
        try {
            this.plugin.getServer().getPluginManager().addPermission(this.permission);
            this.plugin.getServer().getPluginManager().addPermission(this.exempt);
            addToUpperLists(this.permission);
        } catch (IllegalArgumentException e) {
            this.plugin.log(Level.SEVERE, "Error when adding Exemption/Access permissions for " + this.name);
        }
    }

    private double getDefaultScale(Environment environment) {
        if (environment == Environment.NETHER) {
            return 8.0;
        }
        return 1.0;
    }

    private void setEnableWeather(boolean weather) {
        this.allowWeather = weather;
        // Disable any current weather
        if (!weather) {
            this.getCBWorld().setStorm(false);
            this.getCBWorld().setThundering(false);
        }
        this.config.setProperty("worlds." + this.name + ".allowweather", weather);
        saveConfig();
    }

    private void addToUpperLists(Permission permission) {
        Permission all = this.plugin.getServer().getPluginManager().getPermission("multiverse.*");
        Permission allWorlds = this.plugin.getServer().getPluginManager().getPermission("multiverse.access.*");
        Permission allExemption = this.plugin.getServer().getPluginManager().getPermission("multiverse.exempt.*");

        if (allWorlds == null) {
            allWorlds = new Permission("multiverse.access.*");
            this.plugin.getServer().getPluginManager().addPermission(allWorlds);
        }
        allWorlds.getChildren().put(permission.getName(), true);
        if (allExemption == null) {
            allExemption = new Permission("multiverse.exempt.*");
            this.plugin.getServer().getPluginManager().addPermission(allExemption);
        }
        allExemption.getChildren().put(this.exempt.getName(), true);
        if (all == null) {
            all = new Permission("multiverse.*");
            this.plugin.getServer().getPluginManager().addPermission(all);
        }
        all.getChildren().put("multiverse.access.*", true);
        all.getChildren().put("multiverse.exempt.*", true);

        this.plugin.getServer().getPluginManager().recalculatePermissionDefaults(all);
        this.plugin.getServer().getPluginManager().recalculatePermissionDefaults(allWorlds);
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
                    this.setSpawn(new Location(this.getCBWorld(), coords[0], coords[1], coords[2]));
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
        this.masterList.put("worldblacklist", new ArrayList<String>());
        this.masterList.put("animals", new ArrayList<String>());
        this.masterList.put("monsters", new ArrayList<String>());
    }

    @Override
    public boolean clearVariable(String property) {
        if (this.masterList.keySet().contains(property)) {
            this.masterList.get(property).clear();
        } else {
            return false;
        }
        this.config.setProperty("worlds." + this.name + "." + property.toLowerCase(), new ArrayList<String>());
        this.saveConfig();
        return true;
    }

    @Override
    public boolean addToVariable(String property, String value) {
        if (this.masterList.keySet().contains(property)) {

            if (property.equalsIgnoreCase("animals") || property.equalsIgnoreCase("monsters")) {
                this.masterList.get(property).add(value.toUpperCase());
                this.config.setProperty("worlds." + this.name + "." + property.toLowerCase() + ".exceptions", this.masterList.get(property));
                this.syncMobs();
            } else {
                this.masterList.get(property).add(value);
                this.config.setProperty("worlds." + this.name + "." + property.toLowerCase(), this.masterList.get(property));
            }
            saveConfig();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeFromVariable(String property, String value) {
        if (this.masterList.keySet().contains(property)) {

            if (property.equalsIgnoreCase("animals") || property.equalsIgnoreCase("monsters")) {
                this.masterList.get(property).remove(value.toUpperCase());
                this.config.setProperty("worlds." + this.name + "." + property.toLowerCase() + ".exceptions", this.masterList.get(property));
                this.syncMobs();
            } else {
                this.masterList.get(property).remove(value);
                this.config.setProperty("worlds." + this.name + "." + property.toLowerCase(), this.masterList.get(property));
            }
            saveConfig();
            return true;
        }
        return false;
    }

    /** Deprecated, use {@link #addToVariable(String, String)} now. */
    @Deprecated
    public boolean addToList(String list, String value) {
        return this.addToVariable(list, value);
    }

    // Deprecated, use {@link #removeFromVariable(String, String)} now.
    @Deprecated
    public boolean removeFromList(String list, String value) {
        return this.removeFromVariable(list, value);
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
        this.plugin.getWorldManager().getWorldPurger().purgeWorld(null, this);
    }

    private boolean setVariable(String name, boolean value) {
        if (name.equalsIgnoreCase("pvp")) {
            this.setPVPMode(value);
        } else if (name.equalsIgnoreCase("animals")) {
            this.setAnimals(value);
        } else if (name.equalsIgnoreCase("monsters")) {
            this.setMonsters(value);
        } else if (name.equalsIgnoreCase("memory") || name.equalsIgnoreCase("spawnmemory")) {
            this.setSpawnInMemory(value);
        } else if ((name.equalsIgnoreCase("hunger")) || (name.equalsIgnoreCase("food"))) {
            this.setHunger(value);
        } else if (name.equalsIgnoreCase("weather") || name.equalsIgnoreCase("storm")) {
            this.setEnableWeather(value);
        } else {
            return false;
        }
        return true;
    }

    private void setSpawnInMemory(boolean value) {
        this.world.setKeepSpawnInMemory(value);
        this.keepSpawnInMemory = value;
        this.config.setProperty("worlds." + this.name + ".keepspawninmemory", value);
        saveConfig();
    }

    @Override
    public boolean setVariable(String name, String value) {
        if (name.equalsIgnoreCase("diff") || name.equalsIgnoreCase("difficulty")) {
            return this.setDifficulty(value);
        }
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
        if (name.equalsIgnoreCase("currency") || name.equalsIgnoreCase("curr")) {
            try {
                int intValue = Integer.parseInt(value);
                this.setCurrency(intValue);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        if (name.equalsIgnoreCase("price")) {
            try {
                double doubValue = Double.parseDouble(value);
                return this.setPrice(doubValue);
            } catch (Exception e) {
                return false;
            }
        }
        if (name.equalsIgnoreCase("scale") || name.equalsIgnoreCase("scaling")) {
            try {
                double doubValue = Double.parseDouble(value);
                return this.setScaling(doubValue);
            } catch (Exception e) {
                return false;
            }
        }

        if (name.equalsIgnoreCase("gamemode") || name.equalsIgnoreCase("mode")) {
            try {
                GameMode mode = GameMode.valueOf(value.toUpperCase());
                return this.setGameMode(mode);
            } catch (Exception e) {
                return false;
            }
        }

        try {
            boolean boolValue = Boolean.parseBoolean(value);
            return this.setVariable(name, boolValue);
        } catch (Exception e) {
            return false;
        }
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
        saveConfig();
    }

    public boolean allowAnimalSpawning() {
        return this.allowAnimals;
    }

    private void setAnimals(Boolean animals) {
        this.allowAnimals = animals;
        // If animals are a boolean, then we can turn them on or off on the server
        // If there are ANY exceptions, there will be something spawning, so turn them on
        this.config.setProperty("worlds." + this.name + ".animals.spawn", animals);
        saveConfig();
        this.syncMobs();
    }

    public List<String> getAnimalList() {
        return this.masterList.get("animals");
    }

    public boolean allowMonsterSpawning() {
        return this.allowMonsters;
    }

    private void setMonsters(Boolean monsters) {
        this.allowMonsters = monsters;
        // If monsters are a boolean, then we can turn them on or off on the server
        // If there are ANY exceptions, there will be something spawning, so turn them on
        this.config.setProperty("worlds." + this.name + ".monsters.spawn", monsters);
        saveConfig();
        this.syncMobs();
    }

    public List<String> getMonsterList() {
        return this.masterList.get("monsters");
    }

    public Boolean getPvp() {
        return this.pvp;
    }

    @Override
    public void setPVPMode(boolean pvp) {
        if (this.fakePVP) {
            this.world.setPVP(true);
        } else {
            this.world.setPVP(pvp);
        }
        this.pvp = pvp;
        this.config.setProperty("worlds." + this.name + ".pvp", pvp);
        saveConfig();
    }

    public void setFakePVPMode(Boolean fakePVPMode) {
        this.fakePVP = fakePVPMode;
        this.config.setProperty("worlds." + this.name + ".fakepvp", this.fakePVP);
        // Now that we've set PVP mode, make sure to go through the normal setting too!
        // This method will perform the save for us to eliminate one write.
        this.setPVPMode(this.pvp);
    }

    public List<String> getWorldBlacklist() {
        return this.masterList.get("worldblacklist");
    }

    public Double getScaling() {
        return this.scaling;
    }

    public boolean setScaling(Double scaling) {
        if (scaling <= 0) {
            // Disallow negative or 0 scalings.
            scaling = 1.0;
        }
        this.scaling = scaling;
        this.config.setProperty("worlds." + this.name + ".scale", scaling);
        saveConfig();
        return true;
    }

    public void setAliasColor(String aliasColor) {
        EnglishChatColor color = EnglishChatColor.fromString(aliasColor);
        if (color == null) {
            color = EnglishChatColor.WHITE;
        }
        this.aliasColor = color.getColor();
        this.config.setProperty("worlds." + this.name + ".alias.color", color.getText());
        saveConfig();
        return;
    }

    public boolean isValidAliasColor(String aliasColor) {
        return (EnglishChatColor.fromString(aliasColor) != null);
    }

    public ChatColor getAliasColor() {
        return this.aliasColor;
    }

    public boolean clearList(String property) {
        if (this.masterList.containsKey(property)) {
            this.masterList.get(property).clear();
            this.config.setProperty("worlds." + this.name + "." + property.toLowerCase(), this.masterList.get(property));
            this.syncMobs();
            saveConfig();
            return true;
        }
        return false;
    }

    public boolean getFakePVP() {
        return this.fakePVP;
    }

    public String getRespawnToWorld() {
        return this.respawnWorld;
    }

    public void setRespawnToWorld(String respawnToWorld) {
        this.respawnWorld = respawnToWorld;
        this.config.setProperty("worlds." + this.name + ".respawnworld", respawnToWorld);
        saveConfig();
    }

    public Permission getPermission() {
        return this.permission;
    }

    public int getCurrency() {
        return this.currency;
    }

    public double getPrice() {
        return this.price;
    }

    private boolean setCurrency(int currency) {
        this.currency = currency;
        config.setProperty("worlds." + this.name + ".entryfee.currency", currency);
        saveConfig();
        return true;
    }

    private boolean setPrice(double price) {
        this.price = price;
        config.setProperty("worlds." + this.name + ".entryfee.amount", price);
        saveConfig();
        return true;
    }

    public boolean isExempt(Player p) {
        return (this.plugin.getPermissions().hasPermission(p, this.exempt.getName(), true));
    }

    public Permission getExempt() {
        return this.exempt;
    }

    private void saveConfig() {
        if (this.canSave) {
            this.config.save();
        }
    }

    private boolean setGameMode(String strMode) {
        GameMode mode = GameMode.SURVIVAL;
        try {
            mode = GameMode.valueOf(strMode);
        } catch (Exception e) {
        }
        return this.setGameMode(mode);
    }

    private boolean setGameMode(GameMode mode) {

        this.gameMode = mode;
        config.setProperty("worlds." + this.name + ".gamemode", this.gameMode.toString());
        saveConfig();

        if (this.plugin.getConfig().getBoolean("enforcegamemodes", true)) {
            for (Player p : this.plugin.getServer().getWorld(this.getName()).getPlayers()) {
                this.plugin.log(Level.FINER, "Setting " + p.getName() + "'s GameMode to " + this.gameMode.toString());
                this.plugin.getPlayerListener().handleGameMode(p, this);
            }
        }
        return true;
    }

    public GameMode getGameMode() {
        return this.gameMode;
    }

    public boolean getWeatherEnabled() {
        return this.allowWeather;
    }

    public boolean getKeepSpawnInMemory() {
        return this.keepSpawnInMemory;
    }

    private boolean setHunger(boolean hunger) {
        this.hunger = hunger;
        config.setProperty("worlds." + this.name + ".hunger", this.hunger);
        saveConfig();
        return true;
    }

    public boolean getHunger() {
        return this.hunger;
    }

    public boolean setSpawn(Location l) {
        this.getCBWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        config.setProperty("worlds." + this.name + ".spawn.x", l.getX());
        config.setProperty("worlds." + this.name + ".spawn.y", l.getY());
        config.setProperty("worlds." + this.name + ".spawn.z", l.getZ());
        config.setProperty("worlds." + this.name + ".spawn.pitch", l.getPitch());
        config.setProperty("worlds." + this.name + ".spawn.yaw", l.getYaw());
        this.getCBWorld().setSpawnLocation(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        this.spawnLocation = l.clone();
        saveConfig();
        return true;

    }

    private void readSpawnFromConfig(World w) {
        double x = config.getDouble("worlds." + this.name + ".spawn.x", w.getSpawnLocation().getX());
        double y = config.getDouble("worlds." + this.name + ".spawn.y", w.getSpawnLocation().getY());
        double z = config.getDouble("worlds." + this.name + ".spawn.z", w.getSpawnLocation().getZ());
        float pitch = (float) config.getDouble("worlds." + this.name + ".spawn.pitch", w.getSpawnLocation().getPitch());
        float yaw = (float) config.getDouble("worlds." + this.name + ".spawn.yaw", w.getSpawnLocation().getYaw());
        this.spawnLocation = new Location(w, x, y, z, yaw, pitch);
    }

    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    public Difficulty getDifficulty() {
        return this.getCBWorld().getDifficulty();
    }

    public boolean setDifficulty(String difficulty) {
        Difficulty worlddiff = null;
        try {
            worlddiff = Difficulty.valueOf(difficulty.toUpperCase());
        } catch (Exception e) {
            try {
                int diff = Integer.parseInt(difficulty);
                if (diff >= 0 && diff <= 3) {
                    worlddiff = Difficulty.getByValue(diff);
                } else {
                    return false;
                }
            } catch (Exception e2) {
                return false;
            }
        }
        this.getCBWorld().setDifficulty(worlddiff);
        config.setProperty("worlds." + this.name + ".difficulty", worlddiff.getValue());
        saveConfig();
        return true;
    }
}
