package org.mvplugins.multiverse.core.api

import org.mvplugins.multiverse.core.MultiverseCoreApi
import org.mvplugins.multiverse.core.TestWithMockBukkit
import kotlin.test.Test
import kotlin.test.assertNotNull

class MultiverseCoreApiTest : TestWithMockBukkit() {

    @Test
    fun `MultiverseCoreApi is available`() {
        val api = assertNotNull(MultiverseCoreApi.get())
        assertNotNull(api.blockSafety)
        assertNotNull(api.destinationsProvider)
        assertNotNull(api.generatorProvider)
        assertNotNull(api.locationManipulation)
        assertNotNull(api.mvCoreConfig)
        assertNotNull(api.safetyTeleporter)
        assertNotNull(api.worldManager)
    }

    @Test
    fun `Get MultiverseCoreApi from bukkit service`() {
        val registeredService = server.servicesManager.getRegistration(MultiverseCoreApi::class.java)
        val api = assertNotNull(registeredService?.provider)
        testApiAccess(api)
    }

    private fun testApiAccess(api: MultiverseCoreApi?) {
        assertNotNull(api?.anchorManager)
        assertNotNull(api?.blockSafety)
        assertNotNull(api?.destinationsProvider)
        assertNotNull(api?.mvEconomist)
        assertNotNull(api?.generatorProvider)
        assertNotNull(api?.locationManipulation)
        assertNotNull(api?.mvCoreConfig)
        assertNotNull(api?.safetyTeleporter)
        assertNotNull(api?.worldManager)
    }
}
