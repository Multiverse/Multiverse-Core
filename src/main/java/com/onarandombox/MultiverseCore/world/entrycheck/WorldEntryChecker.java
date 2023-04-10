package com.onarandombox.MultiverseCore.world.entrycheck;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import com.onarandombox.MultiverseCore.economy.MVEconomist;
import com.onarandombox.MultiverseCore.utils.permissions.PermissionsChecker;
import com.onarandombox.MultiverseCore.utils.result.Result;
import com.onarandombox.MultiverseCore.utils.result.ResultGroup;
import com.onarandombox.MultiverseCore.world.configuration.EntryFee;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class WorldEntryChecker {
    private final @NotNull CommandSender sender;
    private final @Nullable MVWorld fromWorld;
    private final @NotNull MVWorld toWorld;

    private final @NotNull MVCoreConfig config;
    private final @NotNull MVEconomist economist;
    private final @NotNull PermissionsChecker permissionsChecker;

    public WorldEntryChecker(
            @NotNull CommandSender sender,
            @Nullable MVWorld fromWorld,
            @NotNull MVWorld toWorld,
            @NotNull MVCoreConfig config,
            @NotNull PermissionsChecker permissionsChecker,
            @NotNull MVEconomist economist
    ) {
        this.sender = sender;
        this.fromWorld = fromWorld;
        this.toWorld = toWorld;
        this.config = config;
        this.permissionsChecker = permissionsChecker;
        this.economist = economist;
    }

    public ResultGroup canEnterWorld() {
        return canEnterWorld(true);
    }

    public ResultGroup canEnterWorld(boolean stopOnFailure) {
        return ResultGroup.builder(stopOnFailure)
                .then(this::canAccessWorld)
                .then(this::isNotBlacklisted)
                .then(this::isWithinPlayerLimit)
                .then(this::canPayEntryFee)
                .build();
    }

    public Result<WorldAccessResult.Success, WorldAccessResult.Failure> canAccessWorld() {
        if (!config.getEnforceAccess()) {
            return Result.success(WorldAccessResult.Success.NO_ENFORCE_WORLD_ACCESS);
        }
        return permissionsChecker.hasWorldAccessPermission(this.sender, this.toWorld)
                ? Result.success(WorldAccessResult.Success.HAS_WORLD_ACCESS)
                : Result.failure(WorldAccessResult.Failure.NO_WORLD_ACCESS);
    }

    public Result<EntryFeeResult.Success, EntryFeeResult.Failure> canPayEntryFee() {
        double price = toWorld.getPrice();
        Material currency = toWorld.getCurrency();
        if (price == 0D && (currency == null || currency == EntryFee.DISABLED_MATERIAL)) {
            return Result.success(EntryFeeResult.Success.FREE_ENTRY);
        }
        if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender) {
            return Result.success(EntryFeeResult.Success.CONSOLE_OR_BLOCK_COMMAND_SENDER);
        }
        if (permissionsChecker.hasWorldExemptPermission(sender, toWorld)) {
            return Result.success(EntryFeeResult.Success.EXEMPT_FROM_ENTRY_FEE);
        }
        if (!(sender instanceof Player player)) {
            return Result.failure(EntryFeeResult.Failure.CANNOT_PAY_ENTRY_FEE);
        }
        return economist.isPlayerWealthyEnough(player, price, currency)
                ? Result.success(EntryFeeResult.Success.ENOUGH_MONEY)
                : Result.failure(EntryFeeResult.Failure.NOT_ENOUGH_MONEY);
    }

    public Result<PlayerLimitResult.Success, PlayerLimitResult.Failure> isWithinPlayerLimit() {
        final int playerLimit = toWorld.getPlayerLimit();
        if (playerLimit <= -1) {
            return Result.success(PlayerLimitResult.Success.NO_PLAYERLIMIT);
        }
        if (permissionsChecker.hasPlayerLimitBypassPermission(sender, toWorld)) {
            return Result.success(PlayerLimitResult.Success.BYPASS_PLAYERLIMIT);
        }
        return playerLimit > toWorld.getCBWorld().getPlayers().size()
                ? Result.success(PlayerLimitResult.Success.WITHIN_PLAYERLIMIT)
                : Result.failure(PlayerLimitResult.Failure.EXCEED_PLAYERLIMIT);
    }

    public Result<BlacklistResult.Success, BlacklistResult.Failure> isNotBlacklisted() {
        if (fromWorld == null) {
            return Result.success(BlacklistResult.Success.UNKNOWN_FROM_WORLD);
        }
        return toWorld.getWorldBlacklist().contains(fromWorld.getName())
                ? Result.failure(BlacklistResult.Failure.BLACKLISTED)
                : Result.success(BlacklistResult.Success.NOT_BLACKLISTED);
    }
}
