package com.onarandombox.MultiverseCore;

import java.util.HashMap;
import java.util.Map;

import com.onarandombox.MultiverseCore.utils.TestInstanceCreator;
import com.onarandombox.MultiverseCore.world.WorldProperties;
import org.bukkit.Material;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestEntryFeeConversion {

    private TestInstanceCreator creator;
    private MultiverseCore core;
    Map<String, Object> config;
    Map<String, Object> entryFee;

    @Before
    public void setUp() {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        core = creator.getCore();

        config = new HashMap<>();
        entryFee = new HashMap<>();
        config.put("entryfee", entryFee);
        entryFee.put("==", "MVEntryFee");
    }

    @After
    public void tearDown() {
        creator.tearDown();
    }

    @Test
    public void testConvertIntegerCurrencyToMaterialCurrency() {
        entryFee.put("currency", -1);
        WorldProperties props = new WorldProperties(config);
        assertNull(props.getCurrency());

        entryFee.put("currency", 0);
        props = new WorldProperties(config);
        assertNull(props.getCurrency());

        entryFee.put("currency", 1);
        props = new WorldProperties(config);
        assertEquals(Material.STONE, props.getCurrency());

        entryFee.put("currency", "1");
        props = new WorldProperties(config);
        assertEquals(Material.STONE, props.getCurrency());

        entryFee.put("currency", "stone");
        props = new WorldProperties(config);
        assertEquals(Material.STONE, props.getCurrency());
    }
}
