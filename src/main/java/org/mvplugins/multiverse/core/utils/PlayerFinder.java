package org.mvplugins.multiverse.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.dumptruckman.minecraft.util.Logging;
import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class to get {@link Player} from name, UUID or Selectors.
 */
public final class PlayerFinder {

    private static final Pattern UUID_REGEX = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
    private static final Pattern COMMA_SPLIT = Pattern.compile(",");

    /**
     * Get a {@link Player} based on an identifier of name UUID or selector.
     *
     * @param playerIdentifier  An identifier of name UUID or selector.
     * @return The player if found, else null.
     */
    public static @Nullable Player get(@Nullable String playerIdentifier) {
        return get(playerIdentifier, Bukkit.getConsoleSender());
    }

    /**
     * Get a {@link Player} based on an identifier of name UUID or selector.
     *
     * @param playerIdentifier  An identifier of name UUID or selector.
     * @param sender            Target sender for selector.
     * @return The player if found, else null.
     */
    public static @Nullable Player get(@Nullable String playerIdentifier, @NotNull CommandSender sender) {
        if (playerIdentifier == null) {
            return null;
        }

        Player targetPlayer = getByName(playerIdentifier);
        if (targetPlayer != null) {
            return targetPlayer;
        }

        targetPlayer = getByUuid(playerIdentifier);
        if (targetPlayer != null) {
            return targetPlayer;
        }

        return getBySelector(playerIdentifier, sender);
    }

    /**
     * Get multiple {@link Player} based on many identifiers of name UUID or selector.
     *
     * @param playerIdentifiers An identifier of multiple names, UUIDs or selectors, separated by comma.
     * @return A list of all the {@link Player} found.
     */
    public static @NotNull List<Player> getMulti(@Nullable String playerIdentifiers) {
        return getMulti(playerIdentifiers, Bukkit.getConsoleSender());
    }

    /**
     * Get multiple {@link Player} based on many identifiers of name UUID or selector.
     *
     * @param playerIdentifiers An identifier of multiple names, UUIDs or selectors, separated by comma.
     * @param sender            Target sender for selector.
     * @return A list of all the {@link Player} found.
     */
    public static @NotNull List<Player> getMulti(@Nullable String playerIdentifiers,
                                                 @NotNull CommandSender sender
    ) {
        List<Player> playerResults = new ArrayList<>();

        if (playerIdentifiers == null || Strings.isNullOrEmpty(playerIdentifiers)) {
            return playerResults;
        }

        String[] playerIdentifierArray = COMMA_SPLIT.split(playerIdentifiers);
        for (String playerIdentifier : playerIdentifierArray) {
            Player targetPlayer = getByName(playerIdentifier);
            if (targetPlayer != null) {
                playerResults.add(targetPlayer);
                continue;
            }
            targetPlayer = getByUuid(playerIdentifier);
            if (targetPlayer != null) {
                playerResults.add(targetPlayer);
                continue;
            }
            List<Player> targetPlayers = getMultiBySelector(playerIdentifier, sender);
            if (targetPlayers != null) {
                playerResults.addAll(targetPlayers);
            }
        }
        return playerResults;
    }

    /**
     * Get a {@link Player} based on player name.
     *
     * @param playerName    Name of a {@link Player}.
     * @return The player if found, else null.
     */
    @Nullable
    public static Player getByName(@NotNull String playerName) {
        return Bukkit.getPlayerExact(playerName);
    }

    /**
     * Get a {@link Player} based on player UUID.
     *
     * @param playerUuid    UUID of a player.
     * @return The player if found, else null.
     */
    public static @Nullable Player getByUuid(@NotNull String playerUuid) {
        if (!UUID_REGEX.matcher(playerUuid).matches()) {
            return null;
        }
        UUID uuid;
        try {
            uuid = UUID.fromString(playerUuid);
        } catch (Exception e) {
            return null;
        }
        return getByUuid(uuid);
    }

    /**
     * Get a {@link Player} based on playerUUID.
     *
     * @param playerUuid    UUID of a player.
     * @return The player if found, else null.
     */
    public static @Nullable Player getByUuid(@NotNull UUID playerUuid) {
        return Bukkit.getPlayer(playerUuid);
    }

    /**
     * Get a {@link Player} based on vanilla selectors.
     * https://minecraft.gamepedia.com/Commands#Target_selectors
     *
     * @param playerSelector    A target selector, usually starts with an '@'.
     * @param sender            Target sender for selector.
     * @return The player if only one found, else null.
     */
    public static @Nullable Player getBySelector(@NotNull String playerSelector,
                                                 @NotNull CommandSender sender
    ) {
        List<Player> matchedPlayers = getMultiBySelector(playerSelector, sender);
        if (matchedPlayers == null || matchedPlayers.isEmpty()) {
            Logging.fine("No player found with selector '%s' for %s.", playerSelector, sender.getName());
            return null;
        }
        if (matchedPlayers.size() > 1) {
            Logging.warning("Ambiguous selector result '%s' for %s (more than one player matched) - %s",
                    playerSelector, sender.getName(), matchedPlayers.toString());
            return null;
        }
        return matchedPlayers.get(0);
    }

    /**
     * Get multiple {@link Player} based on selector.
     * https://minecraft.gamepedia.com/Commands#Target_selectors
     *
     * @param playerSelector    A target selector, usually starts with an '@'.
     * @param sender            Target sender for selector.
     * @return A list of all the {@link Player} found.
     */
    public static @Nullable List<Player> getMultiBySelector(@NotNull String playerSelector,
                                                            @NotNull CommandSender sender
    ) {
        if (playerSelector.charAt(0) != '@') {
            return null;
        }
        try {
            return Bukkit.selectEntities(sender, playerSelector).stream()
                    .filter(e -> e instanceof Player)
                    .map(e -> ((Player) e))
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            Logging.warning("An error occurred while parsing selector '%s' for %s. Is it is the correct format?",
                    playerSelector, sender.getName());
            e.printStackTrace();
            return null;
        }
    }
}
