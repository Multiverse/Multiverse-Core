package org.mvplugins.multiverse.core.destination.core;

import java.util.Collection;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.destination.DestinationSuggestionPacket;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;
import org.mvplugins.multiverse.core.utils.REPatterns;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.utils.result.FailureReason;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;
import org.mvplugins.multiverse.core.world.location.UnloadedWorldLocation;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.*;

/**
 * {@link Destination} implementation for exact locations.
 */
@Service
public final class ExactDestination implements Destination<ExactDestination, ExactDestinationInstance, ExactDestination.InstanceFailureReason> {

    private final CoreConfig config;
    private final WorldManager worldManager;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;

    @Inject
    public ExactDestination(CoreConfig config, WorldManager worldManager, WorldEntryCheckerProvider worldEntryCheckerProvider) {
        this.config = config;
        this.worldManager = worldManager;
        this.worldEntryCheckerProvider = worldEntryCheckerProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "e";
    }

    /**
     * Make a new {@link ExactDestinationInstance} from a {@link Location}.
     *
     * @param location  The target location
     * @return A new {@link ExactDestinationInstance}
     */
    public @NotNull ExactDestinationInstance fromLocation(@NotNull Location location) {
        return new ExactDestinationInstance(this, new UnloadedWorldLocation(location));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Attempt<ExactDestinationInstance, InstanceFailureReason> getDestinationInstance(@NotNull String destinationParams) {
        String[] items = REPatterns.COLON.split(destinationParams);
        if (items.length < 2) {
            return Attempt.failure(InstanceFailureReason.INVALID_FORMAT);
        }

        String worldName = items[0];
        String coordinates = items[1];
        String[] coordinatesParams = REPatterns.COMMA.split(coordinates);
        if (coordinatesParams.length != 3) {
            return Attempt.failure(InstanceFailureReason.INVALID_COORDINATES_FORMAT);
        }

        World world = getLoadedMultiverseWorld(worldName).flatMap(LoadedMultiverseWorld::getBukkitWorld).getOrNull();
        if (world == null) {
            return Attempt.failure(InstanceFailureReason.WORLD_NOT_FOUND, Replace.WORLD.with(worldName));
        }

        UnloadedWorldLocation location;
        try {
            location = new UnloadedWorldLocation(
                    world,
                    Double.parseDouble(coordinatesParams[0]),
                    Double.parseDouble(coordinatesParams[1]),
                    Double.parseDouble(coordinatesParams[2])
            );
        } catch (NumberFormatException e) {
            return Attempt.failure(InstanceFailureReason.INVALID_NUMBER_FORMAT, Replace.ERROR.with(e));
        }

        if (items.length == 4) {
            String pitch = items[2];
            String yaw = items[3];
            try {
                location.setPitch(Float.parseFloat(pitch));
                location.setYaw(Float.parseFloat(yaw));
            } catch (NumberFormatException e) {
                return Attempt.failure(InstanceFailureReason.INVALID_NUMBER_FORMAT, Replace.ERROR.with(e));
            }
        }

        return Attempt.success(new ExactDestinationInstance(this, location));
    }

    //TODO: Extract to a world finder class
    private Option<LoadedMultiverseWorld> getLoadedMultiverseWorld(String worldName) {
        return config.getResolveAliasName()
                ? worldManager.getLoadedWorldByNameOrAlias(worldName)
                : worldManager.getLoadedWorld(worldName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Collection<DestinationSuggestionPacket> suggestDestinations(
            @NotNull CommandSender sender, @Nullable String destinationParams) {
        return worldManager.getLoadedWorlds().stream()
                .filter(world -> worldEntryCheckerProvider.forSender(sender)
                        .canAccessWorld(world)
                        .isSuccess())
                .map(world ->
                        new DestinationSuggestionPacket(this, world.getTabCompleteName() + ":", world.getName()))
                .toList();
    }

    public enum InstanceFailureReason implements FailureReason {
        INVALID_FORMAT(MVCorei18n.DESTINATION_EXACT_FAILUREREASON_INVALIDFORMAT),
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
