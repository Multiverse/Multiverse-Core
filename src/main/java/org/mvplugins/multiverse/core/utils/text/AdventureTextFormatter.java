package org.mvplugins.multiverse.core.utils.text;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;

final class AdventureTextFormatter implements TextFormatter {
    @Override
    public void sendFormattedMessage(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    @Override
    public String removeColor(String message) {
        TextComponent sectionComponent = LegacyComponentSerializer.legacySection().deserialize(colorize(message));
        return PlainTextComponentSerializer.plainText().serialize(sectionComponent);
    }

    public String removeAmpColor(String message) {
        return PlainTextComponentSerializer.plainText().serialize(toAmpComponent(message));
    }

    public String removeSectionColor(String message) {
        return PlainTextComponentSerializer.plainText().serialize(toSectionComponent(message));
    }

    @Override
    public String colorize(String message) {
        return LegacyComponentSerializer.legacySection().serialize(toAmpComponent(message));
    }

    private TextComponent toAmpComponent(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    private TextComponent toSectionComponent(String message) {
        return LegacyComponentSerializer.legacySection().deserialize(message);
    }
}
