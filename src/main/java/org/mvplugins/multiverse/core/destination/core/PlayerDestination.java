package org.mvplugins.multiverse.core.destination.core;

import java.util.Collection;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.destination.DestinationSuggestionPacket;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.utils.PlayerFinder;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.utils.result.FailureReason;
import org.mvplugins.multiverse.core.world.helpers.ConcurrentPlayerWorldTracker;

/**
 * {@link Destination} implementation for players.s
 */
@Service
public final class PlayerDestination implements Destination<PlayerDestination, PlayerDestinationInstance, PlayerDestination.InstanceFailureReason> {

    private final ConcurrentPlayerWorldTracker playerWorldTracker;

    /**
     * Creates a new instance of the PlayerDestination.
     */
    @Inject
    PlayerDestination(@NotNull ConcurrentPlayerWorldTracker playerWorldTracker) {
        this.playerWorldTracker = playerWorldTracker;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "pl";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Attempt<PlayerDestinationInstance, InstanceFailureReason> getDestinationInstance(
            @NotNull CommandSender sender,
            @NotNull String destinationParams
    ) {
        Player player = PlayerFinder.get(destinationParams);
        if (player == null) {
            return Attempt.failure(InstanceFailureReason.PLAYER_NOT_FOUND, Replace.PLAYER.with(destinationParams));
        }
        return Attempt.success(new PlayerDestinationInstance(this, player));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(
            @NotNull CommandSender sender, @Nullable String destinationParams) {
        return playerWorldTracker.getOnlinePlayers().stream()
                .map(player -> new DestinationSuggestionPacket(this, player, player))
                .toList();
    }

    public enum InstanceFailureReason implements FailureReason {

        PLAYER_NOT_FOUND(MVCorei18n.DESTINATION_PLAYER_FAILUREREASON_PLAYERNOTFOUND)
        ;

        private final MessageKeyProvider messageKey;

        InstanceFailureReason(MessageKeyProvider message) {
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
