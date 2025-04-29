package org.mvplugins.multiverse.core.event.world;

import org.bukkit.event.Event;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

abstract class MultiverseWorldEvent<W extends MultiverseWorld> extends Event {
    protected final W world;

    MultiverseWorldEvent(W world) {
        this.world = world;
    }

    /**
     * Gets the world that's about to be deleted.
     *
     * @return That {@link LoadedMultiverseWorld}.
     */
    public W getWorld() {
        return world;
    }
}
