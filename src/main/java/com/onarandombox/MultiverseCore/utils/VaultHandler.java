package com.onarandombox.MultiverseCore.utils;

import com.dumptruckman.minecraft.util.Logging;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * A class we use to interface with Vault when it is present.
 */
public class VaultHandler implements Listener {

    private Economy economy;

    public VaultHandler(final Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new VaultListener(), plugin);
        setupVaultEconomy();
    }

    private boolean setupVaultEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            final RegisteredServiceProvider<Economy> economyProvider =
                    Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (economyProvider != null) {
                Logging.fine("Vault economy enabled.");
                economy = economyProvider.getProvider();
            } else {
                Logging.finer("Vault economy not detected.");
                economy = null;
            }
        } else {
            Logging.finer("Vault was not found.");
            economy = null;
        }

        return (economy != null);
    }

    /**
     * Listens for Vault plugin events.
     */
    private class VaultListener implements Listener {
        @EventHandler
        private void vaultEnabled(PluginEnableEvent event) {
            if (event.getPlugin() != null && event.getPlugin().getName().equals("Vault")) {
                setupVaultEconomy();
            }
        }

        @EventHandler
        private void vaultDisabled(PluginDisableEvent event) {
            if (event.getPlugin() != null && event.getPlugin().getName().equals("Vault")) {
                Logging.fine("Vault economy disabled");
                economy = null;
            }
        }
    }

    /**
     * Checks whether Vault is in use and has an economy system enabled.
     *
     * @return true if vault is in use and has an economy system enabled.
     */
    public boolean hasEconomy() {
        return economy != null;
    }

    /**
     * Returns the Vault economy system if Vault is present and has an economy system enabled.
     *
     * @return The vault economy system or null if not configured.
     */
    public Economy getEconomy() {
        return economy;
    }
}
