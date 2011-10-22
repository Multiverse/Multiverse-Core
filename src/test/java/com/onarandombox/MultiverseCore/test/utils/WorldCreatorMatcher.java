/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test.utils;

import org.bukkit.WorldCreator;
import org.mockito.ArgumentMatcher;

public class WorldCreatorMatcher extends ArgumentMatcher<WorldCreator> {
    private WorldCreator worldCreator;
    private boolean careAboutSeeds = false;
    private boolean careAboutGenerators = false;

    public WorldCreatorMatcher(WorldCreator creator) {
        System.out.println("Creating NEW world matcher.(" + creator.name() + ")");
        this.worldCreator = creator;
    }

    public void careAboutSeeds(boolean doICare) {
        this.careAboutSeeds = doICare;
    }

    public void careAboutGenerators(boolean doICare) {
        this.careAboutGenerators = doICare;
    }

    public boolean matches(Object creator) {
        System.out.println("Checking world creators.");
        if (creator == null) {
            System.out.println("The given creator was null, but I was checking: " + this.worldCreator.name());
            return false;
        }
        System.out.println("Checking Names...(" + ((WorldCreator) creator).name() + ") vs (" + this.worldCreator.name() + ")");
        System.out.println("Checking Envs...(" + ((WorldCreator) creator).environment() + ") vs (" + this.worldCreator.environment() + ")");
        if (!((WorldCreator) creator).name().equals(this.worldCreator.name())) {
            return false;
        } else if (!((WorldCreator) creator).environment().equals(this.worldCreator.environment())) {
            System.out.println("Checking Environments...");
            return false;
        } else if (careAboutSeeds && ((WorldCreator) creator).seed() != this.worldCreator.seed()) {
            System.out.print("Checking Seeds...");
            return false;
        } else if (careAboutGenerators && !((WorldCreator) creator).generator().equals(this.worldCreator.generator())) {
            System.out.print("Checking Gens...");
            return false;
        }
        System.out.println("Creators matched!!!");
        return true;
    }
}
