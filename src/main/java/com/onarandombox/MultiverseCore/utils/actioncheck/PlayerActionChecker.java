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

public class PlayerActionChecker {
    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;
    private final PermissionsTool permissionsTool;

    public PlayerActionChecker(MultiverseCore plugin) {
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();
        this.permissionsTool = plugin.getPermissionsTool();
    }

    public ActionCheckResult canUseDestinationToTeleport(@NotNull Player teleportee, @Nullable ParsedDestination<?> destination){
        return canUseDestinationToTeleport(null, teleportee, destination);
    }

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

    public ActionResponse canGoToDestination(@NotNull Player teleportee, @Nullable ParsedDestination<?> destination){
        return canGoToDestination(null, teleportee, destination);
    }

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

    public ActionResponse canGoToLocation(@NotNull Player teleportee, @Nullable Location location){
        return canGoToLocation(null, teleportee, location);
    }

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

    public ActionResponse canEnterWorld(@NotNull Player player, @NotNull MVWorld toWorld) {
        return canEnterWorld(null, player, toWorld);
    }

    public ActionResponse canEnterWorld(@Nullable CommandSender teleporter,
                                        @NotNull Player player,
                                        @NotNull MVWorld toWorld
    ) {
        MVWorld fromWorld = this.worldManager.getMVWorld(player.getWorld());
        return canGoFromToWorld(teleporter, player, fromWorld, toWorld);
    }

    public ActionResponse canGoFromToWorld(@NotNull CommandSender teleportee, @Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        return canGoFromToWorld(null, teleportee, fromWorld, toWorld);
    }

    public ActionResponse canGoFromToWorld(@Nullable CommandSender teleporter,
                                           @NotNull CommandSender teleportee,
                                           @Nullable MVWorld fromWorld,
                                           @Nullable MVWorld toWorld
    ) {
        ActionResponse response = new ActionResponse();
        if (toWorld == null) {
            return new ActionResponse().addResult(ActionCheckResult.NOT_MV_WORLD);
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

    public ActionCheckResult hasAccessToWorld(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        if (!this.plugin.getMVConfig().getEnforceAccess()) {
            return ActionCheckResult.NO_ENFORCE_WORLD_ACCESS;
        }
        return permissionsTool.hasWorldAccess(sender, toWorld)
                ? ActionCheckResult.HAS_WORLD_ACCESS : ActionCheckResult.NO_WORLD_ACCESS;
    }

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

    public ActionCheckResult isNotBlacklisted(@NotNull Player player, @NotNull MVWorld toWorld) {
        MVWorld fromWorld = this.worldManager.getMVWorld(player.getWorld());
        return isNotBlacklisted(fromWorld, toWorld);
    }
    
    public ActionCheckResult isNotBlacklisted(@Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        if (fromWorld == null) {
            // No blacklisting if the player is not in a MV world
            return ActionCheckResult.NOT_BLACKLISTED;
        }
        return toWorld.getWorldBlacklist().contains(fromWorld.getName())
                ? ActionCheckResult.BLACKLISTED : ActionCheckResult.NOT_BLACKLISTED;
    }

    public ActionCheckResult canKeepGameMode(@NotNull CommandSender sender, @NotNull MVWorld toWorld) {
        //TODO: Add config option disable game mode enforcement
        return permissionsTool.hasBypassGameModeEnforcement(sender, toWorld)
                ? ActionCheckResult.KEEP_GAME_MODE : ActionCheckResult.ENFORCE_GAME_MODE;
    }
}
