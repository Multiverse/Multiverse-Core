package org.mvplugins.multiverse.core.config

import org.bukkit.Material
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.HandlerList
import org.mvplugins.multiverse.core.dynamiclistener.DynamicListener
import org.mvplugins.multiverse.core.dynamiclistener.DynamicListenerRegistration
import org.mvplugins.multiverse.core.dynamiclistener.EventPriorityMapper
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventMethod
import org.mvplugins.multiverse.core.dynamiclistener.annotations.EventPriorityKey
import org.mvplugins.multiverse.core.dynamiclistener.annotations.DefaultEventPriority
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.mvplugins.multiverse.core.teleportation.TeleportQueue
import org.bukkit.Location
import org.mockbukkit.mockbukkit.entity.PlayerMock
import org.mvplugins.multiverse.core.TestWithMockBukkit
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter
import org.mvplugins.multiverse.core.world.WorldManager
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions
import java.lang.AssertionError
import kotlin.test.*

class ConfigTeleportInterceptTest : TestWithMockBukkit() {

    private lateinit var config: CoreConfig
    private lateinit var safetyTeleporter: AsyncSafetyTeleporter
    private lateinit var player: PlayerMock
    private lateinit var location: Location

    @BeforeTest
    fun setUp() {
        config = serviceLocator.getActiveService(CoreConfig::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("CoreConfig is not available as a service") }

        safetyTeleporter = serviceLocator.getActiveService(AsyncSafetyTeleporter::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("AsyncSafetyTeleporter is not available as a service") }

        val worldManager = serviceLocator.getActiveService(WorldManager::class.java).takeIf { it != null } ?: run {
            throw IllegalStateException("WorldManager is not available as a service") }

        player = server.addPlayer()
        worldManager.createWorld(CreateWorldOptions.worldName("world2")).get()
        location = Location(server.getWorld("world2"), 0.0, 5.0, 0.0)
        config.enforceAccess = true
    }

    @Test
    fun `Generic teleport with teleport intercept enabled`() {
        config.teleportIntercept = true
        assertFalse(player.teleport(location))
        assertEquals("world", player.world.name)
    }

    @Test
    fun `Generic teleport with teleport intercept disabled - no access checking`() {
        config.teleportIntercept = false
        assertTrue(player.teleport(location))
        assertEquals("world2", player.world.name)
    }

    @Test
    fun `Multiverse API teleport with teleport intercept disabled`() {
        config.teleportIntercept = false
        safetyTeleporter.to(location).teleport(player).toAttempt()
            .onSuccess (Runnable { throw AssertionError("Teleport should have failed") })
            .onFailure(Runnable { assertEquals("world", player.world.name) })
    }

    private fun configureEntryFee(price: Double = 2.0) {
        config.teleportIntercept = true
        config.enforceAccess = false
        val worldManager = serviceLocator.getActiveService(WorldManager::class.java)!!
        val world = worldManager.getLoadedWorld("world2").get()
        world.setEntryFeeEnabled(true).get()
        world.setCurrency(Material.DIAMOND).get()
        world.setPrice(price).get()
        player.inventory.setItem(0, ItemStack(Material.DIAMOND, 2))
    }

    private fun onTeleport(priority: EventPriority = EventPriority.HIGHEST, action: (PlayerTeleportEvent) -> Unit) {
        server.pluginManager.registerEvent(PlayerTeleportEvent::class.java, object : Listener {}, priority,
            { _, event -> action(event as PlayerTeleportEvent) }, multiverseCore)
    }

    private fun diamonds(payer: PlayerMock = player): Int =
        payer.inventory.all(Material.DIAMOND).values.sumOf { it.amount }

    @Test
    fun `Late cancellation does not charge an entry fee`() {
        configureEntryFee()
        onTeleport { it.isCancelled = true }
        assertFalse(player.teleport(location))
        assertEquals("world", player.world.name)
        assertEquals(2, diamonds())
    }

    @Test
    fun `Successful teleport charges exactly once`() {
        configureEntryFee()
        var balanceBeforeMonitor = -1
        onTeleport { balanceBeforeMonitor = diamonds() }
        assertTrue(player.teleport(location))
        assertEquals(2, balanceBeforeMonitor)
        assertEquals("world2", player.world.name)
        assertEquals(0, diamonds())
    }

    @Test
    fun `Pre-cancelled teleport does not charge`() {
        configureEntryFee()
        onTeleport(EventPriority.LOWEST) { it.isCancelled = true }
        assertFalse(player.teleport(location))
        assertEquals(2, diamonds())
    }

    @Test
    fun `Insufficient funds still cancels before monitor`() {
        configureEntryFee()
        player.inventory.setItem(0, ItemStack(Material.DIAMOND, 1))
        assertFalse(player.teleport(location))
        assertEquals("world", player.world.name)
        assertEquals(1, diamonds())
    }

    @Test
    fun `Same-world teleport remains free`() {
        configureEntryFee()
        assertTrue(player.teleport(Location(player.world, 5.0, 5.0, 5.0)))
        assertEquals(2, diamonds())
    }

    @Test
    fun `Late cancellation does not grant a negative entry fee`() {
        configureEntryFee(-2.0)
        onTeleport { it.isCancelled = true }
        assertFalse(player.teleport(location))
        assertEquals(2, diamonds())
    }

    @Test
    fun `Successful teleport retains negative entry fee rewards`() {
        configureEntryFee(-2.0)
        assertTrue(player.teleport(location))
        assertEquals(4, diamonds())
    }

    @Test
    fun `Queued teleporter pays instead of the teleported player`() {
        configureEntryFee()
        val payer = server.addPlayer()
        payer.inventory.setItem(0, ItemStack(Material.DIAMOND, 2))
        serviceLocator.getActiveService(TeleportQueue::class.java)!!.addToQueue(payer, player)
        assertTrue(player.teleport(location))
        assertEquals(0, diamonds(payer))
        assertEquals(2, diamonds())
    }

    @Test
    fun `Redirecting the destination does not charge for the original world`() {
        configureEntryFee()
        onTeleport { it.to = Location(player.world, 5.0, 5.0, 5.0) }
        assertTrue(player.teleport(location))
        assertEquals("world", player.world.name)
        assertEquals(2, diamonds())
    }

    @Test
    fun `World access denial remains enforced before payment`() {
        configureEntryFee()
        config.enforceAccess = true
        assertFalse(player.teleport(location))
        assertEquals("world", player.world.name)
        assertEquals(2, diamonds())
    }

    @Test
    fun `Cancelled payment does not leak into the next teleport`() {
        configureEntryFee()
        var cancel = true
        onTeleport { it.isCancelled = cancel }
        assertFalse(player.teleport(location))
        assertEquals(2, diamonds())
        cancel = false
        assertTrue(player.teleport(location))
        assertEquals(0, diamonds())
    }

    private fun setPermissionCheckPriority(priority: EventPriority) {
        val listener = PlayerTeleportEvent.getHandlerList().registeredListeners
            .first { it.listener.javaClass.simpleName == "MVPlayerListener" }.listener as DynamicListener
        HandlerList.unregisterAll(listener)
        serviceLocator.getActiveService(EventPriorityMapper::class.java)!!
            .setPriority("mvcore-player-teleport", priority)
        serviceLocator.getActiveService(DynamicListenerRegistration::class.java)!!.register(listener, multiverseCore)
    }

    @Test
    fun `Custom permission-check priority still defers payment until monitor`() {
        configureEntryFee()
        setPermissionCheckPriority(EventPriority.LOW)
        onTeleport(EventPriority.NORMAL) { it.isCancelled = true }
        assertFalse(player.teleport(location))
        assertEquals(2, diamonds())
    }

    @Test
    fun `Every configurable permission-check priority collects exactly one fee`() {
        configureEntryFee()
        var cancel = true
        onTeleport { it.isCancelled = cancel }
        for (priority in EventPriority.entries) {
            setPermissionCheckPriority(priority)
            player.inventory.setItem(0, ItemStack(Material.DIAMOND, 2))
            cancel = true
            assertFalse(player.teleport(location), priority.name)
            assertEquals(2, diamonds(), priority.name)
            cancel = false
            assertTrue(player.teleport(location), priority.name)
            assertEquals(0, diamonds(), priority.name)
            assertTrue(player.teleport(Location(server.getWorld("world"), 0.0, 5.0, 0.0)))
        }
    }

    class OrderedListener : DynamicListener {
        val calls = mutableListOf<String>()

        @EventMethod
        @EventPriorityKey("test-fee-validation")
        @DefaultEventPriority(EventPriority.HIGHEST)
        fun validate(event: PlayerTeleportEvent) {
            calls.add("validate")
        }

        @EventMethod
        @DefaultEventPriority(EventPriority.MONITOR)
        fun collect(event: PlayerTeleportEvent) {
            calls.add("collect")
        }
    }

    @Test
    fun `Default monitor handler remains after validation when configured priorities tie`() {
        val listener = OrderedListener()
        serviceLocator.getActiveService(EventPriorityMapper::class.java)!!
            .setPriority("test-fee-validation", EventPriority.MONITOR)
        serviceLocator.getActiveService(DynamicListenerRegistration::class.java)!!.register(listener, multiverseCore)
        server.pluginManager.callEvent(PlayerTeleportEvent(player, player.location, location))
        assertEquals(listOf("validate", "collect"), listener.calls)
    }

    @Test
    fun `Fee exemption remains free`() {
        configureEntryFee()
        player.addAttachment(multiverseCore, "multiverse.exempt.world2", true)
        assertTrue(player.teleport(location))
        assertEquals(2, diamonds())
    }

    @Test
    fun `Console initiated teleport remains free`() {
        configureEntryFee()
        serviceLocator.getActiveService(TeleportQueue::class.java)!!.addToQueue(server.consoleSender, player)
        assertTrue(player.teleport(location))
        assertEquals(2, diamonds())
    }
}
