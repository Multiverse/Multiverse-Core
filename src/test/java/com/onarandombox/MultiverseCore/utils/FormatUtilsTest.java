package com.onarandombox.MultiverseCore.utils;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PluginManager.class, MultiverseCore.class, Permission.class, Bukkit.class, WorldManager.class,
        PluginDescriptionFile.class, JavaPluginLoader.class })
@PowerMockIgnore("javax.script.*")
public class FormatUtilsTest {

    private TestInstanceCreator creator;

    @Before
    public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void testParseColors() {
        assertEquals("&#rrggbb §c", FormatUtils.parseColors("&#rrggbb &c"));
        assertEquals("§x§a§a§b§b§c§c &v", FormatUtils.parseColors("&#aabbcc &v"));
    }

    @Test
    public void testParseLegacyColor() {
        assertEquals("§4 §c §6 §e §2 §a §b §3 §1 §9 §d §5 §f §7 §8 §0 §k §l §m §n §o §r",
                FormatUtils.parseLegacyColor("&4 &c &6 &e &2 &a &b &3 &1 &9 &d &5 &f &7 &8 &0 &k &l &m &n &o &r"));
    }

    @Test
    public void testParseRGBCode() {
        assertEquals("&#rrggbd", FormatUtils.parseRGBColor("&#rrggbd"));
        assertEquals("§x§1§2§3§4§5§6", FormatUtils.parseRGBColor("&#123456"));
        assertEquals("§x§7§8§9§0§A§B", FormatUtils.parseRGBColor("&#7890AB"));
        assertEquals("§x§c§d§e§f§D§C", FormatUtils.parseRGBColor("&#cdefDC"));
    }

    @Test
    public void testRemoveColors() {
        assertEquals("&#rrggbb ", FormatUtils.removeColors("&#rrggbb &c"));
        assertEquals(" &v", FormatUtils.removeColors("&#aabbcc &v"));
    }

    @Test
    public void testRemoveLegacyColor() {
        assertEquals("", FormatUtils.removeLegacyColor("&4&c&6&e&2&a&b&3&1&9&d&5&f&7&8&0&k&l&m&n&o&r"));
    }

    @Test
    public void testRemoveRGBCode() {
        assertEquals("&#rrggbd", FormatUtils.removeRGBColor("&#rrggbd"));
        assertEquals("", FormatUtils.removeRGBColor("&#123456"));
        assertEquals("", FormatUtils.removeRGBColor("&#7890AB"));
        assertEquals("", FormatUtils.removeRGBColor("&#cdefDC"));
    }
}
