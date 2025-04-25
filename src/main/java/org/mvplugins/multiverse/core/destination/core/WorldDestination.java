package org.mvplugins.multiverse.core.destination.core;

import java.util.Collection;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.config.CoreConfig;
import org.mvplugins.multiverse.core.destination.Destination;
import org.mvplugins.multiverse.core.destination.DestinationSuggestionPacket;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.teleportation.LocationManipulation;
import org.mvplugins.multiverse.core.utils.REPatterns;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.utils.result.FailureReason;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entrycheck.WorldEntryCheckerProvider;

/**
 * {@link Destination} implementation for exact locations.
 */
@Service
public final class WorldDestination implements Destination<WorldDestination, WorldDestinationInstance, WorldDestination.InstanceFailureReason> {

    private final CoreConfig config;
    private final WorldManager worldManager;
    private final LocationManipulation locationManipulation;
    private final WorldEntryCheckerProvider worldEntryCheckerProvider;

    @Inject
    WorldDestination(
            @NotNull CoreConfig config,
            @NotNull WorldManager worldManager,
            @NotNull LocationManipulation locationManipulation,
            @NotNull WorldEntryCheckerProvider worldEntryCheckerProvider) {
        this.config = config;
        this.worldManager = worldManager;
        this.locationManipulation = locationManipulation;
        this.worldEntryCheckerProvider = worldEntryCheckerProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull String getIdentifier() {
        return "w";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Attempt<WorldDestinationInstance, InstanceFailureReason> getDestinationInstance(@NotNull String destinationParams) {
        String[] items = REPatterns.COLON.split(destinationParams, 3);
        String worldName = items[0];
        MultiverseWorld world = getMultiverseWorld(worldName);
        if (world == null) {
            return Attempt.failure(InstanceFailureReason.WORLD_NOT_FOUND, Replace.WORLD.with(worldName));
        }

        String direction = (items.length == 2) ? items[1] : null;
        float yaw = direction != null ? this.locationManipulation.getYaw(direction) : -1;

        return Attempt.success(new WorldDestinationInstance(this, world, direction, yaw));
    }

    //TODO: Extract to a world finder class
    @Nullable
    private MultiverseWorld getMultiverseWorld(String worldName) {
        return config.getResolveAliasName()
                ? worldManager.getWorldByNameOrAlias(worldName).getOrNull()
                : worldManager.getWorld(worldName).getOrNull();
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
                .map(world -> new DestinationSuggestionPacket(this, world.getTabCompleteName(), world.getName()))
                .toList();
    }

    public enum InstanceFailureReason implements FailureReason {
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
