package org.mvplugins.multiverse.core.destination.core;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import org.bukkit.Bukkit;
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

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

/**
 * {@link Destination} implementation for beds.
 */
@Service
public final class BedDestination implements Destination<BedDestination, BedDestinationInstance, BedDestination.InstanceFailureReason> {
    static final String OWN_BED_STRING = "playerbed";

    BedDestination() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "b";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Attempt<BedDestinationInstance, InstanceFailureReason> getDestinationInstance(@NotNull String destinationParams) {
        Player player = PlayerFinder.get(destinationParams);
        if (player == null && !OWN_BED_STRING.equals(destinationParams)) {
            return Attempt.failure(InstanceFailureReason.PLAYER_NOT_FOUND, Replace.PLAYER.with(destinationParams));
        }
        return Attempt.success(new BedDestinationInstance(this, player));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(
            @NotNull CommandSender sender, @Nullable String destinationParams) {
        List<DestinationSuggestionPacket> collect = Bukkit.getOnlinePlayers().stream()
                .map(player -> new DestinationSuggestionPacket(this, player.getName(), player.getName()))
                .collect(Collectors.toList());
        if (sender instanceof Player) {
            collect.add(new DestinationSuggestionPacket(this, OWN_BED_STRING, OWN_BED_STRING));
        }
        return collect;
    }

    public enum InstanceFailureReason implements FailureReason {
        PLAYER_NOT_FOUND(MVCorei18n.DESTINATION_BED_FAILUREREASON_PLAYERNOTFOUND),
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
