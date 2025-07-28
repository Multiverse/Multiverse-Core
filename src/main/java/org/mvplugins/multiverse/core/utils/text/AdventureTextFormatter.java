package org.mvplugins.multiverse.core.utils.text;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class AdventureTextFormatter implements TextFormatter {
    @Override
    public void sendFormattedMessage(@NotNull CommandSender sender, @Nullable String message) {
        String colorizedMessage = colorize(message);
        if (colorizedMessage == null) {
            return; // Avoid sending null messages
        }
        sender.sendMessage(colorizedMessage);
    }

    @Override
    public @Nullable String removeColor(@Nullable String message) {
        TextComponent sectionComponent = LegacyComponentSerializer.legacySection().deserializeOrNull(colorize(message));
        return PlainTextComponentSerializer.plainText().serializeOrNull(sectionComponent);
    }

    @Override
    public @Nullable String removeAmpColor(@Nullable String message) {
        return PlainTextComponentSerializer.plainText().serializeOrNull(toAmpComponent(message));
    }

    @Override
    public @Nullable String removeSectionColor(@Nullable String message) {
        return PlainTextComponentSerializer.plainText().serializeOrNull(toSectionComponent(message));
    }

    @Override
    public @Nullable String colorize(@Nullable String message) {
        return LegacyComponentSerializer.legacySection().serializeOrNull(toAmpComponent(message));
    }

    private @Nullable TextComponent toAmpComponent(@Nullable String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserializeOrNull(message);
    }

    private @Nullable TextComponent toSectionComponent(@Nullable String message) {
        return LegacyComponentSerializer.legacySection().deserializeOrNull(message);
    }
}
