package org.mvplugins.multiverse.core.world

import org.bukkit.Bukkit
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.world.helpers.WorldNameChecker
import java.io.File
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.*

class WorldNameCheckerTest : TestWithMockBukkit() {

    lateinit var worldNameChecker: WorldNameChecker

    @BeforeTest
    fun setUp() {
        worldNameChecker = serviceLocator.getActiveService(WorldNameChecker::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldNameChecker is not available as a service") }
    }

    @Test
    fun `Valid world name`() {
        assertEquals(WorldNameChecker.NameStatus.VALID, worldNameChecker.checkName("test"))
    }

    @Test
    fun `Invalid characters in world name`() {
        assertEquals(WorldNameChecker.NameStatus.INVALID_CHARS, worldNameChecker.checkName("test!"))
    }

    @Test
    fun `Empty world name`() {
        assertEquals(WorldNameChecker.NameStatus.EMPTY, worldNameChecker.checkName(null))
        assertEquals(WorldNameChecker.NameStatus.EMPTY, worldNameChecker.checkName(""))
    }

    @Test
    fun `Blacklisted world name`() {
        assertEquals(WorldNameChecker.NameStatus.BLACKLISTED, worldNameChecker.checkName("logs"))
        assertEquals(WorldNameChecker.NameStatus.BLACKLISTED, worldNameChecker.checkName("plugins"))
    }

    @Test
    fun `Valid world folder`() {
        File(Bukkit.getWorldContainer(), "test").mkdir()
        File(Bukkit.getWorldContainer(), "test/level.dat").createNewFile()
        assertEquals(WorldNameChecker.FolderStatus.VALID, worldNameChecker.checkFolder("test"))
    }

    @Test
    fun `Not a valid world folder`() {
        File(Bukkit.getWorldContainer(), "test").mkdir()
        File(Bukkit.getWorldContainer(), "test/random.txt").createNewFile()
        assertEquals(WorldNameChecker.FolderStatus.NOT_A_WORLD, worldNameChecker.checkFolder("test"))
    }

    @Test
    fun `World folder does not exist`() {
        File(Bukkit.getWorldContainer(), "test").mkdir()
        assertEquals(WorldNameChecker.FolderStatus.DOES_NOT_EXIST, worldNameChecker.checkFolder("test2"))
    }
}
