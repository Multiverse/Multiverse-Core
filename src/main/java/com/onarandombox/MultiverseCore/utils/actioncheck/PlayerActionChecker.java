package com.onarandombox.MultiverseCore.utils.actioncheck;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.action.ActionResponse;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import com.onarandombox.MultiverseCore.utils.PermissionsTool;
import com.onarandombox.MultiverseCore.world.configuration.EntryFee;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Checks if a player can perform an action.
 */
public class PlayerActionChecker {
    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;
    private final PermissionsTool permissionsTool;

    /**
     * Creates a new PlayerActionChecker.
     *
     * @param plugin The MultiverseCore plugin.
     */
    public PlayerActionChecker(MultiverseCore plugin) {
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();
        this.permissionsTool = plugin.getPermissionsTool();
    }

    /**
     * Checks if a player have permissions to teleport to a destination.
     * <p>
     * {@link ActionCheckResult#NULL_DESTINATION} if the destination is null.
     * {@link ActionCheckResult#NO_DESTINATION_PERMISSION} if the player does not have permission to teleport to the destination.
     * {@link ActionCheckResult#CAN_USE_DESTINATION} if the player can teleport to the destination.
     *
     * @param teleportee    The player to teleport.
     * @param destination   The destination to teleport to.
     * @return The result of the check.
     */
    public ActionCheckResult canUseDestinationToTeleport(@NotNull Player teleportee, @Nullable ParsedDestination<?> destination){
        return canUseDestinationToTeleport(null, teleportee, destination);
    }

    /**
     * Checks if a player have permissions to teleport to a destination by another teleporter
     * <p>
     * {@link ActionCheckResult#NULL_DESTINATION} if the destination is null.
     * {@link ActionCheckResult#NO_DESTINATION_PERMISSION} if the player does not have permission to teleport to the destination.
     * {@link ActionCheckResult#CAN_USE_DESTINATION} if the player can teleport to the destination.
     *
     * @param teleporter    The player who is teleporting the teleportee.
     * @param teleportee    The player to teleport.
     * @param destination   The destination to teleport to.
     * @return The result of the check.
     */
    public ActionCheckResult canUseDestinationToTeleport(@Nullable CommandSender teleporter,
                                                         @NotNull CommandSender teleportee,
                                                         @Nullable ParsedDestination<?> destination
    ) {
        if (destination == null) {
            return ActionCheckResult.NULL_DESTINATION;
        }
        if (!permissionsTool.hasDestinationTeleportPermission(
                teleporter == null ? teleportee : teleporter,
                teleportee,
                destination.getDestination())
        ) {
            return ActionCheckResult.NO_DESTINATION_PERMISSION;
        }
        //TODO Config whether to use finer permission
        return permissionsTool.hasFinerDestinationTeleportPermission(teleporter, teleportee, destination)
                ? ActionCheckResult.CAN_USE_DESTINATION : ActionCheckResult.NO_DESTINATION_PERMISSION;
    }

    /**
     * Checks if a player can teleport to a destination.
     * <p>
     * {@link ActionCheckResult#NULL_DESTINATION} if the destination is null.
     * {@link ActionCheckResult#NULL_LOCATION} if the destination does not have a location.
     * {@link ActionCheckResult#NOT_FROM_MVWORLD} if the destination is not a Multiverse world.
     * {@link ActionCheckResult#SAME_WORLD} if the destination is in the same world as the player.
     * {@link ActionCheckResult#NO_ENFORCE_WORLD_ACCESS} if multiverse enforceaccess is disabled.
     * {@link ActionCheckResult#HAS_WORLD_ACCESS} if the player has access to the world.
     * {@link ActionCheckResult#NO_WORLD_ACCESS} if the player does not have access to the world.
     * {@link ActionCheckResult#EXEMPTED_FROM_ENTRY_FEE} if the player is exempted from entry fees.
     * {@link ActionCheckResult#ENOUGH_MONEY} if the player has to pay an entry fee.
     * {@link ActionCheckResult#NOT_ENOUGH_MONEY} if the player does not have enough money to pay the entry fee.
     * {@link ActionCheckResult#FREE_ENTRY} if the player can enter the world for free.
     * {@link ActionCheckResult#NO_PLAYERLIMIT} if the player limit is not enabled for the world.
     * {@link ActionCheckResult#WITHIN_PLAYERLIMIT} if the player limit is not reached for the world.
     * {@link ActionCheckResult#EXCEED_PLAYERLIMIT} if the player limit is reached for the world.
     * {@link ActionCheckResult#BYPASS_PLAYERLIMIT} if the player can bypass the player limit for the world.
     * {@link ActionCheckResult#NOT_BLACKLISTED} if the player's world is not blacklisted by the target to world.
     * {@link ActionCheckResult#BLACKLISTED} if the player's world is blacklisted by the target to world.
     *
     * @param teleportee    The player to teleport.
     * @param destination   The destination to teleport to.
     * @return One or more of the above action results.
     */
    public ActionResponse canGoToDestination(@NotNull Player teleportee, @Nullable ParsedDestination<?> destination){
        return canGoToDestination(null, teleportee, destination);
    }

    /**
     * Checks if a player can teleport to a destination.
     * <p>
     * {@link ActionCheckResult#NULL_DESTINATION} if the destination is null.
     * {@link ActionCheckResult#NULL_LOCATION} if the destination does not have a location.
     * {@link ActionCheckResult#NOT_FROM_MVWORLD} if the destination is not a Multiverse world.
     * {@link ActionCheckResult#SAME_WORLD} if the destination is in the same world as the player.
     * {@link ActionCheckResult#NO_ENFORCE_WORLD_ACCESS} if multiverse enforceaccess is disabled.
     * {@link ActionCheckResult#HAS_WORLD_ACCESS} if the player has access to the world.
     * {@link ActionCheckResult#NO_WORLD_ACCESS} if the player does not have access to the world.
     * {@link ActionCheckResult#EXEMPTED_FROM_ENTRY_FEE} if the player is exempted from entry fees.
     * {@link ActionCheckResult#ENOUGH_MONEY} if the player has to pay an entry fee.
     * {@link ActionCheckResult#NOT_ENOUGH_MONEY} if the player does not have enough money to pay the entry fee.
     * {@link ActionCheckResult#FREE_ENTRY} if the player can enter the world for free.
     * {@link ActionCheckResult#NO_PLAYERLIMIT} if the player limit is not enabled for the world.
     * {@link ActionCheckResult#WITHIN_PLAYERLIMIT} if the player limit is not reached for the world.
     * {@link ActionCheckResult#EXCEED_PLAYERLIMIT} if the player limit is reached for the world.
     * {@link ActionCheckResult#BYPASS_PLAYERLIMIT} if the player can bypass the player limit for the world.
     * {@link ActionCheckResult#NOT_BLACKLISTED} if the player's world is not blacklisted by the target to world.
     * {@link ActionCheckResult#BLACKLISTED} if the player's world is blacklisted by the target to world.
     *
     * @param teleporter    The player who is teleporting the teleportee.
     * @param teleportee    The player to teleport.
     * @param destination   The destination to teleport to.
     * @return One or more of the above action results.
     */
    public ActionResponse canGoToDestination(@Nullable CommandSender teleporter,
                                             @NotNull Player teleportee,
                                             @Nullable ParsedDestination<?> destination
    ) {
        if (destination == null) {
            return new ActionResponse().addResult(ActionCheckResult.NULL_DESTINATION);
        }
        Location location = destination.getDestinationInstance().getLocation(teleportee);
        if (location == null) {
            return new ActionResponse().addResult(ActionCheckResult.NULL_LOCATION);
        }
        return canGoToLocation(teleporter, teleportee, location);
    }

    /**
     * Checks if a player can teleport to a destination.
     * <p>
     * {@link ActionCheckResult#NULL_LOCATION} if the destination does not have a location.
     * {@link ActionCheckResult#NOT_FROM_MVWORLD} if the destination is not a Multiverse world.
     * {@link ActionCheckResult#SAME_WORLD} if the destination is in the same world as the player.
     * {@link ActionCheckResult#NO_ENFORCE_WORLD_ACCESS} if multiverse enforceaccess is disabled.
     * {@link ActionCheckResult#HAS_WORLD_ACCESS} if the player has access to the world.
     * {@link ActionCheckResult#NO_WORLD_ACCESS} if the player does not have access to the world.
     * {@link ActionCheckResult#EXEMPTED_FROM_ENTRY_FEE} if the player is exempted from entry fees.
     * {@link ActionCheckResult#ENOUGH_MONEY} if the player has to pay an entry fee.
     * {@link ActionCheckResult#NOT_ENOUGH_MONEY} if the player does not have enough money to pay the entry fee.
     * {@link ActionCheckResult#FREE_ENTRY} if the player can enter the world for free.
     * {@link ActionCheckResult#NO_PLAYERLIMIT} if the player limit is not enabled for the world.
     * {@link ActionCheckResult#WITHIN_PLAYERLIMIT} if the player limit is not reached for the world.
     * {@link ActionCheckResult#EXCEED_PLAYERLIMIT} if the player limit is reached for the world.
     * {@link ActionCheckResult#BYPASS_PLAYERLIMIT} if the player can bypass the player limit for the world.
     * {@link ActionCheckResult#NOT_BLACKLISTED} if the player's world is not blacklisted by the target to world.
     * {@link ActionCheckResult#BLACKLISTED} if the player's world is blacklisted by the target to world.
     *
     * @param teleportee    The player to teleport.
     * @param location      The location to teleport to.
     * @return One or more of the above action results.
     */
    public ActionResponse canGoToLocation(@NotNull Player teleportee, @Nullable Location location){
        return canGoToLocation(null, teleportee, location);
    }

    /**
     * Checks if a player can teleport to a location.
     * <p>
     * {@link ActionCheckResult#NULL_LOCATION} if the destination does not have a location.
     * {@link ActionCheckResult#NOT_FROM_MVWORLD} if the destination is not a Multiverse world.
     * {@link ActionCheckResult#SAME_WORLD} if the destination is in the same world as the player.
     * {@link ActionCheckResult#NO_ENFORCE_WORLD_ACCESS} if multiverse enforceaccess is disabled.
     * {@link ActionCheckResult#HAS_WORLD_ACCESS} if the player has access to the world.
     * {@link ActionCheckResult#NO_WORLD_ACCESS} if the player does not have access to the world.
     * {@link ActionCheckResult#EXEMPTED_FROM_ENTRY_FEE} if the player is exempted from entry fees.
     * {@link ActionCheckResult#ENOUGH_MONEY} if the player has to pay an entry fee.
     * {@link ActionCheckResult#NOT_ENOUGH_MONEY} if the player does not have enough money to pay the entry fee.
     * {@link ActionCheckResult#FREE_ENTRY} if the player can enter the world for free.
     * {@link ActionCheckResult#NO_PLAYERLIMIT} if the player limit is not enabled for the world.
     * {@link ActionCheckResult#WITHIN_PLAYERLIMIT} if the player limit is not reached for the world.
     * {@link ActionCheckResult#EXCEED_PLAYERLIMIT} if the player limit is reached for the world.
     * {@link ActionCheckResult#BYPASS_PLAYERLIMIT} if the player can bypass the player limit for the world.
     * {@link ActionCheckResult#NOT_BLACKLISTED} if the player's world is not blacklisted by the target to world.
     * {@link ActionCheckResult#BLACKLISTED} if the player's world is blacklisted by the target to world.
     *
     * @param teleporter    The player who is teleporting the teleportee.
     * @param teleportee    The player to teleport.
     * @param location      The location to teleport to.
     * @return One or more of the above action results.
     */
    public ActionResponse canGoToLocation(@Nullable CommandSender teleporter,
                                          @NotNull Player teleportee,
                                          @Nullable Location location
    ) {
        if (location == null) {
            return new ActionResponse().addResult(ActionCheckResult.NULL_LOCATION);
        }
        MVWorld toWorld = this.worldManager.getMVWorld(location.getWorld());
        return canEnterWorld(teleporter, teleportee, toWorld);
    }

    /**
     * Checks if a player can teleport to a world.
     * <p>
     * {@link ActionCheckResult#NOT_FROM_MVWORLD} if the destination is not a Multiverse world.
     * {@link ActionCheckResult#SAME_WORLD} if the destination is in the same world as the player.
     * {@link ActionCheckResult#NO_ENFORCE_WORLD_ACCESS} if multiverse enforceaccess is disabled.
     * {@link ActionCheckResult#HAS_WORLD_ACCESS} if the player has access to the world.
     * {@link ActionCheckResult#NO_WORLD_ACCESS} if the player does not have access to the world.
     * {@link ActionCheckResult#EXEMPTED_FROM_ENTRY_FEE} if the player is exempted from entry fees.
     * {@link ActionCheckResult#ENOUGH_MONEY} if the player has to pay an entry fee.
     * {@link ActionCheckResult#NOT_ENOUGH_MONEY} if the player does not have enough money to pay the entry fee.
     * {@link ActionCheckResult#FREE_ENTRY} if the player can enter the world for free.
     * {@link ActionCheckResult#NO_PLAYERLIMIT} if the player limit is not enabled for the world.
     * {@link ActionCheckResult#WITHIN_PLAYERLIMIT} if the player limit is not reached for the world.
     * {@link ActionCheckResult#EXCEED_PLAYERLIMIT} if the player limit is reached for the world.
     * {@link ActionCheckResult#BYPASS_PLAYERLIMIT} if the player can bypass the player limit for the world.
     * {@link ActionCheckResult#NOT_BLACKLISTED} if the player's world is not blacklisted by the target to world.
     * {@link ActionCheckResult#BLACKLISTED} if the player's world is blacklisted by the target to world.
     *
     * @param teleportee    The player to teleport.
     * @param toWorld      The world to teleport to.
     * @return One or more of the above action results.
     */
    public ActionResponse canEnterWorld(@NotNull Player teleportee, @NotNull MVWorld toWorld) {
        return canEnterWorld(null, teleportee, toWorld);
    }

    /**
     * Checks if a player can teleport to a world.
     * <p>
     * {@link ActionCheckResult#NOT_FROM_MVWORLD} if the destination is not a Multiverse world.
     * {@link ActionCheckResult#SAME_WORLD} if the destination is in the same world as the player.
     * {@link ActionCheckResult#NO_ENFORCE_WORLD_ACCESS} if multiverse enforceaccess is disabled.
     * {@link ActionCheckResult#HAS_WORLD_ACCESS} if the player has access to the world.
     * {@link ActionCheckResult#NO_WORLD_ACCESS} if the player does not have access to the world.
     * {@link ActionCheckResult#EXEMPTED_FROM_ENTRY_FEE} if the player is exempted from entry fees.
     * {@link ActionCheckResult#ENOUGH_MONEY} if the player has to pay an entry fee.
     * {@link ActionCheckResult#NOT_ENOUGH_MONEY} if the player does not have enough money to pay the entry fee.
     * {@link ActionCheckResult#FREE_ENTRY} if the player can enter the world for free.
     * {@link ActionCheckResult#NO_PLAYERLIMIT} if the player limit is not enabled for the world.
     * {@link ActionCheckResult#WITHIN_PLAYERLIMIT} if the player limit is not reached for the world.
     * {@link ActionCheckResult#EXCEED_PLAYERLIMIT} if the player limit is reached for the world.
     * {@link ActionCheckResult#BYPASS_PLAYERLIMIT} if the player can bypass the player limit for the world.
     * {@link ActionCheckResult#NOT_BLACKLISTED} if the player's world is not blacklisted by the target to world.
     * {@link ActionCheckResult#BLACKLISTED} if the player's world is blacklisted by the target to world.
     *
     * @param teleporter    The player who is teleporting the teleportee.
     * @param teleportee    The player to teleport.
     * @param toWorld      The world to teleport to.
     * @return One or more of the above action results.
     */
    public ActionResponse canEnterWorld(@Nullable CommandSender teleporter,
                                        @NotNull Player teleportee,
                                        @NotNull MVWorld toWorld
    ) {
        MVWorld fromWorld = this.worldManager.getMVWorld(teleportee.getWorld());
        return canGoFromToWorld(teleporter, teleportee, fromWorld, toWorld);
    }

    /**
     * Checks if a player can teleport from a world to another world.
     * <p>
     * {@link ActionCheckResult#NOT_FROM_MVWORLD} if the destination is not a Multiverse world.
     * {@link ActionCheckResult#SAME_WORLD} if the destination is in the same world as the player.
     * {@link ActionCheckResult#NO_ENFORCE_WORLD_ACCESS} if multiverse enforceaccess is disabled.
     * {@link ActionCheckResult#HAS_WORLD_ACCESS} if the player has access to the world.
     * {@link ActionCheckResult#NO_WORLD_ACCESS} if the player does not have access to the world.
     * {@link ActionCheckResult#EXEMPTED_FROM_ENTRY_FEE} if the player is exempted from entry fees.
     * {@link ActionCheckResult#ENOUGH_MONEY} if the player has to pay an entry fee.
     * {@link ActionCheckResult#NOT_ENOUGH_MONEY} if the player does not have enough money to pay the entry fee.
     * {@link ActionCheckResult#FREE_ENTRY} if the player can enter the world for free.
     * {@link ActionCheckResult#NO_PLAYERLIMIT} if the player limit is not enabled for the world.
     * {@link ActionCheckResult#WITHIN_PLAYERLIMIT} if the player limit is not reached for the world.
     * {@link ActionCheckResult#EXCEED_PLAYERLIMIT} if the player limit is reached for the world.
     * {@link ActionCheckResult#BYPASS_PLAYERLIMIT} if the player can bypass the player limit for the world.
     * {@link ActionCheckResult#NOT_BLACKLISTED} if the player's world is not blacklisted by the target to world.
     * {@link ActionCheckResult#BLACKLISTED} if the player's world is blacklisted by the target to world.
     *
     * @param teleportee    The player to teleport.
     * @param toWorld      The world to teleport to.
     * @return One or more of the above action results.
     */
    public ActionResponse canGoFromToWorld(@NotNull CommandSender teleportee, @Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        return canGoFromToWorld(null, teleportee, fromWorld, toWorld);
    }

    /**
     * Checks if a player can teleport from a world to another world.
     * <p>
     * {@link ActionCheckResult#NOT_FROM_MVWORLD} if the destination is not a Multiverse world.
     * {@link ActionCheckResult#SAME_WORLD} if the destination is in the same world as the player.
     * {@link ActionCheckResult#NO_ENFORCE_WORLD_ACCESS} if multiverse enforceaccess is disabled.
     * {@link ActionCheckResult#HAS_WORLD_ACCESS} if the player has access to the world.
     * {@link ActionCheckResult#NO_WORLD_ACCESS} if the player does not have access to the world.
     * {@link ActionCheckResult#EXEMPTED_FROM_ENTRY_FEE} if the player is exempted from entry fees.
     * {@link ActionCheckResult#ENOUGH_MONEY} if the player has to pay an entry fee.
     * {@link ActionCheckResult#NOT_ENOUGH_MONEY} if the player does not have enough money to pay the entry fee.
     * {@link ActionCheckResult#FREE_ENTRY} if the player can enter the world for free.
     * {@link ActionCheckResult#NO_PLAYERLIMIT} if the player limit is not enabled for the world.
     * {@link ActionCheckResult#WITHIN_PLAYERLIMIT} if the player limit is not reached for the world.
     * {@link ActionCheckResult#EXCEED_PLAYERLIMIT} if the player limit is reached for the world.
     * {@link ActionCheckResult#BYPASS_PLAYERLIMIT} if the player can bypass the player limit for the world.
     * {@link ActionCheckResult#NOT_BLACKLISTED} if the player's world is not blacklisted by the target to world.
     * {@link ActionCheckResult#BLACKLISTED} if the player's world is blacklisted by the target to world.
     *
     * @param teleporter    The player who is teleporting the teleportee.
     * @param teleportee    The player to teleport.
     * @param toWorld      The world to teleport to.
     * @return One or more of the above action results.
     */
    public ActionResponse canGoFromToWorld(@Nullable CommandSender teleporter,
                                           @NotNull CommandSender teleportee,
                                           @Nullable MVWorld fromWorld,
                                           @Nullable MVWorld toWorld
    ) {
        ActionResponse response = new ActionResponse();
        if (toWorld == null) {
            return new ActionResponse().addResult(ActionCheckResult.NOT_FROM_MVWORLD);
        }
        if (toWorld.equals(fromWorld)) {
            return response.addResult(ActionCheckResult.SAME_WORLD)
                    .then(() -> isNotBlacklisted(fromWorld, toWorld));
        }

        CommandSender targetSender = (teleporter == null) ? teleportee : teleporter;

        return response.then(() -> hasAccessToWorld(targetSender, toWorld))
                .then(() -> isWithinPlayerLimit(targetSender, toWorld))
                .then(() -> isNotBlacklisted(fromWorld, toWorld))
                .then(() -> hasMoneyToEnterWorld(targetSender, toWorld));
    }

    /**
     * Checks if a player has access to a world.
     * <p>
     * {@link ActionCheckResult#NO_ENFORCE_WORLD_ACCESS} if multiverse enforceaccess is disabled.
     * {@link ActionCheckResult#HAS_WORLD_ACCESS} if the player has access to the world.
     * {@link ActionCheckResult#NO_WORLD_ACCESS} if the player does not have access to the world.
     *
     * @param sender    The sender to check.
     * @param toWorld   The world to teleport to.
     * @return The action check result.
     */
    public ActionCheckResult hasAccessToWorld(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        if (!this.plugin.getMVConfig().getEnforceAccess()) {
            return ActionCheckResult.NO_ENFORCE_WORLD_ACCESS;
        }
        return permissionsTool.hasWorldAccess(sender, toWorld)
                ? ActionCheckResult.HAS_WORLD_ACCESS : ActionCheckResult.NO_WORLD_ACCESS;
    }

    /**
     * Checks if a player has access to a world.
     * <p>
     * {@link ActionCheckResult#EXEMPTED_FROM_ENTRY_FEE} if the player is exempted from entry fees.
     * {@link ActionCheckResult#ENOUGH_MONEY} if the player has to pay an entry fee.
     * {@link ActionCheckResult#NOT_ENOUGH_MONEY} if the player does not have enough money to pay the entry fee.
     * {@link ActionCheckResult#FREE_ENTRY} if the player can enter the world for free.
     *
     * @param sender    The sender to check.
     * @param toWorld   The world to teleport to.
     * @return The action check result.
     */
    public ActionCheckResult hasMoneyToEnterWorld(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender) {
            return ActionCheckResult.EXEMPTED_FROM_ENTRY_FEE;
        }
        if (!(sender instanceof Player)) {
            return ActionCheckResult.CANNOT_PAY_ENTRY_FEE;
        }

        double price = toWorld.getPrice();
        Material currency = toWorld.getCurrency();
        if (price == 0D && (currency == null || currency == EntryFee.DISABLED_MATERIAL)) {
            return ActionCheckResult.FREE_ENTRY;
        }

        Player player = (Player) sender;
        if (permissionsTool.hasBypassEntryFee(player, toWorld)) {
            return ActionCheckResult.EXEMPTED_FROM_ENTRY_FEE;
        }

        return this.plugin.getEconomist().isPlayerWealthyEnough(player, price, currency)
                ? ActionCheckResult.ENOUGH_MONEY : ActionCheckResult.NOT_ENOUGH_MONEY;
    }

    /**
     * Checks if a player is within the player limit of a world.
     * <p>
     * {@link ActionCheckResult#NO_PLAYERLIMIT} if the player limit is not enabled for the world.
     * {@link ActionCheckResult#WITHIN_PLAYERLIMIT} if the player limit is not reached for the world.
     * {@link ActionCheckResult#EXCEED_PLAYERLIMIT} if the player limit is reached for the world.
     *
     * @param sender    The sender to check.
     * @param toWorld   The world to teleport to.
     * @return The action check result.
     */
    public ActionCheckResult isWithinPlayerLimit(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        if (toWorld.getPlayerLimit() <= -1) {
            return ActionCheckResult.NO_PLAYERLIMIT;
        }
        if (toWorld.getPlayerLimit() > toWorld.getCBWorld().getPlayers().size()) {
            return ActionCheckResult.WITHIN_PLAYERLIMIT;
        }
        return permissionsTool.hasBypassPlayerLimit(sender, toWorld)
                ? ActionCheckResult.BYPASS_PLAYERLIMIT : ActionCheckResult.EXCEED_PLAYERLIMIT;
    }

    /**
     * Checks if a player is not blacklisted from a world.
     * <p>
     * {@link ActionCheckResult#NOT_BLACKLISTED} if the player's world is not blacklisted by the target to world.
     * {@link ActionCheckResult#BLACKLISTED} if the player's world is blacklisted by the target to world.
     *
     * @param teleportee    The player to check.
     * @param toWorld       The world to teleport to.
     * @return The action check result.
     */
    public ActionCheckResult isNotBlacklisted(@NotNull Player teleportee, @NotNull MVWorld toWorld) {
        MVWorld fromWorld = this.worldManager.getMVWorld(teleportee.getWorld());
        return isNotBlacklisted(fromWorld, toWorld);
    }

    /**
     * Checks if a world is not blacklisted by another world.
     * <p>
     * {@link ActionCheckResult#NOT_BLACKLISTED} if the player's world is not blacklisted by the target to world.
     * {@link ActionCheckResult#BLACKLISTED} if the player's world is blacklisted by the target to world.
     *
     * @param fromWorld The from world that is checked for blacklisting.
     * @param toWorld   The to world that has the blacklist.
     * @return The action check result.
     */
    public ActionCheckResult isNotBlacklisted(@Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        if (fromWorld == null) {
            // No blacklisting if the player is not in a MV world
            return ActionCheckResult.NOT_BLACKLISTED;
        }
        return toWorld.getWorldBlacklist().contains(fromWorld.getName())
                ? ActionCheckResult.BLACKLISTED : ActionCheckResult.NOT_BLACKLISTED;
    }

    /**
     * Checks if a player can keep their game mode on world change.
     * <p>
     * {@link ActionCheckResult#KEEP_GAME_MODE} if the player is exempted from game mode enforcement.
     * {@link ActionCheckResult#ENFORCE_GAME_MODE} if the player is not exempted from game mode enforcement.
     *
     * @param player    The sender to check.
     * @param toWorld   The world to teleport to.
     * @return The action check result.
     */
    public ActionCheckResult canKeepGameMode(@NotNull Player player, @NotNull MVWorld toWorld) {
        //TODO: Add config option disable game mode enforcement
        return permissionsTool.hasBypassGameModeEnforcement(player, toWorld)
                ? ActionCheckResult.KEEP_GAME_MODE : ActionCheckResult.ENFORCE_GAME_MODE;
    }
}
