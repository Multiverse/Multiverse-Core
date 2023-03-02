package com.onarandombox.MultiverseCore;

import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.WorldPurger;
import com.onarandombox.MultiverseCore.utils.TestInstanceCreator;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Zombie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TestWorldPurger {
    TestInstanceCreator creator;
    MultiverseCore core;
    WorldPurger purger;

    MVWorld mvWorld;
    World cbworld;

    Sheep sheep;
    Zombie zombie;

    @Before
    public void setUp() throws Exception {
        creator = new TestInstanceCreator();
        assertTrue(creator.setUp());
        core = creator.getCore();
        purger = core.getMVWorldManager().getTheWorldPurger();
        core.getMVConfig().setGlobalDebug(3);
        mvWorld = mock(MVWorld.class);
        cbworld = mock(World.class);
        when(mvWorld.getCBWorld()).thenReturn(cbworld);
    }

    @After
    public void tearDown() throws Exception {
        creator.tearDown();
    }

    @Test
    public void test() {
        // test 1: purge ALL without negations ==> both should be removed
        createAnimals();
        purger.purgeWorld(mvWorld, Arrays.asList("ALL"), false, false);
        verify(sheep).remove();
        verify(zombie).remove();

        // test 2: purge ALL with one negation ==> the zombie should survive
        createAnimals();
        purger.purgeWorld(mvWorld, Arrays.asList("ALL"), false, true);
        verify(sheep).remove();
        verify(zombie, never()).remove();

        // test 3: purge ALL with both negations ==> everybody should survive
        createAnimals();
        purger.purgeWorld(mvWorld, Arrays.asList("ALL"), true, true);
        verify(sheep, never()).remove();
        verify(zombie, never()).remove();

        // test 4: purge ANIMALS without negations ==> the zombie should survive
        createAnimals();
        purger.purgeWorld(mvWorld, Arrays.asList("ANIMALS"), false, false);
        verify(sheep).remove();
        verify(zombie, never()).remove();

        // test 5: purge MONSTERS with one negation ==> nobody should survive
        createAnimals();
        purger.purgeWorld(mvWorld, Arrays.asList("MONSTERS"), true, false);
        verify(sheep).remove();
        verify(zombie).remove();

        // test 6: purge MONSTERS both negations ==> the zombie should survive
        createAnimals();
        purger.purgeWorld(mvWorld, Arrays.asList("MONSTERS"), true, true);
        verify(sheep).remove();
        verify(zombie, never()).remove();

        // test 7: purge SHEEP without negations ==> the zombie should survive
        createAnimals();
        purger.purgeWorld(mvWorld, Arrays.asList("SHEEP"), false, false);
        verify(sheep).remove();
        verify(zombie, never()).remove();

        // test 8: purge SHEEP with one negation ==> nobody should survive
        createAnimals();
        purger.purgeWorld(mvWorld, Arrays.asList("SHEEP"), false, true);
        verify(sheep).remove();
        verify(zombie).remove();

        // test 9: purge ZOMBIE with both negations ==> the zombie should survive
        createAnimals();
        purger.purgeWorld(mvWorld, Arrays.asList("ZOMBIE"), true, true);
        verify(sheep).remove();
        verify(zombie, never()).remove();

        // I like sheep.
    }

    private void createAnimals() {
        World world = mvWorld.getCBWorld();
        sheep = mock(Sheep.class);
        when(sheep.getType()).thenReturn(EntityType.SHEEP);
        when(sheep.getWorld()).thenReturn(world);
        zombie = mock(Zombie.class);
        when(zombie.getType()).thenReturn(EntityType.ZOMBIE);
        when(zombie.getWorld()).thenReturn(world);
        when(cbworld.getEntities()).thenReturn(Arrays.asList(sheep, zombie));
    }
}
