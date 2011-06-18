package com.onarandombox.MultiverseCore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.World;
import org.bukkit.World.Environment;
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
    public List<String> joinWhitelist; // Contain a list of Players/Groups which can join this World.
    public List<String> joinBlacklist; // Contain a list of Players/Groups which cannot join this World.
    public List<String> editWhitelist; // Contain a list of Players/Groups which can edit this World. (Place/Destroy Blocks)
    public List<String> editBlacklist; // Contain a list of Players/Groups which cannot edit this World. (Place/Destroy Blocks)
    public List<String> worldBlacklist; // Contain a list of Worlds which Players cannot use to Portal to this World.
    
    public Double compression; // How stretched/compressed distances are
    
    public MVWorld(World world, Configuration config, MultiverseCore instance, Long seed) {
        this.config = config;
        this.plugin = instance;
        
        this.world = world;
        this.name = world.getName();
        this.environment = world.getEnvironment();
        this.seed = seed;
        
        initLists();
        
        this.alias = config.getString("worlds." + this.name + ".alias", "");
        
        this.pvp = config.getBoolean("worlds." + this.name + ".pvp", true);
        
        this.compression = config.getDouble("worlds." + this.name + ".compression", 1.0);
        
        this.joinWhitelist = config.getStringList("worlds." + name + ".playerWhitelist", joinWhitelist);
        this.joinBlacklist = config.getStringList("worlds." + name + ".playerBlacklist", joinBlacklist);
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
            System.out.print(s);
        }
        temp = config.getStringList("worlds." + name + ".monsters.exceptions", monsterList);
        for (String s : temp) {
            this.monsterList.add(s.toUpperCase());
            System.out.print(s);
        }
        config.setProperty("worlds." + this.name + ".environment", this.environment.toString());
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
        joinWhitelist = new ArrayList<String>();
        joinBlacklist = new ArrayList<String>();
        editWhitelist = new ArrayList<String>();
        editBlacklist = new ArrayList<String>();
        worldBlacklist = new ArrayList<String>();
    }
    
    public void addSampleData() {
        this.monsterList.add("creeper");
        
        this.animalList.add("pig");
        
        this.blockBlacklist.add(49);
        
        this.joinWhitelist.add("fernferret");
        this.joinWhitelist.add("g:Admins");
        
        this.joinBlacklist.add("Rigby90");
        this.joinBlacklist.add("g:Banned");
        
        this.editWhitelist.add("fernferret");
        this.editWhitelist.add("g:Admins");
        
        this.editBlacklist.add("Rigby90");
        this.editBlacklist.add("g:Banned");
        
        this.worldBlacklist.add("world5");
        this.worldBlacklist.add("A world with spaces");
        
        this.config.setProperty("worlds." + name + ".animals.exceptions", animalList);
        this.config.setProperty("worlds." + name + ".monsters.exceptions", monsterList);
        this.config.setProperty("worlds." + name + ".blockBlacklist", blockBlacklist);
        this.config.setProperty("worlds." + name + ".playerWhitelist", joinWhitelist);
        this.config.setProperty("worlds." + name + ".playerBlacklist", joinBlacklist);
        this.config.setProperty("worlds." + name + ".editWhitelist", editWhitelist);
        this.config.setProperty("worlds." + name + ".editBlacklist", editBlacklist);
        this.config.setProperty("worlds." + name + ".worldBlacklist", worldBlacklist);
        this.config.save();
    }
}
