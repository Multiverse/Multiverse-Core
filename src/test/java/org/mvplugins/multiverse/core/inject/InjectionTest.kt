package org.mvplugins.multiverse.core.inject

import com.onarandombox.MultiverseCore.MultiverseCore
import com.onarandombox.MultiverseCore.anchor.AnchorManager
import com.onarandombox.MultiverseCore.api.BlockSafety
import com.onarandombox.MultiverseCore.api.LocationManipulation
import com.onarandombox.MultiverseCore.api.MVCore
import com.onarandombox.MultiverseCore.api.MVPlugin
import com.onarandombox.MultiverseCore.api.MVWorldManager
import com.onarandombox.MultiverseCore.api.SafeTTeleporter
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager
import com.onarandombox.MultiverseCore.config.MVCoreConfigProvider
import com.onarandombox.MultiverseCore.economy.MVEconomist
import com.onarandombox.MultiverseCore.inject.wrapper.PluginDataFolder
import com.onarandombox.MultiverseCore.listeners.MVChatListener
import com.onarandombox.MultiverseCore.listeners.MVEntityListener
import com.onarandombox.MultiverseCore.listeners.MVPlayerListener
import com.onarandombox.MultiverseCore.listeners.MVPortalListener
import com.onarandombox.MultiverseCore.listeners.MVWeatherListener
import com.onarandombox.MultiverseCore.listeners.MVWorldInitListener
import com.onarandombox.MultiverseCore.listeners.MVWorldListener
import com.onarandombox.MultiverseCore.teleportation.SimpleBlockSafety
import com.onarandombox.MultiverseCore.teleportation.SimpleLocationManipulation
import com.onarandombox.MultiverseCore.teleportation.SimpleSafeTTeleporter
import com.onarandombox.MultiverseCore.utils.MVPermissions
import com.onarandombox.MultiverseCore.utils.UnsafeCallWrapper
import com.onarandombox.MultiverseCore.world.SimpleMVWorldManager
import org.bukkit.Server
import org.bukkit.plugin.PluginManager
import org.junit.jupiter.api.Test
import org.mvplugins.multiverse.core.TestWithMockBukkit
import java.io.File
import java.util.logging.Logger
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

class InjectionTest : TestWithMockBukkit() {

    @Test
    fun `Server is available as a service`() {
        assertNotNull(multiverseCore.getService(Server::class.java))
    }

    @Test
    fun `PluginManager is available as a service`() {
        assertNotNull(multiverseCore.getService(PluginManager::class.java))
    }

    @Test
    fun `MultiverseCore is available as a service`() {
        assertNotNull(multiverseCore.getService(MultiverseCore::class.java))
        assertNotNull(multiverseCore.getService(MVCore::class.java))
        assertNotNull(multiverseCore.getService(MVPlugin::class.java))
    }

    @Test
    fun `MultiverseCore service is the same instance of MultiverseCore that the MockBukkit server creates`() {
        assertSame(multiverseCore, multiverseCore.getService(MultiverseCore::class.java));
    }

    @Test
    fun `Logger is available as a service`() {
        assertNotNull(multiverseCore.getService(Logger::class.java));
    }

    @Test
    fun `PluginDataFolder is available as a service`() {
        assertNotNull(multiverseCore.getService(PluginDataFolder::class.java));
    }

    @Test
    fun `File is not available as a service`() {
        assertNull(multiverseCore.getService(File::class.java))
    }

    @Test
    fun `AnchorManager is available as a service`() {
        assertNotNull(multiverseCore.getService(AnchorManager::class.java))
    }

    @Test
    fun `BlockSafety is available as a service`() {
        assertNotNull(multiverseCore.getService(BlockSafety::class.java))
        assertNotNull(multiverseCore.getService(SimpleBlockSafety::class.java))
    }

    @Test
    fun `MVCommandManager is available as a service`() {
        assertNotNull(multiverseCore.getService(MVCommandManager::class.java))
    }

    @Test
    fun `MVEconomist is available as a service`() {
        assertNotNull(multiverseCore.getService(MVEconomist::class.java))
    }

    @Test
    fun `LocationManipulation is available as a service`() {
        assertNotNull(multiverseCore.getService(LocationManipulation::class.java))
        assertNotNull(multiverseCore.getService(SimpleLocationManipulation::class.java))
    }

    @Test
    fun `MVPermissions is available as a service`() {
        assertNotNull(multiverseCore.getService(MVPermissions::class.java))
    }

    @Test
    fun `SafeTTeleporter is available as a service`() {
        assertNotNull(multiverseCore.getService(SafeTTeleporter::class.java))
        assertNotNull(multiverseCore.getService(SimpleSafeTTeleporter::class.java))
    }

    @Test
    fun `UnsafeCallWrapper is available as a service`() {
        assertNotNull(multiverseCore.getService(UnsafeCallWrapper::class.java))
    }

    @Test
    fun `MVWorldManager is available as a service`() {
        assertNotNull(multiverseCore.getService(MVWorldManager::class.java))
        assertNotNull(multiverseCore.getService(SimpleMVWorldManager::class.java))
    }

    @Test
    fun `MVEntityListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVEntityListener::class.java))
    }

    @Test
    fun `MVPlayerListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVPlayerListener::class.java))
    }

    @Test
    fun `MVChatListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVChatListener::class.java))
    }

    @Test
    fun `MVPortalListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVPortalListener::class.java))
    }

    @Test
    fun `MVWeatherListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVWeatherListener::class.java))
    }

    @Test
    fun `MVWorldListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVWorldListener::class.java))
    }

    @Test
    fun `MVWorldInitListener is available as a service`() {
        assertNotNull(multiverseCore.getService(MVWorldInitListener::class.java))
    }

    @Test
    fun `MVCoreConfigProvider is available as a service`() {
        assertNotNull(multiverseCore.getService(MVCoreConfigProvider::class.java))
    }
}
