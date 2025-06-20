package org.mvplugins.multiverse.core.destination;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.permissions.CorePermissions;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

/**
 * Provides destinations for teleportation.
 */
@Service
public final class DestinationsProvider {
    private static final String SEPARATOR = ":";

    private final Map<String, Destination<?, ?, ?>> destinationMap;
    private final CorePermissions corePermissions;

    @Inject
    DestinationsProvider(@NotNull CorePermissions corePermissions) {
        this.corePermissions = corePermissions;
        this.destinationMap = new HashMap<>();
    }

    /**
     * Adds a destination to the provider.
     *
     * @param destination The destination.
     */
    public void registerDestination(@NotNull Destination<?, ?, ?> destination) {
        this.destinationMap.put(destination.getIdentifier(), destination);
        this.corePermissions.addDestinationPermissions(destination);
    }

    /**
     * Converts a destination string to a destination object.
     *
     * @param destinationString The destination string.
     * @return The destination object, or null if invalid format.
     */
    public @NotNull Attempt<DestinationInstance<?, ?>, FailureReason> parseDestination(@NotNull String destinationString) {
        return this.parseDestination(Bukkit.getConsoleSender(), destinationString);
    }

    /**
     * Converts a destination string to a destination object with sender context.
     *
     * @param sender            The target sender context.
     * @param destinationString The destination string.
     * @return The destination object, or null if invalid format.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    @SuppressWarnings("unchecked,rawtypes")
    public @NotNull Attempt<DestinationInstance<?, ?>, FailureReason> parseDestination(
            @NotNull CommandSender sender,
            @NotNull String destinationString
    ) {
        String[] items = destinationString.split(SEPARATOR, 2);

        String idString = items[0];
        String destinationParams;
        Destination destination;

        if (items.length < 2) {
            // Assume world destination
            destination = this.getDestinationById("w");
            destinationParams = items[0];
        } else {
            destination = this.getDestinationById(idString);
            destinationParams = items[1];
        }

        if (destination == null) {
            return Attempt.failure(ParseFailureReason.INVALID_DESTINATION_ID,
                    replace("{id}").with(idString),
                    replace("{ids}").with(String.join(", ", this.destinationMap.keySet())));
        }

        return destination.getDestinationInstance(sender, destinationParams);
    }

    /**
     * Gets a destination by its identifier.
     *
     * @param identifier The identifier.
     * @return The destination, or null if not found.
     */
    public @Nullable Destination<?, ?, ?> getDestinationById(@Nullable String identifier) {
        return this.destinationMap.get(identifier);
    }

    /**
     * Gets all registered destinations.
     *
     * @return A collection of destinations.
     */
    public @NotNull Collection<Destination<?, ?, ?>> getDestinations() {
        return this.destinationMap.values();
    }

    /**
     * Gets suggestions for possible parsable destinations.
     *
     * @param sender            The target sender context.
     * @param destinationParams The current user input.
     * @return A collection of destination suggestions.
     */
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(@NotNull CommandSender sender, @Nullable String destinationParams) {
        return this.getDestinations().stream()
                .flatMap(destination -> destination.suggestDestinations(sender, destinationParams).stream())
                .toList();
    }

    /**
     * Gets suggestions for possible parsable destinations.
     *
     * @param sender            The target sender context.
     * @param destinationParams The current user input.
     * @return A collection of destination suggestions in parsable string format.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public @NotNull Collection<String> suggestDestinationStrings(@NotNull CommandSender sender, @Nullable String destinationParams) {
        return suggestDestinations(sender, destinationParams)
                .stream()
                .map(DestinationSuggestionPacket::parsableString)
                .toList();
    }

    public enum ParseFailureReason implements FailureReason {
        INVALID_DESTINATION_ID(MVCorei18n.DESTINATION_PARSE_FAILUREREASON_INVALIDDESTINATIONID),
        ;

        private final MessageKeyProvider messageKey;

        ParseFailureReason(MessageKeyProvider message) {
            this.messageKey = message;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MessageKey getMessageKey() {
            return messageKey.getMessageKey();
        }
    }
}
