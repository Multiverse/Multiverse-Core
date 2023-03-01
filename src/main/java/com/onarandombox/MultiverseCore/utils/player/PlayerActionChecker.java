package com.onarandombox.MultiverseCore.utils.player;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.operation.OperationResultChain;
import com.onarandombox.MultiverseCore.destination.ParsedDestination;
import com.onarandombox.MultiverseCore.utils.PermissionsTool;
import com.onarandombox.MultiverseCore.utils.player.checkresult.BlacklistResult;
import com.onarandombox.MultiverseCore.utils.player.checkresult.EntryFeeResult;
import com.onarandombox.MultiverseCore.utils.player.checkresult.GameModeResult;
import com.onarandombox.MultiverseCore.utils.player.checkresult.NullPlaceResult;
import com.onarandombox.MultiverseCore.utils.player.checkresult.PlayerLimitResult;
import com.onarandombox.MultiverseCore.utils.player.checkresult.UseDestinationResult;
import com.onarandombox.MultiverseCore.utils.player.checkresult.WorldAccessResult;
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
    public PlayerActionChecker(@NotNull MultiverseCore plugin) {
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();
        this.permissionsTool = plugin.getPermissionsTool();
    }

    /**
     * Checks if a player have permissions to teleport to a destination by another teleporter.
     *
     * @param teleporter    The player who is teleporting the teleportee.
     * @param teleportee    The player to teleport.
     * @param destination   The destination to teleport to.
     * @return The result of the check.
     */
    public UseDestinationResult canUseDestinationToTeleport(@NotNull CommandSender teleporter,
                                                            @NotNull CommandSender teleportee,
                                                            @NotNull ParsedDestination<?> destination
    ) {
        if (!permissionsTool.hasDestinationTeleportPermission(teleporter, teleportee, destination.getDestination())) {
            return UseDestinationResult.NO_DESTINATION_PERMISSION;
        }
        //TODO Config whether to use finer permission
        return permissionsTool.hasFinerDestinationTeleportPermission(teleporter, teleportee, destination)
                ? UseDestinationResult.CAN_USE_DESTINATION : UseDestinationResult.NO_DESTINATION_PERMISSION;
    }

    /**
     * Checks if a player can teleport to a destination.
     *
     * @param teleporter    The player who is teleporting the teleportee.
     * @param teleportee    The player to teleport.
     * @param destination   The destination to teleport to.
     * @return One or more of the above action results.
     */
    public OperationResultChain canGoToDestination(@Nullable CommandSender teleporter,
                                                   @NotNull Player teleportee,
                                                   @Nullable ParsedDestination<?> destination
    ) {
        if (destination == null) {
            return OperationResultChain.of(NullPlaceResult.NULL_DESTINATION);
        }
        Location location = destination.getDestinationInstance().getLocation(teleportee);
        return canGoToLocation(teleporter, teleportee, location);
    }

    /**
     * Checks if a player can teleport to a location.
     *
     * @param teleporter    The player who is teleporting the teleportee.
     * @param teleportee    The player to teleport.
     * @param location      The location to teleport to.
     * @return One or more of the above action results.
     */
    public OperationResultChain canGoToLocation(@Nullable CommandSender teleporter,
                                                @NotNull Player teleportee,
                                                @Nullable Location location
    ) {
        if (location == null) {
            return OperationResultChain.of(NullPlaceResult.NULL_LOCATION);
        }
        if (location.getWorld() == null) {
            return OperationResultChain.of(NullPlaceResult.NULL_WORLD);
        }
        MVWorld toWorld = this.worldManager.getMVWorld(location.getWorld());
        return canGoToWorld(teleporter, teleportee, toWorld);
    }

    /**
     * Checks if a player can teleport to a world.
     *
     * @param teleporter    The player who is teleporting the teleportee.
     * @param teleportee    The player to teleport.
     * @param toWorld      The world to teleport to.
     * @return One or more of the above action results.
     */
    public OperationResultChain canGoToWorld(@Nullable CommandSender teleporter,
                                             @NotNull Player teleportee,
                                             @NotNull MVWorld toWorld
    ) {
        MVWorld fromWorld = this.worldManager.getMVWorld(teleportee.getWorld());
        return canGoFromWorldToWorld(teleporter, teleportee, fromWorld, toWorld);
    }

    /**
     * Checks if a player can teleport from a world to another world.
     *
     * @param teleporter    The player who is teleporting the teleportee.
     * @param teleportee    The player to teleport.
     * @param toWorld      The world to teleport to.
     * @return One or more of the above action results.
     */
    public OperationResultChain canGoFromWorldToWorld(@Nullable CommandSender teleporter,
                                                      @NotNull CommandSender teleportee,
                                                      @Nullable MVWorld fromWorld,
                                                      @Nullable MVWorld toWorld
    ) {
        if (toWorld == null) {
            return OperationResultChain.of(NullPlaceResult.NOT_MV_WORLD);
        }

        CommandSender targetSender = (teleporter == null) ? teleportee : teleporter;

        return OperationResultChain.create(true)
                .then(() -> hasAccessToWorld(targetSender, toWorld))
                .then(() -> isWithinPlayerLimit(targetSender, toWorld))
                .then(() -> isNotBlacklisted(fromWorld, toWorld))
                .then(() -> hasMoneyToEnterWorld(targetSender, fromWorld, toWorld));
    }

    /**
     * Checks if a player has access to a world.
     *
     * @param sender    The sender to check.
     * @param toWorld   The world to teleport to.
     * @return The action check result.
     */
    public WorldAccessResult hasAccessToWorld(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        if (!this.plugin.getMVConfig().getEnforceAccess()) {
            return WorldAccessResult.NO_ENFORCE_WORLD_ACCESS;
        }
        return permissionsTool.hasWorldAccess(sender, toWorld)
                ? WorldAccessResult.HAS_WORLD_ACCESS : WorldAccessResult.NO_WORLD_ACCESS;
    }

    /**
     * Checks if a player has access to a world.
     *
     * @param sender    The sender to check.
     * @param toWorld   The world to teleport to.
     * @return The action check result.
     */
    public EntryFeeResult hasMoneyToEnterWorld(@NotNull CommandSender sender, @Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        if (toWorld.equals(fromWorld)) {
            return EntryFeeResult.SAME_WORLD;
        }
        if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender) {
            return EntryFeeResult.EXEMPT_FROM_ENTRY_FEE;
        }
        if (!(sender instanceof Player)) {
            return EntryFeeResult.CANNOT_PAY_ENTRY_FEE;
        }
        double price = toWorld.getPrice();
        Material currency = toWorld.getCurrency();
        if (price == 0D && (currency == null || currency == EntryFee.DISABLED_MATERIAL)) {
            return EntryFeeResult.FREE_ENTRY;
        }
        Player player = (Player) sender;
        if (permissionsTool.hasBypassEntryFee(player, toWorld)) {
            return EntryFeeResult.EXEMPT_FROM_ENTRY_FEE;
        }
        return this.plugin.getEconomist().isPlayerWealthyEnough(player, price, currency)
                ? EntryFeeResult.ENOUGH_MONEY : EntryFeeResult.NOT_ENOUGH_MONEY;
    }

    /**
     * Checks if a player is within the player limit of a world.
     *
     * @param sender    The sender to check.
     * @param toWorld   The world to teleport to.
     * @return The action check result.
     */
    public PlayerLimitResult isWithinPlayerLimit(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        if (toWorld.getPlayerLimit() <= -1) {
            return PlayerLimitResult.NO_PLAYERLIMIT;
        }
        if (toWorld.getPlayerLimit() > toWorld.getCBWorld().getPlayers().size()) {
            return PlayerLimitResult.WITHIN_PLAYERLIMIT;
        }
        return permissionsTool.hasBypassPlayerLimit(sender, toWorld)
                ? PlayerLimitResult.BYPASS_PLAYERLIMIT : PlayerLimitResult.EXCEED_PLAYERLIMIT;
    }

    /**
     * Checks if a world is not blacklisted by another world.
     *
     * @param fromWorld The from world that is checked for blacklisting.
     * @param toWorld   The to world that has the blacklist.
     * @return The action check result.
     */
    public BlacklistResult isNotBlacklisted(@Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        if (fromWorld == null) {
            return BlacklistResult.UNKNOWN_SOURCE_WORLD;
        }
        // TODO: Add permission to bypass blacklist
        return toWorld.getWorldBlacklist().contains(fromWorld.getName())
                ? BlacklistResult.BLACKLISTED : BlacklistResult.NOT_BLACKLISTED;
    }

    /**
     * Checks if a player can keep their game mode on world change.
     *
     * @param player    The sender to check.
     * @param toWorld   The world to teleport to.
     * @return The action check result.
     */
    public GameModeResult canKeepGameMode(@NotNull Player player, @NotNull MVWorld toWorld) {
        if (player.getGameMode().equals(toWorld.getGameMode())) {
            return GameModeResult.KEEP_GAME_MODE;
        }
        //TODO: Add config option disable game mode enforcement
        return permissionsTool.hasBypassGameModeEnforcement(player, toWorld)
                ? GameModeResult.KEEP_GAME_MODE : GameModeResult.ENFORCE_GAME_MODE;
    }
}
