package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandContexts;
import com.google.common.base.Strings;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import com.onarandombox.MultiverseCore.utils.PlayerFinder;
import org.bukkit.entity.Player;

public class MVCommandContexts extends PaperCommandContexts {
    private final MultiverseCore plugin;

    public MVCommandContexts(MVCommandManager mvCommandManager, MultiverseCore plugin) {
        super(mvCommandManager);
        this.plugin = plugin;

        registerIssuerOnlyContext(BukkitCommandIssuer.class, BukkitCommandExecutionContext::getIssuer);
        registerContext(MVWorld.class, this::parseMVWorld);
        registerContext(ParsedDestination.class, this::parseDestination);
        registerIssuerAwareContext(Player.class, this::parsePlayer);
    }

    private ParsedDestination<?> parseDestination(BukkitCommandExecutionContext context) {
        String destination = context.popFirstArg();
        if (Strings.isNullOrEmpty(destination)) {
            throw new InvalidCommandArgument("No destination specified.");
        }

        ParsedDestination<?> parsedDestination = plugin.getDestinationsProvider().parseDestination(destination);
        if (parsedDestination == null) {
            throw new InvalidCommandArgument("The destination " + destination + " is not valid.");
        }

        return parsedDestination;
    }

    private MVWorld parseMVWorld(BukkitCommandExecutionContext context) {
        String worldName = context.popFirstArg();
        MVWorld world = plugin.getMVWorldManager().getMVWorld(worldName);
        if (world == null) {
            throw new InvalidCommandArgument("World " + worldName + " is not a multiverse world.");
        }
        return world;
    }

    private Player parsePlayer(BukkitCommandExecutionContext context) {
        String resolve = context.getFlagValue("resolve", "");

        // Get player based on sender only
        if (resolve.equals("issueronly")) {
            if (context.getIssuer().isPlayer()) {
                return context.getIssuer().getPlayer();
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("This command can only be used by a player.");
        }

        String playerIdentifier = context.getFirstArg();
        Player player = PlayerFinder.get(playerIdentifier, context.getSender());

        // Get player based on input, fallback to sender if input is not a player
        if (resolve.equals("issueraware")) {
            if (player != null) {
                context.popFirstArg();
                return player;
            }
            if (context.getIssuer().isPlayer()) {
                return context.getIssuer().getPlayer();
            }
            if (context.isOptional()) {
                return null;
            }
            throw new InvalidCommandArgument("Invalid player: " + playerIdentifier
                    + ". Either specify an online player or use this command as a player.");
        }

        // Get player based on input only
        if (player != null) {
            context.popFirstArg();
            return player;
        }
        if (!context.isOptional()) {
            return null;
        }
        throw new InvalidCommandArgument("Player " + playerIdentifier + " not found.");
    }
}
