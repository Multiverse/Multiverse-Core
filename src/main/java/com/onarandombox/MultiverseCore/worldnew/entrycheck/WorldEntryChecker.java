package com.onarandombox.MultiverseCore.worldnew.entrycheck;

import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import com.onarandombox.MultiverseCore.economy.MVEconomist;
import com.onarandombox.MultiverseCore.permissions.CorePermissionsChecker;
import com.onarandombox.MultiverseCore.utils.result.Result;
import com.onarandombox.MultiverseCore.utils.result.ResultChain;
import com.onarandombox.MultiverseCore.world.configuration.EntryFee;
import com.onarandombox.MultiverseCore.worldnew.MVWorld;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.onarandombox.MultiverseCore.utils.message.MessageReplacement.replace;

public class WorldEntryChecker {
    private final @NotNull MVCoreConfig config;
    private final @NotNull MVEconomist economist;
    private final @NotNull CorePermissionsChecker permissionsChecker;

    private final @NotNull CommandSender sender;

    public WorldEntryChecker(
            @NotNull MVCoreConfig config,
            @NotNull CorePermissionsChecker permissionsChecker,
            @NotNull MVEconomist economist,
            @NotNull CommandSender sender
            ) {
        this.config = config;
        this.permissionsChecker = permissionsChecker;
        this.economist = economist;
        this.sender = sender;
    }

    public ResultChain canStayInWorld(@NotNull MVWorld world) {
        return canEnterWorld(null, world);
    }

    public ResultChain canEnterWorld(@Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        return ResultChain.builder()
                .then(() -> canAccessWorld(toWorld))
                .then(() -> isWithinPlayerLimit(toWorld))
                .then(() -> isNotBlacklisted(fromWorld, toWorld))
                .then(() -> canPayEntryFee(toWorld))
                .build();
    }

    public Result<WorldAccessResult.Success, WorldAccessResult.Failure> canAccessWorld(@NotNull MVWorld world) {
        if (!config.getEnforceAccess()) {
            return Result.success(WorldAccessResult.Success.NO_ENFORCE_WORLD_ACCESS);
        }
        return permissionsChecker.hasWorldAccessPermission(this.sender, world)
                ? Result.success(WorldAccessResult.Success.HAS_WORLD_ACCESS)
                : Result.failure(WorldAccessResult.Failure.NO_WORLD_ACCESS);
    }

    public Result<PlayerLimitResult.Success, PlayerLimitResult.Failure> isWithinPlayerLimit(@NotNull MVWorld world) {
        final int playerLimit = world.getPlayerLimit();
        if (playerLimit <= -1) {
            return Result.success(PlayerLimitResult.Success.NO_PLAYERLIMIT);
        }
        if (permissionsChecker.hasPlayerLimitBypassPermission(sender, world)) {
            return Result.success(PlayerLimitResult.Success.BYPASS_PLAYERLIMIT);
        }
        return playerLimit > world.getBukkitWorld().map(org.bukkit.World::getPlayers).map(java.util.Collection::size).getOrElse(0)
                ? Result.success(PlayerLimitResult.Success.WITHIN_PLAYERLIMIT)
                : Result.failure(PlayerLimitResult.Failure.EXCEED_PLAYERLIMIT);
    }

    public Result<BlacklistResult.Success, BlacklistResult.Failure> isNotBlacklisted(@Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        if (fromWorld == null) {
            return Result.success(BlacklistResult.Success.UNKNOWN_FROM_WORLD);
        }
        return toWorld.getWorldBlacklist().contains(fromWorld.getName())
                ? Result.failure(BlacklistResult.Failure.BLACKLISTED, replace("{world}").with(fromWorld.getAlias()))
                : Result.success(BlacklistResult.Success.NOT_BLACKLISTED);
    }

    public Result<EntryFeeResult.Success, EntryFeeResult.Failure> canPayEntryFee(MVWorld world) {
        double price = world.getPrice();
        Material currency = world.getCurrency();
        if (price == 0D && (currency == null || currency == EntryFee.DISABLED_MATERIAL)) {
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
        return economist.isPlayerWealthyEnough(player, price, currency)
                ? Result.success(EntryFeeResult.Success.ENOUGH_MONEY)
                : Result.failure(EntryFeeResult.Failure.NOT_ENOUGH_MONEY, replace("{amount}").with("$##")); // TODO: Money formatting
    }
}
