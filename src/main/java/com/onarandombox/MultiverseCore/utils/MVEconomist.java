package com.onarandombox.MultiverseCore.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

/**
 * Multiverse's Friendly Economist. This is used to deal with external economies and also item costs for stuff in MV.
 */
public class MVEconomist {

    private final VaultHandler vaultHandler;

    public MVEconomist(Plugin plugin) {
        vaultHandler = new VaultHandler(plugin);
    }

    private boolean isUsingVault(int currency) {
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
     * @param currency the type of currency. A value greater than -1 indicates the material type used for currency.
     * @return the human readable currency string.
     */
    public String formatPrice(double amount, int currency) {
        if (isUsingVault(currency)) {
            return getVaultHandler().getEconomy().format(amount);
        } else {
            return ItemEconomy.getFormattedPrice(amount, currency);
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
            return ItemEconomy.getName();
        }
    }

    /**
     * Determines if a player has enough of a given currency.
     * @param player the player to check for currency.
     * @param amount the amount of currency.
     * @param currency the type of currency. A value greater than -1 indicates the material type used for currency.
     * @return true if the player has enough of the given currency or the amount is 0 or less.
     */
    public boolean isPlayerWealthyEnough(Player player, double amount, int currency) {
        if (amount <= 0D) {
            return true;
        } else if (isUsingVault(currency)) {
            return getVaultHandler().getEconomy().has(player, amount);
        } else {
            return ItemEconomy.hasEnough(player, amount, currency);
        }
    }

    /**
     * Formats a message for a player indicating they don't have enough currency.
     *
     * @param currency the type of currency. A value greater than -1 indicates the material type used for currency.
     * @param message The more specific message to append to the generic message of not having enough.
     * @return the formatted insufficient funds message.
     */
    public String getNSFMessage(int currency, String message) {
        return "Sorry, you don't have enough " + (isItemCurrency(currency) ? "items" : "funds") + ". " + message;
    }

    /**
     * Deposits a given amount of currency either into the player's economy account or inventory if the currency
     * represents an item.
     *
     * @param player the player to give currency to.
     * @param amount the amount to give.
     * @param currency the type of currency. A value greater than -1 indicates the material type used for currency.
     */
    public void deposit(Player player, double amount, int currency) {
        if (isUsingVault(currency)) {
            getVaultHandler().getEconomy().depositPlayer(player, amount);
        } else {
            ItemEconomy.deposit(player, amount, currency);
        }
    }

    /**
     * Withdraws a given amount of currency either from the player's economy account or inventory if the currency
     * represents an item.
     *
     * @param player the player to take currency from.
     * @param amount the amount to take.
     * @param currency the type of currency. A value greater than -1 indicates the material type used for currency.
     */
    public void withdraw(Player player, double amount, int currency) {
        if (isUsingVault(currency)) {
            getVaultHandler().getEconomy().withdrawPlayer(player, amount);
        } else {
            ItemEconomy.withdraw(player, amount, currency);
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
     * @deprecated just use the other methods in this class for economy stuff.
     */
    // TODO make private
    @Deprecated
    public VaultHandler getVaultHandler() {
        return vaultHandler;
    }

    /**
     * Determines if the currency type given represents an item currency.
     *
     * @param currency the type of currency. A value greater than -1 indicates the material type used for currency.
     * @return true if currency is greater than -1.
     */
    public static boolean isItemCurrency(int currency) {
        return currency >= 0;
    }

    private static class ItemEconomy {

        private static final String ECONOMY_NAME = "Simple Item Economy";

        private static String getFormattedPrice(double amount, int currency) {
            if (isItemCurrency(currency)) {
                Material m = Material.getMaterial(currency);
                return m != null ? amount + " " + m.toString() : "NO ITEM FOUND";
            } else {
                return "";
            }
        }

        private static String getName() {
            return ECONOMY_NAME;
        }

        private static boolean hasEnough(Player player, double amount, int currency) {
            if (isItemCurrency(currency)) {
                return player.getInventory().contains(currency, (int) amount);
            } else {
                return true;
            }
        }

        private static void deposit(Player player, double amount, int currency) {
            if (isItemCurrency(currency)) {
                giveItem(player, amount, currency);
            }
        }

        private static void withdraw(Player player, double amount, int currency) {
            if (isItemCurrency(currency)) {
                takeItem(player, amount, currency);
            }
        }

        private static void giveItem(Player player, double amount, int type) {
            ItemStack item = new ItemStack(type, (int) amount);
            player.getInventory().addItem(item);
            showReceipt(player, (amount * -1), type);
        }

        private static void takeItem(Player player, double amount, int type) {
            int removed = 0;
            HashMap<Integer, ItemStack> items = (HashMap<Integer, ItemStack>) player.getInventory().all(type);
            for (int i : items.keySet()) {
                if (removed >= amount) {
                    break;
                }
                int diff = (int) (amount - removed);
                int amt = player.getInventory().getItem(i).getAmount();
                if (amt - diff > 0) {
                    player.getInventory().getItem(i).setAmount(amt - diff);
                    break;
                } else {
                    removed += amt;
                    player.getInventory().clear(i);
                }
            }
            showReceipt(player, amount, type);
        }

        private static void showReceipt(Player player, double price, int item) {
            if (price > 0D) {
                player.sendMessage(String.format("%s%s%s %s",
                        ChatColor.WHITE, "You have been charged", ChatColor.GREEN, getFormattedPrice(price, item)));
            } else if (price < 0D) {
                player.sendMessage(String.format("%s%s%s %s",
                        ChatColor.DARK_GREEN, getFormattedPrice((price * -1), item),
                        ChatColor.WHITE, "has been added to your inventory."));
            }
        }
    }
}
