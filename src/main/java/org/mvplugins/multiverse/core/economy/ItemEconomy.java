package org.mvplugins.multiverse.core.economy;

import java.util.HashMap;

import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandManager;

@Service
final class ItemEconomy {

    private static final String ECONOMY_NAME = "Simple Item Economy";
    @NotNull
    private final MVCommandManager commandManager;

    @Inject
    private ItemEconomy(@NotNull MVCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    String getFormattedPrice(double amount, Material currency) {
        if (MVEconomist.isItemCurrency(currency)) {
            return amount + " " + currency.toString();
        } else {
            return "";
        }
    }

    String getName() {
        return ECONOMY_NAME;
    }

    boolean hasEnough(Player player, double amount, Material currency) {
        if (currency != null) {
            return player.getInventory().contains(currency, augmentAmount(amount));
        } else {
            return true;
        }
    }

    void deposit(Player player, double amount, Material currency) {
        if (MVEconomist.isItemCurrency(currency)) {
            giveItem(player, augmentAmount(amount), currency);
        }
    }

    void withdraw(Player player, double amount, Material currency) {
        if (MVEconomist.isItemCurrency(currency)) {
            takeItem(player, augmentAmount(amount), currency);
        }
    }

    void giveItem(Player player, int amount, Material type) {
        ItemStack item = new ItemStack(type, amount);
        player.getInventory().addItem(item);
        showReceipt(player, amount, type);
    }

    void takeItem(Player player, int amount, Material type) {
        int removed = 0;
        HashMap<Integer, ItemStack> items = (HashMap<Integer, ItemStack>) player.getInventory().all(type);
        for (int i : items.keySet()) {
            if (removed >= amount) {
                break;
            }
            int diff = amount - removed;
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

    void showReceipt(Player player, int price, Material item) {
        if (price > 0D) {
            player.sendMessage(String.format("%s%s%s %s",
                    ChatColor.WHITE, "You have been charged", ChatColor.GREEN, getFormattedPrice(price, item)));
        } else if (price < 0D) {
            player.sendMessage(String.format("%s%s%s %s",
                    ChatColor.DARK_GREEN, getFormattedPrice(price, item),
                    ChatColor.WHITE, "has been added to your inventory."));
        }
    }

    private int augmentAmount(@NotNull Double amount) {
        return amount.intValue();
    }
}
