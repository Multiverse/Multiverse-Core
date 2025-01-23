package org.mvplugins.multiverse.core.economy;

import jakarta.inject.Inject;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * Multiverse's Friendly Economist. This is used to deal with external economies and also item costs for stuff in MV.
 */
@Service
public final class MVEconomist {
    public static final String VAULT_ECONOMY_CODE = "@vault-economy";
    public static final Material VAULT_ECONOMY_MATERIAL = Material.AIR;

    private final VaultHandler vaultHandler;
    private final ItemEconomy itemEconomy;

    @Inject
    public MVEconomist(MultiverseCore plugin, ItemEconomy itemEconomy) {
        vaultHandler = new VaultHandler(plugin);
        this.itemEconomy = itemEconomy;
    }

    private boolean isUsingVault(Material currency) {
        return !isItemCurrency(currency) && getVaultHandler().hasEconomy();
    }

    /**
     * Checks if an economy plugin is in use.
     *
     * @return true if an economy plugin is detected by Vault.
     */
    public boolean isUsingEconomyPlugin() {
        return getVaultHandler().hasEconomy();
    }

    /**
     * Formats the amount to a human readable currency string.
     *
     * @param amount the amount of currency.
     * @param currency the type of currency. Null indicates a non-item currency is used.
     * @return the human readable currency string.
     */
    public String formatPrice(double amount, @Nullable Material currency) {
        if (isUsingVault(currency)) {
            return getVaultHandler().getEconomy().format(amount);
        } else {
            return itemEconomy.getFormattedPrice(amount, currency);
        }
    }

    /**
     * Returns the name of the economy in use.
     *
     * @return the name of the economy in use.
     */
    public String getEconomyName() {
        if (getVaultHandler().hasEconomy()) {
            return getVaultHandler().getEconomy().getName();
        } else {
            return itemEconomy.getName();
        }
    }

    /**
     * Determines if a player has enough of a given currency.
     *
     * @param player the player to check for currency.
     * @param amount the amount of currency.
     * @param currency the type of currency. Null indicates non-item currency is used.
     * @return true if the player has enough of the given currency or the amount is 0 or less.
     */
    public boolean isPlayerWealthyEnough(Player player, double amount, Material currency) {
        if (amount <= 0D) {
            return true;
        } else if (isUsingVault(currency)) {
            return getVaultHandler().getEconomy().has(player, amount);
        } else {
            return itemEconomy.hasEnough(player, amount, currency);
        }
    }

    /**
     * Formats a message for a player indicating they don't have enough currency.
     *
     * @param currency the type of currency. Null indicates a non-item currency is used.
     * @param message The more specific message to append to the generic message of not having enough.
     * @return the formatted insufficient funds message.
     */
    public String getNSFMessage(Material currency, String message) {
        return "Sorry, you don't have enough " + (isItemCurrency(currency) ? "items" : "funds") + ". " + message;
    }

    /**
     * Pays for a given amount of currency either from the player's economy account or inventory if the currency.
     *
     * @param player    the player to deposit currency into.
     * @param world     the world to take entry fee from.
     */
    public void payEntryFee(Player player, MultiverseWorld world) {
        payEntryFee(player, world.getPrice(), world.getCurrency());
    }

    /**
     * Pays for a given amount of currency either from the player's economy account or inventory if the currency.
     *
     * @param player    the player to take currency from.
     * @param price     the amount to take.
     * @param currency  the type of currency.
     */
    public void payEntryFee(Player player, double price, Material currency) {
        if (price == 0D) {
            return;
        }

        if (price < 0) {
            this.deposit(player, -price, currency);
        } else {
            this.withdraw(player, price, currency);
        }
    }

    /**
     * Deposits a given amount of currency either into the player's economy account or inventory if the currency
     * is not null.
     *
     * @param player the player to give currency to.
     * @param amount the amount to give.
     * @param currency the type of currency.
     */
    public void deposit(Player player, double amount, @Nullable Material currency) {
        if (isUsingVault(currency)) {
            getVaultHandler().getEconomy().depositPlayer(player, amount);
        } else {
            itemEconomy.deposit(player, amount, currency);
        }
    }

    /**
     * Withdraws a given amount of currency either from the player's economy account or inventory if the currency
     * is not null.
     *
     * @param player the player to take currency from.
     * @param amount the amount to take.
     * @param currency the type of currency.
     */
    public void withdraw(Player player, double amount, @Nullable Material currency) {
        if (isUsingVault(currency)) {
            getVaultHandler().getEconomy().withdrawPlayer(player, amount);
        } else {
            itemEconomy.withdraw(player, amount, currency);
        }
    }

    /**
     * Returns the economy balance of the given player.
     *
     * @param player the player to get the balance for.
     * @return the economy balance of the given player.
     * @throws IllegalStateException thrown if this is used when no economy plugin is available.
     */
    public double getBalance(Player player) throws IllegalStateException {
        return getBalance(player, null);
    }

    /**
     * Returns the economy balance of the given player in the given world. If the economy plugin does not have world
     * specific balances then the global balance will be returned.
     *
     * @param player the player to get the balance for.
     * @param world the world to get the balance for.
     * @return the economy balance of the given player in the given world.
     * @throws IllegalStateException thrown if this is used when no economy plugin is available.
     */
    public double getBalance(Player player, World world) throws IllegalStateException {
        if (!isUsingEconomyPlugin()) {
            throw new IllegalStateException("getBalance is only available when using an economy plugin with Vault");
        }
        if (world != null) {
            return getVaultHandler().getEconomy().getBalance(player, world.getName());
        } else {
            return getVaultHandler().getEconomy().getBalance(player);
        }
    }

    /**
     * Sets the economy balance for the given player.
     *
     * @param player the player to set the balance for.
     * @param amount the amount to set the player's balance to.
     * @throws IllegalStateException thrown if this is used when no economy plugin is available.
     */
    public void setBalance(Player player, double amount) throws IllegalStateException {
        setBalance(player, null, amount);
    }

    /**
     * Sets the economy balance for the given player in the given world. If the economy plugin does not have world
     * specific balances then the global balance will be set.
     *
     * @param player the player to set the balance for.
     * @param world the world to get the balance for.
     * @param amount the amount to set the player's balance to.
     * @throws IllegalStateException thrown if this is used when no economy plugin is available.
     */
    public void setBalance(Player player, World world, double amount) throws IllegalStateException {
        if (!isUsingEconomyPlugin()) {
            throw new IllegalStateException("getBalance is only available when using an economy plugin with Vault");
        }
        if (world != null) {
            getVaultHandler().getEconomy().withdrawPlayer(player, world.getName(), getBalance(player, world));
            getVaultHandler().getEconomy().depositPlayer(player, world.getName(), amount);
        } else {
            getVaultHandler().getEconomy().withdrawPlayer(player, getBalance(player));
            getVaultHandler().getEconomy().depositPlayer(player, amount);
        }
    }

    /**
     * This method is public for backwards compatibility.
     *
     * @return the old VaultHandler.
     */
    private VaultHandler getVaultHandler() {
        return vaultHandler;
    }

    /**
     * Determines if the currency type string given represents an item currency.
     *
     * @param currency the type of currency.
     * @return true if currency string matches a valid material.
     */
    public static boolean isItemCurrency(Material currency) {
        return currency != VAULT_ECONOMY_MATERIAL;
    }

}
