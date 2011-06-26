package com.onarandombox.MultiverseCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.util.config.Configuration;

public class MVWorld {
    
    private MultiverseCore plugin; // Hold the Plugin Instance.
    private Configuration config; // Hold the Configuration File.
    
    private World world; // The World Instance.
    private Environment environment; // Hold the Environment type EG Environment.NETHER / Environment.NORMAL
    private Long seed;
    
    private String name; // The Worlds Name, EG its folder name.
    private String alias = ""; // Short Alias for the World, this will be used in Chat Prefixes.
    
    private boolean allowAnimals; // Does this World allow Animals to Spawn?
    //public List<String> animals = new ArrayList<String>(); // Contain a list of Animals which we want to ignore the Spawn Setting.
    
    private boolean allowMonsters; // Does this World allow Monsters to Spawn?
    //public List<String> monsters = new ArrayList<String>(); // Contain a list of Monsters which we want to ignore the Spawn Setting.
    
    private Boolean pvp; // Does this World allow PVP?
    
    private List<Integer> blockBlacklist; // Contain a list of Blocks which we won't allow on this World.
    
    // These have been moved to a hash, for easy editing with strings.
//    private List<String> playerWhitelist; // Contain a list of Players/Groups which can join this World.
//    private List<String> playerBlacklist; // Contain a list of Players/Groups which cannot join this World.
//    private List<String> editWhitelist; // Contain a list of Players/Groups which can edit this World. (Place/Destroy Blocks)
//    private List<String> editBlacklist; // Contain a list of Players/Groups which cannot edit this World. (Place/Destroy Blocks)
//    private List<String> worldBlacklist; // Contain a list of Worlds which Players cannot use to Portal to this World.
    
    private HashMap<String, List<String>> masterList;
    
    private Double scaling; // How stretched/compressed distances are
    /**
     * The generator as a string. This is used only for reporting. ex: BukkitFullOfMoon:GenID
     */
    private String generator;
    
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
        this.setAlias(config.getString("worlds." + this.name + ".alias", ""));
        this.setPvp(config.getBoolean("worlds." + this.name + ".pvp", true));
        this.setScaling(config.getDouble("worlds." + this.name + ".scale", 1.0));
        
        this.setAnimals(config.getBoolean("worlds." + this.name + ".animals.spawn", true));
        this.setMonsters(config.getBoolean("worlds." + this.name + ".monsters.spawn", true));
        this.getMobExceptions();
        
        this.getPlayerWhitelist().addAll(config.getStringList("worlds." + this.name + ".playerwhitelist", new ArrayList<String>()));
        this.getPlayerBlacklist().addAll(config.getStringList("worlds." + this.name + ".playerblacklist", new ArrayList<String>()));
        this.getWorldBlacklist().addAll(config.getStringList("worlds." + this.name + ".worldblacklist", new ArrayList<String>()));
        this.getBlockBlacklist().addAll(config.getIntList("worlds." + this.name + ".blockblacklist", new ArrayList<Integer>()));
        this.getEditWhitelist().addAll(config.getStringList("worlds." + this.name + ".editwhitelist", new ArrayList<String>()));
        this.getEditBlacklist().addAll(config.getStringList("worlds." + this.name + ".editblacklist", new ArrayList<String>()));
        
        config.save();
        // The following 3 lines will add some sample data to new worlds created.
        // if (config.getIntList("worlds." + name + ".blockBlacklist", new ArrayList<Integer>()).size() == 0) {
        // addSampleData();
        // }
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
        this.masterList.put("playerwhitelist", new ArrayList<String>());
        this.masterList.put("playerblacklist", new ArrayList<String>());
        this.masterList.put("editwhitelist", new ArrayList<String>());
        this.masterList.put("editblacklist", new ArrayList<String>());
        this.masterList.put("worldblacklist", new ArrayList<String>());
        this.masterList.put("animals", new ArrayList<String>());
        this.masterList.put("monsters", new ArrayList<String>());
    }
    
    public void addSampleData() {
        this.getMonsterList().add("creeper");
        
        this.getAnimalList().add("pig");
        
        this.blockBlacklist.add(49);
        
        this.getPlayerWhitelist().add("fernferret");
        this.getPlayerBlacklist().add("g:Admins");
        
        this.getPlayerBlacklist().add("Rigby90");
        this.getPlayerBlacklist().add("g:Banned");
        
        this.getEditWhitelist().add("fernferret");
        this.getEditWhitelist().add("g:Admins");
        
        this.getEditBlacklist().add("Rigby90");
        this.getEditBlacklist().add("g:Banned");
        
        this.getWorldBlacklist().add("world5");
        this.getWorldBlacklist().add("A world with spaces");
        
        this.config.setProperty("worlds." + this.name + ".animals.exceptions", this.getAnimalList());
        this.config.setProperty("worlds." + this.name + ".monsters.exceptions", this.getMonsterList());
        this.config.setProperty("worlds." + this.name + ".blockblacklist", this.getBlockBlacklist());
        this.config.setProperty("worlds." + this.name + ".playerwhitelist", this.getPlayerWhitelist());
        this.config.setProperty("worlds." + this.name + ".playerblacklist", this.getPlayerBlacklist());
        this.config.setProperty("worlds." + this.name + ".editwhitelist", this.getEditWhitelist());
        this.config.setProperty("worlds." + this.name + ".editblacklist", this.getEditBlacklist());
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
            this.masterList.get(list).add(value);
            if (list.equalsIgnoreCase("animals") || list.equalsIgnoreCase("monsters")) {
                this.config.setProperty("worlds." + this.name + "." + list.toLowerCase() + ".exceptions", this.masterList.get(list));
                this.syncMobs();
            } else {
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
            this.masterList.get(list).remove(value);
            if (list.equalsIgnoreCase("animals") || list.equalsIgnoreCase("monsters")) {
                this.config.setProperty("worlds." + this.name + "." + list.toLowerCase() + ".exceptions", this.masterList.get(list));
                this.syncMobs();
            } else {
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
            if (!this.allowAnimals) {
                // TODO: Purge
            }
        } else {
            this.world.setSpawnFlags(this.world.getAllowMonsters(), true);
        }
        
        if (this.getMonsterList().isEmpty()) {
            this.world.setSpawnFlags(this.allowMonsters, this.world.getAllowAnimals());
            if (!this.allowMonsters) {
                // TODO: Purge
            }
        } else {
            this.world.setSpawnFlags(true, this.world.getAllowAnimals());
        }
    }
    
    private boolean addToList(String list, Integer value) {
        if (list.equalsIgnoreCase("blockblacklist")) {
            this.blockBlacklist.add(value);
            this.config.setProperty("worlds." + this.name + ".blockblacklist", this.blockBlacklist);
        }
        return false;
        
    }
    
    private boolean removeFromList(String list, Integer value) {
        if (list.equalsIgnoreCase("blockblacklist")) {
            this.blockBlacklist.remove(value);
            this.config.setProperty("worlds." + this.name + ".blockblacklist", this.blockBlacklist);
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
            this.alias = value;
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
        return this.alias;
        
    }
    
    public void setAlias(String alias) {
        this.alias = alias;
        this.config.setProperty("worlds." + this.name + ".alias", alias);
        this.config.save();
    }
    
    public Boolean hasAnimals() {
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
    
    public Boolean hasMonsters() {
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
        this.world.setPVP(pvp);
        this.pvp = pvp;
        this.config.setProperty("worlds." + this.name + ".pvp", pvp);
        this.config.save();
        
    }
    
    public List<Integer> getBlockBlacklist() {
        return this.blockBlacklist;
    }
    
    public List<String> getPlayerWhitelist() {
        return this.masterList.get("playerwhitelist");
    }
    
    public List<String> getPlayerBlacklist() {
        return this.masterList.get("playerblacklist");
    }
    
    public List<String> getEditWhitelist() {
        return this.masterList.get("editwhitelist");
    }
    
    public List<String> getEditBlacklist() {
        return this.masterList.get("editblacklist");
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
}
