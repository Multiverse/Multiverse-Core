/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.api;

import org.bukkit.World;

/**
 * The API for a Multiverse Handled World.
 * <p/>
 * Currently INCOMPLETE
 */
public interface MultiverseWorld {

    /**
     * Returns the Bukkit world object that this world describes.
     *
     * @return A {@link World}
     */
    public World getCBWorld();

    /**
     * Adds the property to the given value. The property must be a {@link com.onarandombox.MultiverseCore.enums.SetProperties}.
     *
     * @param property The name of a {@link com.onarandombox.MultiverseCore.enums.SetProperties} to set.
     * @param value    A value in string representation, it will be parsed to the correct type.
     *
     * @return True if the value was set, false if not.
     */
    public boolean setVariable(String property, String value);

    /**
     * Removes all values from the given property. The property must be a {@link com.onarandombox.MultiverseCore.enums.AddProperties}.
     *
     * @param property The name of a {@link com.onarandombox.MultiverseCore.enums.AddProperties} to clear.
     *
     * @return True if it was cleared, false if not.
     */
    public boolean clearVariable(String property);

    /**
     * Adds a value to the given property. The property must be a {@link com.onarandombox.MultiverseCore.enums.AddProperties}.
     *
     * @param property The name of a {@link com.onarandombox.MultiverseCore.enums.AddProperties} to add a value to.
     * @param value    A value in string representation, it will be parsed to the correct type.
     *
     * @return True if the value was added, false if not.
     */
    public boolean addToVariable(String property, String value);

    /**
     * Removes a value from the given property. The property must be a {@link com.onarandombox.MultiverseCore.enums.AddProperties}.
     *
     * @param property The name of a {@link com.onarandombox.MultiverseCore.enums.AddProperties} to remove a value
     *                 from.
     * @param value    A value in string representation, it will be parsed to the correct type.
     *
     * @return True if the value was removed, false if not.
     */
    public boolean removeFromVariable(String property, String value);

    /**
     * Gets the environment of this world.
     *
     * @return A {@link org.bukkit.World.Environment}.
     */
    public World.Environment getEnvironment();

    /**
     * Sets the environment of a world.
     * <p/>
     * Note: This will ONLY take effect once the world is unloaded/reloaded.
     *
     * @param environment A {@link org.bukkit.World.Environment}.
     */
    public void setEnvironment(World.Environment environment);

    /**
     * Gets the world seed of this world.
     *
     * @return The Long version of the seed.
     */
    public Long getSeed();

    /**
     * Sets the seed of this world.
     *
     * @param seed A Long that is the seed.
     */
    public void setSeed(Long seed);

    /**
     * Gets the name of this world. This cannot be changed.
     *
     * @return The name of the world as a String.
     */
    public String getName();

    /**
     * Gets the alias of this world.
     * <p/>
     * This alias allows users to have a world named "world" but show up in the list as "FernIsland"
     *
     * @return The alias of the world as a String.
     */
    public String getAlias();

    /**
     * Sets the alias of the world.
     *
     * @param alias A string that is the new alias.
     */
    public void setAlias(String alias);

    /**
     * Returns a very nicely colored string (using Alias and Color if they are set).
     *
     * @return A nicely colored string.
     */
    public String getColoredWorldString();

    /**
     * Gets whether or not animals are allowed to spawn in this world.
     *
     * @return True if ANY animal can, false if no animals can spawn.
     */
    public boolean allowAnimalSpawning();

    /**
     * Gets whether or not monsters are allowed to spawn in this world.
     *
     * @return True if ANY monster can, false if no monsters can spawn.
     */
    public boolean allowMonsterSpawning();

    /**
     * Turn pvp on or off. This setting is used to set the world's PVP mode, and thus relies on fakePVP
     *
     * @param pvpMode True to enable PVP damage, false to disable it.
     */
    public void setPVPMode(boolean pvpMode);
}
