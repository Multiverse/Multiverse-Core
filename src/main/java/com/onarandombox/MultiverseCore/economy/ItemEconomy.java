package com.onarandombox.MultiverseCore.economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

// TODO: Make thsi no static
class ItemEconomy {

    private static final String ECONOMY_NAME = "Simple Item Economy";

    static String getFormattedPrice(double amount, Material currency) {
        if (MVEconomist.isItemCurrency(currency)) {
            return amount + " " + currency.toString();
        } else {
            return "";
        }
    }

    static String getName() {
        return ECONOMY_NAME;
    }

    static boolean hasEnough(Player player, double amount, Material currency) {
        if (currency != null) {
            return player.getInventory().contains(currency, (int) amount);
        } else {
            return true;
        }
    }

    static void deposit(Player player, double amount, Material currency) {
        if (MVEconomist.isItemCurrency(currency)) {
            giveItem(player, amount, currency);
        }
    }

    static void withdraw(Player player, double amount, Material currency) {
        if (MVEconomist.isItemCurrency(currency)) {
            takeItem(player, amount, currency);
        }
    }

    static void giveItem(Player player, double amount, Material type) {
        ItemStack item = new ItemStack(type, (int) amount);
        player.getInventory().addItem(item);
        showReceipt(player, (amount * -1), type);
    }

    static void takeItem(Player player, double amount, Material type) {
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

    static void showReceipt(Player player, double price, Material item) {
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
