package org.mvplugins.multiverse.core.world

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldType
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.world.options.CloneWorldOptions
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import org.mvplugins.multiverse.core.world.options.RegenWorldOptions
import org.mvplugins.multiverse.core.world.options.UnloadWorldOptions
import org.mvplugins.multiverse.core.world.reasons.CloneFailureReason
import org.mvplugins.multiverse.core.world.reasons.CreateFailureReason
import org.mvplugins.multiverse.core.world.reasons.LoadFailureReason
import java.io.File
import kotlin.test.*

class WorldManagerTest : TestWithMockBukkit() {

    private lateinit var worldManager: WorldManager
    private lateinit var world: LoadedMultiverseWorld
    private lateinit var world2: LoadedMultiverseWorld

    @BeforeTest
    fun setUp() {
        worldManager = serviceLocator.getActiveService(WorldManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldManager is not available as a service") }

        assertTrue(worldManager.createWorld(CreateWorldOptions.worldName("world")).isSuccess)
        world = worldManager.getLoadedWorld("world").get()
        assertNotNull(world)

        assertTrue(worldManager.createWorld(CreateWorldOptions.worldName("world2")).isSuccess)
        world2 = worldManager.getLoadedWorld("world2").get()
        assertNotNull(world2)
    }

    @Test
    fun `Create world with custom options`() {
        assertTrue(worldManager.createWorld(
            CreateWorldOptions.worldName("world_nether")
            .environment(World.Environment.NETHER)
            .generateStructures(false)
            .seed(1234L)
            .useSpawnAdjust(false)
            .worldType(WorldType.FLAT)
        ).isSuccess)

        val getWorld = worldManager.getLoadedWorld("world_nether")
        assertTrue(getWorld.isDefined)
        val world = getWorld.get()
        assertNotNull(world)
        assertEquals("world_nether", world.name)
        assertEquals(World.Environment.NETHER, world.environment)
        assertFalse(world.canGenerateStructures().get())
        assertEquals(1234L, world.seed)
        assertFalse(world.adjustSpawn)
        assertEquals(WorldType.FLAT, world.worldType.get())
        assertEquals("", world.generator)
        assertEquals(8.0, world.scale)
    }

    @Test
    fun `Create world failed - invalid worldname`() {
        assertEquals(
            CreateFailureReason.INVALID_WORLDNAME,
            worldManager.createWorld(CreateWorldOptions.worldName("*!@^&#*(")).failureReason
        )
    }

    @Test
    fun `Create world failed - world exists and loaded`() {
        assertEquals(
            CreateFailureReason.WORLD_EXIST_LOADED,
            worldManager.createWorld(CreateWorldOptions.worldName("world")).failureReason
        )
    }

    @Test
    fun `Create world failed - world exists but unloaded`() {
        assertTrue(worldManager.unloadWorld(UnloadWorldOptions.world(world)).isSuccess)
        assertEquals(
            CreateFailureReason.WORLD_EXIST_UNLOADED,
            worldManager.createWorld(CreateWorldOptions.worldName("world")).failureReason
        )
    }

    @Test
    fun `Create world failed - world folder exists`() {
        File(Bukkit.getWorldContainer(), "worldfolder").mkdir()
        assertEquals(
            CreateFailureReason.WORLD_EXIST_FOLDER,
            worldManager.createWorld(CreateWorldOptions.worldName("worldfolder")).failureReason
        )
    }

    @Test
    fun `Remove world`() {
        assertTrue(worldManager.removeWorld(world).isSuccess)
        assertFalse(worldManager.getWorld("world").isDefined)
        assertFalse(worldManager.getLoadedWorld("world").isDefined)
        assertFalse(worldManager.getUnloadedWorld("world").isDefined)
    }

    @Test
    fun `Delete world`() {
        assertTrue(worldManager.deleteWorld(world).isSuccess)
        assertFalse(worldManager.getLoadedWorld("world").isDefined)
    }

    @Test
    fun `Unload and load world`() {
        assertTrue(worldManager.unloadWorld(UnloadWorldOptions.world(world2).saveBukkitWorld(true)).isSuccess)
        assertFalse(world2.isLoaded)
        assertFalse(world2.bukkitWorld.isDefined)
        assertFalse(worldManager.getLoadedWorld("world2").isDefined)
        assertTrue(worldManager.getWorld("world2").isDefined)
        assertTrue(worldManager.getUnloadedWorld("world2").isDefined)

        assertTrue(worldManager.loadWorld("world2").isSuccess)
        assertTrue(world2.isLoaded)
        assertTrue(worldManager.getLoadedWorld("world2").flatMap{ w -> w.bukkitWorld }.isDefined)
        assertTrue(worldManager.getLoadedWorld("world2").isDefined)
        assertFalse(worldManager.getUnloadedWorld("world2").isDefined)
    }

    @Test
    fun `Load world failed - non-existent world`() {
        assertEquals(
            LoadFailureReason.WORLD_NON_EXISTENT,
            worldManager.loadWorld("ghost").failureReason
        )
    }

    @Test
    fun `Load world failed - world folder exists but not imported`() {
        File(Bukkit.getWorldContainer(), "worldfolder").mkdir()
        File(Bukkit.getWorldContainer(), "worldfolder/level.dat").createNewFile()
        assertEquals(
            LoadFailureReason.WORLD_EXIST_FOLDER,
            worldManager.loadWorld("worldfolder").failureReason
        )
    }

    @Test
    fun `Regen world`() {
        assertTrue(worldManager.regenWorld(
            RegenWorldOptions
            .world(world2)
            .seed(4321L)
        ).isSuccess)

        val getWorld = worldManager.getLoadedWorld("world2")
        assertTrue(getWorld.isDefined)
        val world = getWorld.get()
        assertNotNull(world)
        assertEquals(4321L, world.seed)
    }

    @Test
    fun `Clone world`() {
        assertTrue(worldManager.cloneWorld(CloneWorldOptions.fromTo(world, "cloneworld")).isSuccess)
        val getWorld = worldManager.getLoadedWorld("cloneworld")
        assertTrue(getWorld.isDefined)
        val world = getWorld.get()
        assertNotNull(world)
        assertEquals("cloneworld", world.name)
    }

    @Test
    fun `Clone world failed - invalid world name`() {
        assertEquals(
            CloneFailureReason.INVALID_WORLDNAME,
            worldManager.cloneWorld(CloneWorldOptions.fromTo(world, "HU(@*!#")).failureReason
        )
    }

    @Test
    fun `Clone world failed - target world exists and loaded`() {
        assertEquals(
            CloneFailureReason.WORLD_EXIST_LOADED,
            worldManager.cloneWorld(CloneWorldOptions.fromTo(world, "world2")).failureReason
        )
    }

    @Test
    fun `Clone world failed - target world exists but unloaded`() {
        assertTrue(worldManager.unloadWorld(UnloadWorldOptions.world(world2)).isSuccess)
        assertEquals(
            CloneFailureReason.WORLD_EXIST_UNLOADED,
            worldManager.cloneWorld(CloneWorldOptions.fromTo(world, "world2")).failureReason
        )
    }

    @Test
    fun `Clone world failed - target world folder exists`() {
        File(Bukkit.getWorldContainer(), "worldfolder").mkdir()
        assertEquals(
            CloneFailureReason.WORLD_EXIST_FOLDER,
            worldManager.cloneWorld(CloneWorldOptions.fromTo(world, "worldfolder")).failureReason
        )
    }

    @Test
    fun `Get potential worlds`() {
        File(Bukkit.getWorldContainer(), "newworld1").mkdir()
        File(Bukkit.getWorldContainer(), "newworld1/level.dat").createNewFile()
        File(Bukkit.getWorldContainer(), "newworld2").mkdir()
        File(Bukkit.getWorldContainer(), "newworld2/level.dat").createNewFile()
        assertEquals(setOf("newworld1", "newworld2"), worldManager.getPotentialWorlds().toSet())
    }

    @Test
    fun `Get world with alias`() {
        world.setAlias("testalias")
        assertEquals(world, worldManager.getLoadedWorldByNameOrAlias("testalias").orNull)
        assertEquals(world, worldManager.getWorldByNameOrAlias("testalias").orNull)
        assertNull(worldManager.getUnloadedWorldByNameOrAlias("testalias").orNull)
    }
}
