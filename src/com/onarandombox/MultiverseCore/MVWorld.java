package com.onarandombox.MultiverseCore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.config.Configuration;

@SuppressWarnings("unused")
public class MVWorld {
    
    private MultiverseCore plugin; // Hold the Plugin Instance.
    private Configuration config; // Hold the Configuration File.
    
    public World world; // The World Instance.
    public String environment; // Hold the Environment type EG Environment.NETHER / Environment.NORMAL
    public Long seed;
    
    public String name; // The Worlds Name, EG its folder name.
    public String alias = ""; // Short Alias for the World, this will be used in Chat Prefixes.
    
    public Boolean animals; // Does this World allow Animals to Spawn?
    public List<String> animalList = new ArrayList<String>(); // Contain a list of Animals which we want to ignore the Spawn Setting.
    
    public Boolean monsters; // Does this World allow Monsters to Spawn?
    public List<String> monsterList = new ArrayList<String>(); // Contain a list of Monsters which we want to ignore the Spawn Setting.
    
    public Boolean pvp; // Does this World allow PVP?
    
    public List<Integer> blockBlacklist; // Contain a list of Blocks which we won't allow on this World.
    public List<String> playerWhitelist; // Contain a list of Players/Groups which can join this World.
    public List<String> playerBlacklist; // Contain a list of Players/Groups which cannot join this World.
    public List<String> editWhitelist; // Contain a list of Players/Groups which can edit this World. (Place/Destroy Blocks)
    public List<String> editBlacklist; // Contain a list of Players/Groups which cannot edit this World. (Place/Destroy Blocks)
    public List<String> worldBlacklist; // Contain a list of Worlds which Players cannot use to Portal to this World.
    
    public Double scaling; // How stretched/compressed distances are
    
    public MVWorld(World world, Configuration config, MultiverseCore instance, Long seed, String env) {
        this.config = config;
        this.plugin = instance;
        
        this.world = world;
        this.name = world.getName();
        this.environment = env;
        this.seed = seed;
        
        this.initLists();
        
        this.alias = config.getString("worlds." + this.name + ".alias", "");
        
        this.pvp = config.getBoolean("worlds." + this.name + ".pvp", true);
        
        this.scaling = config.getDouble("worlds." + this.name + ".scale", 1.0);
        if(this.scaling <= 0) {
            // Disallow negative or 0 scalings.
            config.setProperty("worlds." + this.name + ".scale", 1.0);
            this.scaling = 1.0;
        }
        
        this.playerWhitelist = config.getStringList("worlds." + this.name + ".playerWhitelist", this.playerWhitelist);
        this.playerBlacklist = config.getStringList("worlds." + this.name + ".playerBlacklist", this.playerBlacklist);
        this.worldBlacklist = config.getStringList("worlds." + this.name + ".worldBlacklist", this.worldBlacklist);
        this.blockBlacklist = config.getIntList("worlds." + this.name + ".blockBlacklist", this.blockBlacklist);
        this.editWhitelist = config.getStringList("worlds." + this.name + ".editWhitelist", this.editWhitelist);
        this.editBlacklist = config.getStringList("worlds." + this.name + ".editBlacklist", this.editBlacklist);
        
        this.animals = config.getBoolean("worlds." + this.name + ".animals.spawn", true);
        this.monsters = config.getBoolean("worlds." + this.name + ".monsters.spawn", true);
        
        
        
        this.getMobExceptions();
        
        this.setRealMobBehaviors();
        
        config.setProperty("worlds." + this.name + ".environment", env);
        if(seed != null) {
            config.setProperty("worlds." + this.name + ".seed", this.seed);
        }
        config.save();
        // The following 3 lines will add some sample data to new worlds created.
//        if (config.getIntList("worlds." + name + ".blockBlacklist", new ArrayList<Integer>()).size() == 0) {
//            addSampleData();
//        }
    }

    private void getMobExceptions() {
        List<String> temp;
        temp = this.config.getStringList("worlds." + this.name + ".animals.exceptions", this.animalList);
        // Add Animals to the exclusion list
        this.animalList.clear();
        for (String s : temp) {
            this.animalList.add(s.toUpperCase());
        }
        temp = this.config.getStringList("worlds." + this.name + ".monsters.exceptions", this.monsterList);
        // Add Monsters to the exclusion list
        for (String s : temp) {
            this.monsterList.add(s.toUpperCase());
        }
    }
    
    public World getCBWorld() {
        return this.world;
    }
    
    private void setRealMobBehaviors() {
        boolean animals = true;
        boolean monsters = true;
        if(!this.animals && this.animalList.isEmpty()) {
            animals = false;
        }
        if(!this.monsters && this.monsterList.isEmpty()) {
            monsters = false;
        }
        this.world.setSpawnFlags(monsters, animals);
        
    }

    private void initLists() {
        this.blockBlacklist = new ArrayList<Integer>();
        this.playerWhitelist = new ArrayList<String>();
        this.playerBlacklist = new ArrayList<String>();
        this.editWhitelist = new ArrayList<String>();
        this.editBlacklist = new ArrayList<String>();
        this.worldBlacklist = new ArrayList<String>();
    }
    
    public void addSampleData() {
        this.monsterList.add("creeper");
        
        this.animalList.add("pig");
        
        this.blockBlacklist.add(49);
        
        this.playerWhitelist.add("fernferret");
        this.playerWhitelist.add("g:Admins");
        
        this.playerBlacklist.add("Rigby90");
        this.playerBlacklist.add("g:Banned");
        
        this.editWhitelist.add("fernferret");
        this.editWhitelist.add("g:Admins");
        
        this.editBlacklist.add("Rigby90");
        this.editBlacklist.add("g:Banned");
        
        this.worldBlacklist.add("world5");
        this.worldBlacklist.add("A world with spaces");
        
        this.config.setProperty("worlds." + this.name + ".animals.exceptions", this.animalList);
        this.config.setProperty("worlds." + this.name + ".monsters.exceptions", this.monsterList);
        this.config.setProperty("worlds." + this.name + ".blockBlacklist", this.blockBlacklist);
        this.config.setProperty("worlds." + this.name + ".playerWhitelist", this.playerWhitelist);
        this.config.setProperty("worlds." + this.name + ".playerBlacklist", this.playerBlacklist);
        this.config.setProperty("worlds." + this.name + ".editWhitelist", this.editWhitelist);
        this.config.setProperty("worlds." + this.name + ".editBlacklist", this.editBlacklist);
        this.config.setProperty("worlds." + this.name + ".worldBlacklist", this.worldBlacklist);
        this.config.save();
    }
}
