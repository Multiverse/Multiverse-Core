/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVDestination;
import com.onarandombox.MultiverseCore.api.SafeTTeleporter;
import com.onarandombox.MultiverseCore.api.Teleporter;
import com.onarandombox.MultiverseCore.commandtools.queue.QueuedCommand;
import com.onarandombox.MultiverseCore.destination.CustomTeleporterDestination;
import com.onarandombox.MultiverseCore.destination.InvalidDestination;
import com.onarandombox.MultiverseCore.destination.WorldDestination;
import com.onarandombox.MultiverseCore.enums.TeleportResult;
import com.onarandombox.MultiverseCore.event.MVTeleportEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class TeleportCommand extends MultiverseCoreCommand {

    private final SafeTTeleporter playerTeleporter;

    private static final int UNSAFE_TELEPORT_EXPIRE_DELAY = 300; // In ticks

    public TeleportCommand(MultiverseCore plugin) {
        super(plugin);
        this.playerTeleporter = this.plugin.getSafeTTeleporter();
    }

    @CommandAlias("mv")
    public class Teleport extends BaseCommand {

        @Subcommand("tp|teleport")
        @Syntax("[player] <destination>")
        @CommandCompletion("@players|@MVWorlds:playerOnly|@destinations:playerOnly @players|@MVWorlds|@destinations")
        @Description("Allows you to the teleport to a location on your server!")
        public void doTeleportCommand(@NotNull CommandSender sender,

                                      @Syntax("[player]")
                                      @Description("Target player to teleport.")
                                      @NotNull @Flags("other,defaultself,fallbackself") Player player,

                                      @Syntax("<destination>")
                                      @Description("Location, can be a world name.")
                                      @NotNull @Flags("type=destination") String destinationName) {

            doTeleport(sender, player, destinationName);
        }

        @Override
        public Set<String> getRequiredPermissions() {
            return destPerms();
        }
    }

    public class AliasTeleport extends BaseCommand {

        @CommandAlias("mvtp")
        @Syntax("[player] <destination>")
        @CommandCompletion("@players|@MVWorlds:playerOnly|@destinations:playerOnly @MVWorlds|@destinations")
        @Description("Alias for /mv tp")
        public void doTeleportCommand(@NotNull CommandSender sender,

                                      @Syntax("[player]")
                                      @Description("Target player to teleport.")
                                      @NotNull @Flags("other,defaultself,fallbackself") Player player,

                                      @Syntax("<destination>")
                                      @Description("Location, can be a world name.")
                                      @NotNull @Flags("type=destination") String destinationName) {

            doTeleport(sender, player, destinationName);
        }

        @Override
        public Set<String> getRequiredPermissions() {
            return destPerms();
        }
    }

    private Set<String> destPerms() {
        return this.plugin.getDestFactory().getPermissions();
    }

    private void doTeleport(@NotNull CommandSender teleporter,
                            @NotNull Player teleportee,
                            @NotNull String destinationName) {

        MVDestination destination = this.plugin.getDestFactory().getPlayerAwareDestination(teleportee, destinationName);

        MVTeleportEvent teleportEvent = new MVTeleportEvent(destination, teleportee, teleporter, true);
        this.plugin.getServer().getPluginManager().callEvent(teleportEvent);
        if (teleportEvent.isCancelled()) {
            Logging.fine("Someone else cancelled the MVTeleport Event!!!");
            return;
        }

        if (destination instanceof InvalidDestination) {
            teleporter.sendMessage(String.format("Multiverse does not know how to take you to %s%s", ChatColor.RED, destinationName));
            return;
        }

        if (!this.checkSendPermissions(teleporter, teleportee, destination)) {
            return;
        }

        if (this.plugin.getMVConfig().getEnforceAccess() && !this.plugin.getMVPerms().canEnterDestination(teleportee, destination)) {
            teleporter.sendMessage((teleportee.equals(teleporter))
                    ? "Doesn't look like you're allowed to go " + ChatColor.RED + "there..."
                    : "Doesn't look like you're allowed to send " + ChatColor.GOLD + teleportee.getName() + ChatColor.WHITE + " to " + ChatColor.RED + "there...");
            return;
        }

        if (!this.plugin.getMVPerms().canTravelFromLocation(teleporter, destination.getLocation(teleportee))) {
            teleporter.sendMessage((teleportee.equals(teleporter))
                    ? String.format("Doh! Doesn't look like you can get to %s%s %sfrom where you are...",
                    ChatColor.GREEN, destination.toString(), ChatColor.WHITE)
                    : String.format("Doh! Doesn't look like %s%s %scan get to %sTHERE from where they are...",
                    ChatColor.GREEN, ((Player) teleporter).getWorld().getName(), ChatColor.WHITE, ChatColor.RED));
            return;
        }

        // Special check to verify if players are trying to teleport to the same WORLD Destination
        // as the world they're in, that they ALSO have multiverse.core.spawn.self
        if (destination instanceof WorldDestination
                && teleportee.getWorld().equals(destination.getLocation(teleportee).getWorld())
                && !checkSpawnPermissions(teleporter, teleportee)) {
            return;
        }

        if (destination.getLocation(teleportee) == null) {
            teleporter.sendMessage("Sorry Boss, I tried everything, but just couldn't teleport ya there!");
            return;
        }

        Teleporter teleportObject = (destination instanceof CustomTeleporterDestination)
                ? ((CustomTeleporterDestination) destination).getTeleporter()
                : this.playerTeleporter;

        TeleportResult result = teleportObject.teleport(teleporter, teleportee, destination);
        if (result == TeleportResult.FAIL_UNSAFE) {
            Logging.fine("Could not teleport %s to %s.",
                    teleportee.getName(), this.plugin.getLocationManipulation().strCoordsRaw(destination.getLocation(teleportee)));

            this.plugin.getMVCommandManager().getQueueManager().addToQueue(new QueuedCommand.Builder()
                    .sender(teleporter)
                    .action(unsafeTeleportRunnable(teleporter, teleportee, destination.getLocation(teleportee)))
                    .prompt("The location you are trying to teleport to is deemed unsafe, do you still want to try?")
                    .validDuration(UNSAFE_TELEPORT_EXPIRE_DELAY)
                    .build()
            );
        }

        // else: Player was teleported successfully (or the tp event was fired I should say)
    }

    private boolean checkSendPermissions(@NotNull CommandSender teleporter,
                                         @NotNull Player teleportee,
                                         @NotNull MVDestination destination) {

        if (teleporter.equals(teleportee)) {
            if (!this.plugin.getMVPerms().hasPermission(teleporter, "multiverse.teleport.self." + destination.getIdentifier(), true)) {
                teleporter.sendMessage(String.format("%sYou don't have permission to teleport %syourself %sto a %s%s %sDestination", ChatColor.WHITE, ChatColor.AQUA, ChatColor.WHITE, ChatColor.RED, destination.getType(), ChatColor.WHITE));
                teleporter.sendMessage(String.format("%s   (multiverse.teleport.self.%s)", ChatColor.RED, destination.getIdentifier()));
                return false;
            }
            return true;
        }
        if (!this.plugin.getMVPerms().hasPermission(teleporter, "multiverse.teleport.other." + destination.getIdentifier(), true)) {
            teleporter.sendMessage(String.format("You don't have permission to teleport another player to a %s%s Destination.", ChatColor.GREEN, destination.getType()));
            teleporter.sendMessage(String.format("%s(multiverse.teleport.other.%s)", ChatColor.RED, destination.getIdentifier()));
            return false;
        }
        return true;
    }

    private boolean checkSpawnPermissions(@NotNull CommandSender teleporter,
                                          @NotNull Player teleportee) {

        if (teleporter.equals(teleportee)) {
            if (!this.plugin.getMVPerms().hasPermission(teleporter, "multiverse.core.spawn.self", true)) {
                teleporter.sendMessage(String.format("Sorry you don't have permission to go to the world spawn!"));
                teleporter.sendMessage(String.format("%s  (multiverse.core.spawn.self)", ChatColor.RED));
                return false;
            }
            return true;
        }

        if (!this.plugin.getMVPerms().hasPermission(teleporter, "multiverse.core.spawn.other", true)) {
            teleporter.sendMessage(String.format("Sorry you don't have permission to send %s to the world spawn!", teleportee.getDisplayName()));
            teleporter.sendMessage(String.format("%s  (multiverse.core.spawn.other)", ChatColor.RED));
            return false;
        }
        return true;
    }

    @NotNull
    private Runnable unsafeTeleportRunnable(@NotNull CommandSender teleporter,
                                            @NotNull Player teleportee,
                                            @NotNull Location location) {

        return () -> this.plugin.getSafeTTeleporter().safelyTeleport(teleporter, teleportee, location, false);
    }
}
