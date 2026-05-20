package org.mvplugins.multiverse.core.command.flags;

import co.aikar.commands.InvalidCommandArgument;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.command.flag.FlagBuilder;
import org.mvplugins.multiverse.core.destination.DestinationInstance;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.destination.core.WorldDestination;
import org.mvplugins.multiverse.core.exceptions.command.MVInvalidCommandArgument;
import org.mvplugins.multiverse.core.world.WorldManager;

@ApiStatus.AvailableSince("5.7")
@Service
public class RemovePlayerDestinationFlags extends FlagBuilder {

    public static final String NAME = "removeplayer";

    private WorldManager worldManager;
    private DestinationsProvider destinationsProvider;
    private WorldDestination worldDestination;

    protected RemovePlayerDestinationFlags(
            @NotNull String name,
            @NotNull CommandFlagsManager flagsManager,
            @NotNull WorldManager worldManager,
            @NotNull DestinationsProvider destinationsProvider,
            @NotNull WorldDestination worldDestination
    ) {
        super(name, flagsManager);
        this.worldManager = worldManager;
        this.destinationsProvider = destinationsProvider;
        this.worldDestination = worldDestination;
    }

    @Inject
    private RemovePlayerDestinationFlags(
            @NotNull CommandFlagsManager flagsManager,
            @NotNull WorldManager worldManager,
            @NotNull DestinationsProvider destinationsProvider,
            @NotNull WorldDestination worldDestination
    ) {
        super(NAME, flagsManager);
        this.destinationsProvider = destinationsProvider;
        this.worldManager = worldManager;
        this.worldDestination = worldDestination;
    }

    public final CommandValueFlag<DestinationInstance> removePlayers = flag(CommandValueFlag.builder("--remove-players", DestinationInstance.class)
            .addAlias("-r")
            .defaultValue(() -> worldManager.getDefaultWorld()
                    .map(defaultWorld -> worldDestination.fromWorld(defaultWorld))
                    .getOrElseThrow(() -> new InvalidCommandArgument("No default world found, so the --remove-players flag requires a destination argument."))) //TODO: locale
            .completion(input -> destinationsProvider.suggestDestinationStrings(Bukkit.getConsoleSender(), input))
            .context(input -> destinationsProvider.parseDestination(input)
                    .getOrThrow(failure ->
                            MVInvalidCommandArgument.of(failure.getFailureMessage())))
            .optional()
            .build());
}
