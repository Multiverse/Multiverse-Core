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

import org.mvplugins.multiverse.core.anchor.AnchorManager;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.destination.DestinationSuggestionPacket;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.utils.result.FailureReason;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

/**
 * {@link Destination} implementation for anchors.
 */
@Service
public final class AnchorDestination implements Destination<AnchorDestination, AnchorDestinationInstance, AnchorDestination.InstanceFailureReason> {

    private final AnchorManager anchorManager;

    @Inject
    AnchorDestination(AnchorManager anchorManager) {
        this.anchorManager = anchorManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "a";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Attempt<AnchorDestinationInstance, InstanceFailureReason> getDestinationInstance(@NotNull String destinationParams) {
        return this.anchorManager.getAnchor(destinationParams)
                .fold(
                        () -> Attempt.failure(InstanceFailureReason.ANCHOR_NOT_FOUND, replace("{anchor}").with(destinationParams)),
                        anchor -> Attempt.success(
                                new AnchorDestinationInstance(this, destinationParams, anchor.getLocation()))
                );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(
            @NotNull CommandSender sender, @Nullable String destinationParams) {
        return this.anchorManager.getAnchors(sender instanceof Player ? (Player)sender : null)
                .stream()
                .map(anchor -> new DestinationSuggestionPacket(this, anchor.getName(), anchor.getName()))
                .toList();
    }

    public enum InstanceFailureReason implements FailureReason {
        ANCHOR_NOT_FOUND(MVCorei18n.DESTINATION_ANCHOR_FAILUREREASON_ANCHORNOTFOUND),
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
