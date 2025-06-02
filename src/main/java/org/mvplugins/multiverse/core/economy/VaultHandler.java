package org.mvplugins.multiverse.core.economy;

import com.dumptruckman.minecraft.util.Logging;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.locale.MVCorei18n;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

/**
 * A class we use to interface with Vault when it is present.
 */
final class VaultHandler implements Listener {

    private Economy economy;
    private final MVCommandManager commandManager;

    VaultHandler(final Plugin plugin, @NotNull MVCommandManager commandManager) {
        this.commandManager = commandManager;
        Bukkit.getPluginManager().registerEvents(new VaultListener(), plugin);
        setupVaultEconomy();
    }

    private boolean setupVaultEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            final RegisteredServiceProvider<Economy> economyProvider = Bukkit
                    .getServicesManager()
                    .getRegistration(net.milkbowl.vault.economy.Economy.class);
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

        return economy != null;
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

    void showReceipt(Player player, double price) {
        if (price > 0D) {
            commandManager.getCommandIssuer(player).sendInfo(MVCorei18n.ECONOMY_VAULT_WITHDRAW,
                    replace("{price}").with(economy.format(price)));
        } else if (price < 0D) {
            commandManager.getCommandIssuer(player).sendInfo(MVCorei18n.ECONOMY_VAULT_DEPOSIT,
                    replace("{price}").with(economy.format(price)));
        }
    }

    /**
     * Listens for Vault plugin events.
     */
    private final class VaultListener implements Listener {
        @EventHandler
        private void vaultEnabled(PluginEnableEvent event) {
            if (event.getPlugin().getName().equals("Vault")) {
                setupVaultEconomy();
            }
        }

        @EventHandler
        private void vaultDisabled(PluginDisableEvent event) {
            if (event.getPlugin().getName().equals("Vault")) {
                Logging.fine("Vault economy disabled");
                economy = null;
            }
        }
    }
}
