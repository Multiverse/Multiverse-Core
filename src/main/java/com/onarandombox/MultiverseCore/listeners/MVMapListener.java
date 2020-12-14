package com.onarandombox.MultiverseCore.listeners;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

/**
 * A listener for bukkit map events.
 */
public class MVMapListener implements Listener {

    private final MultiverseCore plugin;

    public MVMapListener(final MultiverseCore plugin) {
        this.plugin = plugin;
    }

    /**
     * This method is called when a map is initialized.
     * We need this to set the correct player location on map.
     *
     * @param event The event that was fired.
     */
    @EventHandler
    public void mapInitialize(final MapInitializeEvent event) {
        final MapView map = event.getMap();
        final Player player = getPlayerHoldingMap(map);
        if (player == null) {
            Logging.fine("No player found with holding map %s.", map.getId());
            return;
        }

        Logging.fine("Setting map %s location that %s is holding.", map.getId(), player.getName());
        Location playerLoc = player.getLocation();
        map.setCenterX(playerLoc.getBlockX());
        map.setCenterZ(playerLoc.getBlockZ());
        map.setWorld(playerLoc.getWorld());
    }

    private Player getPlayerHoldingMap(final MapView map) {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> {
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    return isMapMaterial(itemInHand.getType()) && isMatchingMap(itemInHand.getItemMeta(), map);
                })
                .findFirst()
                .orElse(null);
    }

    private boolean isMapMaterial(final Material type) {
        return type == Material.MAP || type == Material.FILLED_MAP;
    }

    private boolean isMatchingMap(ItemMeta meta, MapView map) {
        MapMeta mapMeta = (MapMeta) meta;
        return mapMeta != null
                && mapMeta.getMapView() != null
                && mapMeta.getMapView().getId() == map.getId();
    }
}
