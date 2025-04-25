package org.mvplugins.multiverse.core.destination.core;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.destination.DestinationSuggestionPacket;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.utils.REPatterns;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.utils.result.FailureReason;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.location.UnloadedWorldLocation;

import java.util.Collection;
import java.util.List;

/**
 * {@link Destination} implementation for cannons.
 */
@Service
public final class CannonDestination implements Destination<CannonDestination, CannonDestinationInstance, CannonDestination.InstanceFailureReason> {

    private final WorldManager worldManager;

    @Inject
    CannonDestination(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "ca";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Attempt<CannonDestinationInstance, InstanceFailureReason> getDestinationInstance(@NotNull String destinationParams) {
        String[] params = REPatterns.COLON.split(destinationParams);
        if (params.length != 5) {
            return Attempt.failure(InstanceFailureReason.INVALID_FORMAT);
        }

        String worldName = params[0];
        String coordinates = params[1];
        String pitch = params[2];
        String yaw = params[3];
        String speed = params[4];

        String[] coordinatesParams = REPatterns.COMMA.split(coordinates);
        if (coordinatesParams.length != 3) {
            return Attempt.failure(InstanceFailureReason.INVALID_COORDINATES_FORMAT);
        }

        //TODO: Add support for alias names
        World world = this.worldManager.getLoadedWorld(worldName).map(LoadedMultiverseWorld::getBukkitWorld).getOrNull().getOrNull();
        if (world == null) {
            return Attempt.failure(InstanceFailureReason.WORLD_NOT_FOUND, Replace.WORLD.with(worldName));
        }

        UnloadedWorldLocation location;
        double dSpeed;
        try {
            location = new UnloadedWorldLocation(
                    world,
                    Double.parseDouble(coordinatesParams[0]),
                    Double.parseDouble(coordinatesParams[1]),
                    Double.parseDouble(coordinatesParams[2]),
                    Float.parseFloat(yaw),
                    Float.parseFloat(pitch)
            );
            dSpeed = Double.parseDouble(speed);
        } catch (NumberFormatException e) {
            return Attempt.failure(InstanceFailureReason.INVALID_NUMBER_FORMAT, Replace.ERROR.with(e));
        }

        return Attempt.success(new CannonDestinationInstance(this, location, dSpeed));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(
            @NotNull CommandSender sender, @Nullable String destinationParams) {
        return List.of();
    }

    public enum InstanceFailureReason implements FailureReason {
        INVALID_FORMAT(MVCorei18n.DESTINATION_CANNON_FAILUREREASON_INVALIDFORMAT),
        INVALID_COORDINATES_FORMAT(MVCorei18n.DESTINATION_SHARED_FAILUREREASON_INVALIDCOORDINATESFORMAT),
        INVALID_NUMBER_FORMAT(MVCorei18n.DESTINATION_SHARED_FAILUREREASON_INVALIDNUMBERFORMAT),
        WORLD_NOT_FOUND(MVCorei18n.DESTINATION_SHARED_FAILUREREASON_WORLDNOTFOUND),
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
