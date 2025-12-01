package org.mvplugins.multiverse.core.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.dumptruckman.minecraft.util.Logging;
import com.google.common.base.Strings;
import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class to get {@link Player} from name, UUID or Selectors.
 */
public final class PlayerFinder {

    private static final List<String> VANILLA_SELECTORS = List.of("@a", "@e", "@r", "@p", "@s");

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
        return tryGetMulti(playerIdentifiers, sender)
                .getOrElse(Collections.emptyList());
    }

    /**
     * Get multiple {@link Player} based on many identifiers of name UUID or selector.
     *
     * @param playerIdentifiers An identifier of multiple names, UUIDs or selectors, separated by comma.
     * @param sender            Target sender for selector.
     * @return A list of all the {@link Player} found.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    public static @NotNull Try<@NotNull List<Player>> tryGetMulti(@Nullable String playerIdentifiers,
                                                                   @NotNull CommandSender sender
    ) {
        if (playerIdentifiers == null || Strings.isNullOrEmpty(playerIdentifiers)) {
            return Try.success(Collections.emptyList());
        }

        // TODO: Currently just assume entire string is a selector. Add support for comma seperated mixture of names, uuids and selectors
        if (isSelector(playerIdentifiers)) {
            return tryGetMultiBySelector(playerIdentifiers, sender);
        }

        List<Player> playerResults = new ArrayList<>();
        String[] playerIdentifierArray = REPatterns.COMMA.split(playerIdentifiers);
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
            Try<@NotNull List<Player>> selectorParseResult = tryGetMultiBySelector(playerIdentifier, sender);
            if  (selectorParseResult.isFailure()) {
                return Try.failure(selectorParseResult.getCause());
            }
            playerResults.addAll(selectorParseResult.getOrElse(Collections.emptyList()));
        }
        return Try.success(playerResults);
    }

    /**
     * Check if the player identifier is a selector.
     *
     * @param playerIdentifier  An identifier of name, UUID or selector.
     * @return True if the identifier is a selector, else false.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    public static boolean isSelector(@NotNull String playerIdentifier) {
        return VANILLA_SELECTORS.stream().anyMatch(playerIdentifier::startsWith);
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
        if (!REPatterns.UUID.matcher(playerUuid).matches()) {
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
     * Get a {@link Player} based on <a href="https://minecraft.gamepedia.com/Commands#Target_selectors">vanilla selectors</a>.
     *
     * @param playerSelector    A target selector, usually starts with an '@'.
     * @param sender            Target sender for selector.
     * @return The player if only one found, else null.
     */
    public static @Nullable Player getBySelector(@NotNull String playerSelector,
                                                 @NotNull CommandSender sender
    ) {
        List<Player> matchedPlayers = getMultiBySelector(playerSelector, sender);
        if (matchedPlayers.isEmpty()) {
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
     * Get multiple {@link Player} based on <a href="https://minecraft.gamepedia.com/Commands#Target_selectors">vanilla selectors</a>.
     *
     * @param playerSelector    A target selector, usually starts with an '@'.
     * @param sender            Target sender for selector.
     * @return A list of all the {@link Player} found.
     */
    public static @NotNull List<Player> getMultiBySelector(@NotNull String playerSelector,
                                                            @NotNull CommandSender sender
    ) {
        return tryGetMultiBySelector(playerSelector, sender)
                .onFailure(throwable -> Logging.warning(
                        "Error selecting entities with selector '%s' for %s: %s",
                        playerSelector, sender.getName(), throwable.getMessage()
                ))
                .getOrElse(Collections::emptyList);
    }

    /**
     * Get multiple {@link Player} based on <a href="https://minecraft.gamepedia.com/Commands#Target_selectors">vanilla selectors</a>.
     *
     * @param playerSelector    A target selector, usually starts with an '@'.
     * @param sender            Target sender for selector.
     * @return A list of all the {@link Player} found.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    public static @NotNull Try<@NotNull List<Player>> tryGetMultiBySelector(@NotNull String playerSelector,
                                                                            @NotNull CommandSender sender
    ) {
        if (playerSelector.charAt(0) != '@') {
            return Try.success(Collections.emptyList());
        }
        return Try.of(() -> Bukkit.selectEntities(sender, playerSelector).stream()
                .filter(e -> e instanceof Player)
                .map(e -> ((Player) e))
                .collect(Collectors.toList()));
    }
}
