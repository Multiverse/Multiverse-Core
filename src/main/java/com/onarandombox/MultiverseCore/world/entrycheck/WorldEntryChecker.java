package com.onarandombox.MultiverseCore.world.entrycheck;

import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.config.MVCoreConfig;
import com.onarandombox.MultiverseCore.economy.MVEconomist;
import com.onarandombox.MultiverseCore.permissions.CorePermissionsChecker;
import com.onarandombox.MultiverseCore.utils.checkresult.CheckResult;
import com.onarandombox.MultiverseCore.utils.checkresult.CheckResultChain;
import com.onarandombox.MultiverseCore.utils.message.MessageReplacement;
import com.onarandombox.MultiverseCore.world.configuration.EntryFee;
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

    public CheckResultChain canStayInWorld(@NotNull MVWorld world) {
        return canEnterWorld(null, world);
    }

    public CheckResultChain canEnterWorld(@Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        return CheckResultChain.builder()
                .then(() -> canAccessWorld(toWorld))
                .then(() -> isWithinPlayerLimit(toWorld))
                .then(() -> isNotBlacklisted(fromWorld, toWorld))
                .then(() -> canPayEntryFee(toWorld))
                .build();
    }

    public CheckResult<WorldAccessResult.Success, WorldAccessResult.Failure> canAccessWorld(@NotNull MVWorld world) {
        if (!config.getEnforceAccess()) {
            return CheckResult.success(WorldAccessResult.Success.NO_ENFORCE_WORLD_ACCESS);
        }
        return permissionsChecker.hasWorldAccessPermission(this.sender, world)
                ? CheckResult.success(WorldAccessResult.Success.HAS_WORLD_ACCESS)
                : CheckResult.failure(WorldAccessResult.Failure.NO_WORLD_ACCESS);
    }

    public CheckResult<PlayerLimitResult.Success, PlayerLimitResult.Failure> isWithinPlayerLimit(@NotNull MVWorld world) {
        final int playerLimit = world.getPlayerLimit();
        if (playerLimit <= -1) {
            return CheckResult.success(PlayerLimitResult.Success.NO_PLAYERLIMIT);
        }
        if (permissionsChecker.hasPlayerLimitBypassPermission(sender, world)) {
            return CheckResult.success(PlayerLimitResult.Success.BYPASS_PLAYERLIMIT);
        }
        return playerLimit > world.getCBWorld().getPlayers().size()
                ? CheckResult.success(PlayerLimitResult.Success.WITHIN_PLAYERLIMIT)
                : CheckResult.failure(PlayerLimitResult.Failure.EXCEED_PLAYERLIMIT);
    }

    public CheckResult<BlacklistResult.Success, BlacklistResult.Failure> isNotBlacklisted(@Nullable MVWorld fromWorld, @NotNull MVWorld toWorld) {
        if (fromWorld == null) {
            return CheckResult.success(BlacklistResult.Success.UNKNOWN_FROM_WORLD);
        }
        return toWorld.getWorldBlacklist().contains(fromWorld.getName())
                ? CheckResult.failure(BlacklistResult.Failure.BLACKLISTED, replace("{world}").with(fromWorld.getAlias()))
                : CheckResult.success(BlacklistResult.Success.NOT_BLACKLISTED);
    }

    public CheckResult<EntryFeeResult.Success, EntryFeeResult.Failure> canPayEntryFee(MVWorld world) {
        double price = world.getPrice();
        Material currency = world.getCurrency();
        if (price == 0D && (currency == null || currency == EntryFee.DISABLED_MATERIAL)) {
            return CheckResult.success(EntryFeeResult.Success.FREE_ENTRY);
        }
        if (sender instanceof ConsoleCommandSender || sender instanceof BlockCommandSender) {
            return CheckResult.success(EntryFeeResult.Success.CONSOLE_OR_BLOCK_COMMAND_SENDER);
        }
        if (permissionsChecker.hasWorldExemptPermission(sender, world)) {
            return CheckResult.success(EntryFeeResult.Success.EXEMPT_FROM_ENTRY_FEE);
        }
        if (!(sender instanceof Player player)) {
            return CheckResult.failure(EntryFeeResult.Failure.CANNOT_PAY_ENTRY_FEE);
        }
        return economist.isPlayerWealthyEnough(player, price, currency)
                ? CheckResult.success(EntryFeeResult.Success.ENOUGH_MONEY)
                : CheckResult.failure(EntryFeeResult.Failure.NOT_ENOUGH_MONEY, replace("{amount}").with("$##")); //TODO
    }
}
