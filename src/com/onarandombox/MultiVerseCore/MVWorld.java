package com.onarandombox.MultiVerseCore;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.util.config.Configuration;

import com.onarandombox.utils.stringLocation;

@SuppressWarnings("unused")
public class MVWorld {

    private MultiVerseCore plugin; // Hold the Plugin Instance.
    private Configuration config; // Hold the Configuration File.
    
    public World world; // The World Instance.
    public Environment environment; // Hold the Environment type EG Environment.NETHER / Environment.NORMAL
    public Location spawn; // Location of the Spawn Point.
    
    public String name; // The Worlds Name, EG its folder name.
    public String alias = ""; // Short Alias for the World, this will be used in Chat Prefixes.
    
    public Boolean animals; // Does this World allow Animals to Spawn?
    public Boolean monsters; // Does this World allow Monsters to Spawn?
    public Boolean pvp; // Does this World allow PVP?
    
    public List<String> blockBlacklist; // Contain a list of Blocks which we won't allow on this World.
    public List<String> joinWhitelist; // Contain a list of Players/Groups which can join this World.
    public List<String> joinBlacklist; // Contain a list of Players/Groups which cannot join this World.
    public List<String> editWhitelist; // Contain a list of Players/Groups which can edit this World. (Place/Destroy Blocks)
    public List<String> editBlacklist; // Contain a list of Players/Groups which cannot edit this World. (Place/Destroy Blocks)
    public List<String> worldBlacklist; // Contain a list of Worlds which Players cannot use to Portal to this World.
    
    public MVWorld(World world, Configuration config, MultiVerseCore instance){
        this.config = config;
        this.plugin = instance;
        
        this.world = world;
        this.name = world.getName();
        
        this.alias = config.getString("worlds." + this.name + ".alias","");
        
        this.environment = world.getEnvironment();
        
        this.spawn = getSpawn(this.config.getString("worlds." + name + ".spawn", "").split(":"));
        
        this.monsters = config.getBoolean("worlds." + this.name + ".monsters", true);
        this.animals = config.getBoolean("worlds." + this.name + ".animals", true);
        this.pvp = config.getBoolean("worlds." + this.name + ".pvp", true);
    }
    
    private Location getSpawn(String[] spawn){
        Location l = null;
        
        if(spawn.length!=5){
            return this.world.getSpawnLocation();
        } else {
            return new stringLocation().stringToLocation(world, spawn[0], spawn[1], spawn[2], spawn[3], spawn[4]);
        }
    }
    
}