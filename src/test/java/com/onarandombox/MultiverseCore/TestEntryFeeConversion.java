package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.utils.TestInstanceCreator;
import org.bukkit.Material;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ MultiverseCore.class, PluginDescriptionFile.class, JavaPluginLoader.class})
@PowerMockIgnore("javax.script.*")
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
        assertNull(props.entryfee.getCurrency());

        entryFee.put("currency", 1);
        props = new WorldProperties(config);
        assertEquals(Material.STONE, props.entryfee.getCurrency());

        entryFee.put("currency", "1");
        props = new WorldProperties(config);
        assertEquals(Material.STONE, props.entryfee.getCurrency());

        entryFee.put("currency", "stone");
        props = new WorldProperties(config);
        assertEquals(Material.STONE, props.entryfee.getCurrency());
    }
}
