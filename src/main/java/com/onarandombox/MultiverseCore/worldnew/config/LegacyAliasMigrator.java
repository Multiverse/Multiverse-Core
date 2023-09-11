package com.onarandombox.MultiverseCore.worldnew.config;

import com.onarandombox.MultiverseCore.configuration.migration.MigratorAction;
import io.vavr.control.Try;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.atomic.AtomicReference;

class LegacyAliasMigrator implements MigratorAction {
    @Override
    public void migrate(ConfigurationSection config) {
        AtomicReference<String> alias = new AtomicReference<>(config.getString("alias", ""));
        if (alias.get().isEmpty()) return;

        String color = config.getString("color", "");
        String style = config.getString("style", "");

        Try.of(() -> Enum.valueOf(EnglishChatColor.class, color.toUpperCase()))
                .map(c -> c.color)
                .onSuccess(c -> {
                    if (c != ChatColor.WHITE) {
                        alias.set("&" + c.getChar() + alias.get());
                    }
                });

        Try.of(() -> Enum.valueOf(EnglishChatStyle.class, style.toUpperCase()))
                .map(c -> c.color)
                .onSuccess(s -> {
                    if (s != null) {
                        alias.set("&" + s.getChar() + alias.get());
                    }
                });

        config.set("alias", alias.get());
        config.set("color", null);
        config.set("style", null);
    }

    private enum EnglishChatColor {
        // BEGIN CHECKSTYLE-SUPPRESSION: JavadocVariable
        AQUA(ChatColor.AQUA),
        BLACK(ChatColor.BLACK),
        BLUE(ChatColor.BLUE),
        DARKAQUA(ChatColor.DARK_AQUA),
        DARKBLUE(ChatColor.DARK_BLUE),
        DARKGRAY(ChatColor.DARK_GRAY),
        DARKGREEN(ChatColor.DARK_GREEN),
        DARKPURPLE(ChatColor.DARK_PURPLE),
        DARKRED(ChatColor.DARK_RED),
        GOLD(ChatColor.GOLD),
        GRAY(ChatColor.GRAY),
        GREEN(ChatColor.GREEN),
        LIGHTPURPLE(ChatColor.LIGHT_PURPLE),
        RED(ChatColor.RED),
        YELLOW(ChatColor.YELLOW),
        WHITE(ChatColor.WHITE);
        // END CHECKSTYLE-SUPPRESSION: JavadocVariable

        private final ChatColor color;
        //private final String text;

        EnglishChatColor(ChatColor color) {
            this.color = color;
        }
    }

    private enum EnglishChatStyle {
        // BEGIN CHECKSTYLE-SUPPRESSION: JavadocVariable
        /**
         * No style.
         */
        NORMAL(null),
        MAGIC(ChatColor.MAGIC),
        BOLD(ChatColor.BOLD),
        STRIKETHROUGH(ChatColor.STRIKETHROUGH),
        UNDERLINE(ChatColor.UNDERLINE),
        ITALIC(ChatColor.ITALIC);
        // END CHECKSTYLE-SUPPRESSION: JavadocVariable

        private final ChatColor color;

        EnglishChatStyle(ChatColor color) {
            this.color = color;
        }

    }
}
