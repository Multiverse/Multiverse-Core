package org.mvplugins.multiverse.core.teleportation;

import java.util.HashMap;
import java.util.Map;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jvnet.hk2.annotations.Service;

@Service
public final class TeleportQueue {

    private final Map<String, String> teleportQueueMap;

    TeleportQueue() {
        teleportQueueMap = new HashMap<>();
    }

    /**
     * This method is used to add a teleportation to the teleportQueue.
     *
     * @param teleporter The sender that initiated the teleportation.
     * @param teleportee The player that will be teleported.
     */
    public void addToQueue(CommandSender teleporter, Player teleportee) {
        addToQueue(teleporter.getName(), teleportee.getName());
    }

    /**
     * This method is used to add a teleportation to the teleportQueue.
     *
     * @param teleporter The name of the sender that initiated the teleportation.
     * @param teleportee The name of the player that will be teleported.
     */
    public void addToQueue(String teleporter, String teleportee) {
        Logging.finest("Adding mapping '%s' => '%s' to teleport queue", teleporter, teleportee);
        teleportQueueMap.put(teleportee, teleporter);
    }

    /**
     * This method is used to find out who is teleporting a player.
     * @param playerName The teleported player (the teleportee).
     * @return The player that teleported the other one (the teleporter).
     */
    public Option<String> popFromQueue(String playerName) {
        if (teleportQueueMap.containsKey(playerName)) {
            String teleportee = teleportQueueMap.get(playerName);
            teleportQueueMap.remove(playerName);
            return Option.of(teleportee);
        }
        return Option.none();
    }
}
