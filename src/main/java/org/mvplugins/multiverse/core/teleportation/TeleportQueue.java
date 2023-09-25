package org.mvplugins.multiverse.core.teleportation;

import java.util.HashMap;
import java.util.Map;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import org.jvnet.hk2.annotations.Service;

@Service
public class TeleportQueue {

    private final Map<String, String> teleportQueue;

    TeleportQueue() {
        teleportQueue = new HashMap<>();
    }

    /**
     * This method is used to add a teleportation to the teleportQueue.
     *
     * @param teleporter The name of the player that initiated the teleportation.
     * @param teleportee The name of the player that was teleported.
     */
    public void addToQueue(String teleporter, String teleportee) {
        Logging.finest("Adding mapping '%s' => '%s' to teleport queue", teleporter, teleportee);
        teleportQueue.put(teleportee, teleporter);
    }

    /**
     * This method is used to find out who is teleporting a player.
     *
     * @param playerName The teleported player (the teleportee).
     * @return The player that teleported the other one (the teleporter).
     */
    public Option<String> popFromQueue(String playerName) {
        if (teleportQueue.containsKey(playerName)) {
            String teleportee = teleportQueue.get(playerName);
            teleportQueue.remove(playerName);
            return Option.of(teleportee);
        }
        return Option.none();
    }
}
