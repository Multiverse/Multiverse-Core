package com.onarandombox.MultiVerseCore;

import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.util.config.Configuration;

import com.onarandombox.utils.stringLocation;

@SuppressWarnings("unused")
public class MVWorld {
    
    public World world; // The World Instance.
    public Environment environment; // Hold the Environment type EG Environment.NETHER / Environment.NORMAL
    
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
    
    public int compression = 0; // TODO: Needs a description, minds blank and can't think :). 
    
    /**
     * @param world
     * @param handle - If the World was loaded by MultiVerse then this will be true and means we can do 
     * what we wan't with it else it's only here to be read from.
     */
    public MVWorld(World world, boolean handle){
        this.world = world;
        this.name = world.getName();
        
        this.environment = world.getEnvironment();
        
        // The following should already of been set when the World was created, so we don't wan't to overwrite these values we'll just grab them.
        this.monsters = ((CraftWorld) world).getHandle().D = monsters; // TODO: Swap to Bukkit Function when implemented.
        this.animals = ((CraftWorld) world).getHandle().E = animals; // TODO: Swap to Bukkit Function when implemented.
        
        // If MultiVerse created/loaded the World then it means we wan't to handle it as well, otherwise 
        // we don't touch any settings unless the user specifically asks us to.
        if(handle==true){
            this.alias = MultiVerseCore.configWorlds.getString("worlds." + this.name + ".alias","");
            this.compression = MultiVerseCore.configWorlds.getInt("worlds." + this.name + ".compression", 0);
            this.pvp = MultiVerseCore.configWorlds.getBoolean("worlds." + this.name + ".pvp", false);
        }
    }
}