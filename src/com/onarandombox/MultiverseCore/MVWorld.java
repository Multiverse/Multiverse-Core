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
    public Environment environment; // Hold the Environment type EG Environment.NETHER / Environment.NORMAL
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
        this.environment = world.getEnvironment();
        this.seed = seed;
        
        initLists();
        
        this.alias = config.getString("worlds." + this.name + ".alias", "");
        
        this.pvp = config.getBoolean("worlds." + this.name + ".pvp", true);
        
        this.scaling = config.getDouble("worlds." + this.name + ".scale", 1.0);
        if(this.scaling <= 0) {
            // Disallow negative or 0 scalings.
            config.setProperty("worlds." + this.name + ".scale", 1.0);
            this.scaling = 1.0;
        }
        
        this.playerWhitelist = config.getStringList("worlds." + name + ".playerWhitelist", playerWhitelist);
        this.playerBlacklist = config.getStringList("worlds." + name + ".playerBlacklist", playerBlacklist);
        this.worldBlacklist = config.getStringList("worlds." + name + ".worldBlacklist", worldBlacklist);
        this.blockBlacklist = config.getIntList("worlds." + name + ".blockBlacklist", blockBlacklist);
        this.editWhitelist = config.getStringList("worlds." + name + ".editWhitelist", editWhitelist);
        this.editBlacklist = config.getStringList("worlds." + name + ".editBlacklist", editBlacklist);
        
        this.animals = config.getBoolean("worlds." + name + ".animals.spawn", true);
        this.monsters = config.getBoolean("worlds." + name + ".monsters.spawn", true);
        
        List<String> temp;
        temp = config.getStringList("worlds." + name + ".animals.exceptions", animalList);
        this.animalList.clear();
        for (String s : temp) {
            this.animalList.add(s.toUpperCase());
            //System.out.print(s);
        }
        temp = config.getStringList("worlds." + name + ".monsters.exceptions", monsterList);
        for (String s : temp) {
            this.monsterList.add(s.toUpperCase());
            //System.out.print(s);
        }
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
    
    private void initLists() {
        blockBlacklist = new ArrayList<Integer>();
        playerWhitelist = new ArrayList<String>();
        playerBlacklist = new ArrayList<String>();
        editWhitelist = new ArrayList<String>();
        editBlacklist = new ArrayList<String>();
        worldBlacklist = new ArrayList<String>();
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
        
        this.config.setProperty("worlds." + name + ".animals.exceptions", animalList);
        this.config.setProperty("worlds." + name + ".monsters.exceptions", monsterList);
        this.config.setProperty("worlds." + name + ".blockBlacklist", blockBlacklist);
        this.config.setProperty("worlds." + name + ".playerWhitelist", playerWhitelist);
        this.config.setProperty("worlds." + name + ".playerBlacklist", playerBlacklist);
        this.config.setProperty("worlds." + name + ".editWhitelist", editWhitelist);
        this.config.setProperty("worlds." + name + ".editBlacklist", editBlacklist);
        this.config.setProperty("worlds." + name + ".worldBlacklist", worldBlacklist);
        this.config.save();
    }
}
