package org.mvplugins.multiverse.core.utils.text;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.utils.ReflectHelper;

/**
 * Utility class to format chat messages. Uses Kyori Adventure if available for better format support such as &amp;#rrggbb
 * hex codes. Falls back to bukkit's ChatColor if Kyori Adventure is not available.
 *
 * @since 5.1
 */
@ApiStatus.AvailableSince("5.1")
public final class ChatTextFormatter {

    private static final TextFormatter wrapper;

    static {
        if (ReflectHelper.hasClass("net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer")) {
            wrapper = new AdventureTextFormatter();
        } else {
            wrapper = new ChatColorTextFormatter();
        }
    }

    /**
     * Sends message with color formatting applied.
     *
     * @param sender   The sender to send the message to
     * @param message  The message to send
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public static void sendFormattedMessage(CommandSender sender, String message) {
        wrapper.sendFormattedMessage(sender, message);
    }

    /**
     * Remove all color formatting from the message.
     *
     * @param message The text to remove color from
     * @return The text with color formatting removed
     *
     * @since 5.1
     */
    public static String removeColor(String message) {
        return wrapper.removeColor(message);
    }

    /**
     * Removes &amp; color formatting from the message.
     *
     * @param message The text to remove color from
     * @return The text with color formatting removed
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public static String removeAmpColor(String message) {
        return wrapper.removeAmpColor(message);
    }

    /**
     * Removes § color formatting from the message.
     *
     * @param message The text to remove color from
     * @return The text with color formatting removed
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public static String removeSectionColor(String message) {
        return wrapper.removeSectionColor(message);
    }

    /**
     * Applies color formatting to the message.
     *
     * @param message The text to apply color to
     * @return The text with color formatting
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public static String colorize(String message) {
        return wrapper.colorize(message);
    }
}
