package org.mvplugins.multiverse.core.anchor

import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.world.location.UnloadedWorldLocation
import kotlin.test.*

class AnchorManagerTest : TestWithMockBukkit() {

    private lateinit var anchorManager: AnchorManager

    @BeforeTest
    fun setUp() {
        anchorManager = serviceLocator.getActiveService(AnchorManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("AnchorManager is not available as a service") }

        writeResourceFileToPluginDataFolder("/anchors/default_anchors.yml", "anchors.yml")
        assertTrue(anchorManager.loadAnchors().isSuccess)
    }

    @Test
    fun `Loaded correct Anchors from file`() {
        assertEquals(2, anchorManager.allAnchors.size)

        val a1 = anchorManager.getAnchor("a1").orNull
        assertNotNull(a1)
        assertLocationEquals(UnloadedWorldLocation("w1", 0.5, 70.0, 0.5, -68.25f, 10.35f), a1.location)

        val a2 = anchorManager.getAnchor("a2").orNull
        assertNotNull(a2)
        assertLocationEquals(UnloadedWorldLocation("w2", 3.3, 9.9, 6.6, -1.1f, 2.0f), a2.location)
    }

    @Test
    fun `Set new location for existing anchor`() {
        val a1 = anchorManager.getAnchor("a1").orNull
        assertNotNull(a1)
        val newLocation = UnloadedWorldLocation("w1", 1.0, 1.0, 1.0, 1.0f, 1.0f)
        anchorManager.setAnchor("a1", newLocation)
        assertLocationEquals(newLocation, a1.location)
    }

    @Test
    fun `Create new anchor`() {
        val newLocation = UnloadedWorldLocation("w3", 3.0, 3.0, 3.0, -3.0f, 3.0f)
        anchorManager.setAnchor("a3", newLocation)
        assertLocationEquals(newLocation, anchorManager.getAnchor("a3").orNull?.location)

        assertConfigEquals("/anchors/anchors_saved.yml", "anchors.yml")
    }

    @Test
    fun `Delete anchor`() {
        val a1 = anchorManager.getAnchor("a1").orNull
        assertNotNull(a1)
        anchorManager.deleteAnchor(a1)
        assertNull(anchorManager.getAnchor("a1").orNull)
    }
}