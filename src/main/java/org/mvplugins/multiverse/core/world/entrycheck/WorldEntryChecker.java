package org.mvplugins.multiverse.core.world.entrycheck;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.config.MVCoreConfig;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.utils.result.Result;
import org.mvplugins.multiverse.core.utils.result.ResultChain;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

/**
 * Checks if a player can enter a world.
 */
public final class WorldEntryChecker {
    private final @NotNull MVCoreConfig config;
    private final @NotNull MVEconomist economist;
    private final @NotNull CorePermissionsChecker permissionsChecker;

    private final @NotNull CommandSender sender;

    WorldEntryChecker(
            @NotNull MVCoreConfig config,
            @NotNull CorePermissionsChecker permissionsChecker,
            @NotNull MVEconomist economist,
            @NotNull CommandSender sender) {
        this.config = config;
        this.permissionsChecker = permissionsChecker;
        this.economist = economist;
        this.sender = sender;
    }

    /**
     * Checks if the sender have access to be in the world.
     *
     * @param world The world to check.
     * @return The result of the check.
     */
    public ResultChain canStayInWorld(@NotNull LoadedMultiverseWorld world) {
        return ResultChain.builder()
                .then(() -> canAccessWorld(world))
                .then(() -> isWithinPlayerLimit(world))
                .build();
    }

    /**
     * Checks if the sender can enter the given world.
     *
     * @param fromWorld The world the sender is coming from.
     * @param toWorld   The world the sender is going to.
     * @return The result of the check.
     */
    public ResultChain canEnterWorld(
            @Nullable MultiverseWorld fromWorld, @NotNull LoadedMultiverseWorld toWorld) {
        return ResultChain.builder()
                .then(() -> canAccessWorld(toWorld))
                .then(() -> isWithinPlayerLimit(toWorld))
                .then(() -> isNotBlacklisted(fromWorld, toWorld))
                .then(() -> canPayEntryFee(toWorld))
                .build();
    }

    /**
     * Checks if the sender can access the given world.
     *
     * @param world The world to check.
     * @return The result of the check.
     */
    public Result<WorldAccessResult.Success, WorldAccessResult.Failure> canAccessWorld(@NotNull MultiverseWorld world) {
        if (!config.getEnforceAccess()) {
            return Result.success(WorldAccessResult.Success.NO_ENFORCE_WORLD_ACCESS);
        }
        return permissionsChecker.hasWorldAccessPermission(this.sender, world)
                ? Result.success(WorldAccessResult.Success.HAS_WORLD_ACCESS)
                : Result.failure(WorldAccessResult.Failure.NO_WORLD_ACCESS);
    }

    /**
     * Checks if the sender is within the player limit of the given world.
     *
     * @param world The world to check.
     * @return The result of the check.
     */
    public Result<PlayerLimitResult.Success, PlayerLimitResult.Failure> isWithinPlayerLimit(
            @NotNull LoadedMultiverseWorld world) {
        final int playerLimit = world.getPlayerLimit();
        if (playerLimit <= -1) {
            return Result.success(PlayerLimitResult.Success.NO_PLAYERLIMIT);
        }
        if (permissionsChecker.hasPlayerLimitBypassPermission(sender, world)) {
            return Result.success(PlayerLimitResult.Success.BYPASS_PLAYERLIMIT);
        }
        int numberOfPlayersInWorld = world.getBukkitWorld().map(World::getPlayers)
                .map(Collection::size)
                .getOrElse(0);
        return playerLimit > numberOfPlayersInWorld
                ? Result.success(PlayerLimitResult.Success.WITHIN_PLAYERLIMIT)
                : Result.failure(PlayerLimitResult.Failure.EXCEED_PLAYERLIMIT);
    }

    /**
     * Checks if the sender is not blacklisted from the given world.
     *
     * @param fromWorld The world the sender is coming from.
     * @param toWorld   The world the sender is going to.
     * @return The result of the check.
     */
    public Result<BlacklistResult.Success, BlacklistResult.Failure> isNotBlacklisted(
            @Nullable MultiverseWorld fromWorld, @NotNull MultiverseWorld toWorld) {
        if (fromWorld == null) {
            return Result.success(BlacklistResult.Success.UNKNOWN_FROM_WORLD);
        }
        return toWorld.getWorldBlacklist().contains(fromWorld.getName())
                ? Result.failure(BlacklistResult.Failure.BLACKLISTED, Replace.WORLD.with(fromWorld.getAlias()))
                : Result.success(BlacklistResult.Success.NOT_BLACKLISTED);
    }

    /**
     * Checks if the sender can pay the entry fee for the given world.
     *
     * @param world The world to check.
     * @return The result of the check.
     */
    public Result<EntryFeeResult.Success, EntryFeeResult.Failure> canPayEntryFee(MultiverseWorld world) {
        double price = world.getPrice();
        if (!world.isEntryFeeEnabled() || price == 0D) {
            return Result.success(EntryFeeResult.Success.FREE_ENTRY);
        }
        if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender) {
            return Result.success(EntryFeeResult.Success.CONSOLE_OR_BLOCK_COMMAND_SENDER);
        }
        if (permissionsChecker.hasWorldExemptPermission(sender, world)) {
            return Result.success(EntryFeeResult.Success.EXEMPT_FROM_ENTRY_FEE);
        }
        if (!(sender instanceof Player player)) {
            return Result.failure(EntryFeeResult.Failure.CANNOT_PAY_ENTRY_FEE);
        }
        Material currency = world.getCurrency();
        return economist.isPlayerWealthyEnough(player, price, currency)
                ? Result.success(EntryFeeResult.Success.ENOUGH_MONEY)
                : Result.failure(EntryFeeResult.Failure.NOT_ENOUGH_MONEY,
                replace("{amount}").with(economist.formatPrice(price, currency)));
    }
}
