package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import io.vavr.control.Try;
import org.bukkit.WorldBorder;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

import java.util.function.Consumer;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
@Subcommand("worldborder")
final class WorldBorderCommand extends CoreCommand {

    @Subcommand("add")
    void onWorldBorderAdd(
            MVCommandIssuer issuer,

            @Syntax("<size>")
            double size,

            @Optional
            @Default("0")
            @Syntax("[time]")
            int time,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            LoadedMultiverseWorld world
    ) {
        worldBorderAction(issuer, world, worldBorder -> {
            onWorldBorderSet(issuer, worldBorder.getSize() + size, time, world);
        });
    }

    @Subcommand("center")
    void onWorldBorderCenter(
            MVCommandIssuer issuer,

            @Syntax("[x]")
            double x,

            @Syntax("[z]")
            double z,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            LoadedMultiverseWorld world
    ) {
        worldBorderAction(issuer, world, worldBorder -> {
            if (worldBorder.getCenter().getX() == x && worldBorder.getCenter().getZ() == z) {
                issuer.sendMessage(MVCorei18n.WORLDBORDER_CENTER_NOTHINGCHANGED,
                        Replace.WORLD.with(world.getAliasOrName()));
                return;
            }
            worldBorder.setCenter(x, z);
            issuer.sendMessage(MVCorei18n.WORLDBORDER_CENTER_SUCCESS,
                    replace("{x}").with(worldBorder.getCenter().getX()),
                    replace("{z}").with(worldBorder.getCenter().getZ()),
                    Replace.WORLD.with(world.getAliasOrName()));
        });
    }

    @Subcommand("damage amount")
    void onWorldBorderDamageAmount(
            MVCommandIssuer issuer,

            @Syntax("<damage>")
            double damage,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            LoadedMultiverseWorld world
    ) {
        worldBorderAction(issuer, world, worldBorder -> {
            if (worldBorder.getDamageAmount() == damage) {
                issuer.sendMessage(MVCorei18n.WORLDBORDER_DAMAGEAMOUNT_NOTHINGCHANGED,
                        Replace.WORLD.with(world.getAliasOrName()));
                return;
            }
            worldBorder.setDamageAmount(damage);
            issuer.sendMessage(MVCorei18n.WORLDBORDER_DAMAGEAMOUNT_SUCCESS,
                    replace("{amount}").with(worldBorder.getDamageAmount()),
                    Replace.WORLD.with(world.getAliasOrName()));
        });
    }

    @Subcommand("damage buffer")
    void onWorldBorderDamageBuffer(
            MVCommandIssuer issuer,

            @Syntax("<distance>")
            double distance,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            LoadedMultiverseWorld world
    ) {
        worldBorderAction(issuer, world, worldBorder -> {
            if (worldBorder.getDamageBuffer() == distance) {
                issuer.sendMessage(MVCorei18n.WORLDBORDER_DAMAGEBUFFER_NOTHINGCHANGED,
                        Replace.WORLD.with(world.getAliasOrName()));
                return;
            }
            worldBorder.setDamageBuffer(distance);
            issuer.sendMessage(MVCorei18n.WORLDBORDER_DAMAGEBUFFER_SUCCESS,
                    replace("{distance}").with(worldBorder.getDamageBuffer()),
                    Replace.WORLD.with(world.getAliasOrName()));
        });
    }

    @Subcommand("get")
    void onWorldBorderGet(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            LoadedMultiverseWorld world
    ) {
        worldBorderAction(issuer, world, worldBorder -> {
            issuer.sendMessage(MVCorei18n.WORLDBORDER_GET_SIZE,
                    replace("{size}").with(worldBorder.getSize()),
                    Replace.WORLD.with(world.getAliasOrName()));
        });
    }

    @Subcommand("set")
    void onWorldBorderSet(
            MVCommandIssuer issuer,

            @Syntax("<size>")
            double size,

            @Optional
            @Default("0")
            @Syntax("[time]")
            int time,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            LoadedMultiverseWorld world
    ) {
        worldBorderAction(issuer, world, worldBorder -> {
            double previousSize = worldBorder.getSize();
            if (previousSize == size) {
                issuer.sendMessage(MVCorei18n.WORLDBORDER_SET_NOTHINGCHANGED,
                        Replace.WORLD.with(world.getAliasOrName()));
                return;
            }
            worldBorder.setSize(size, time);
            if (time <= 0) {
                issuer.sendMessage(MVCorei18n.WORLDBORDER_SET_IMMEDIATE,
                        replace("{size}").with(worldBorder.getSize()),
                        Replace.WORLD.with(world.getAliasOrName()));
            } else {
                issuer.sendMessage(previousSize > size ? MVCorei18n.WORLDBORDER_SET_GROWING : MVCorei18n.WORLDBORDER_SET_SHRINKING,
                        replace("{size}").with(size),
                        replace("{time}").with(time),
                        Replace.WORLD.with(world.getAliasOrName()));
            }
        });
    }

    @Subcommand("warning distance")
    void onWorldBorderWarningDistance(
            MVCommandIssuer issuer,

            @Syntax("<distance>")
            int distance,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            LoadedMultiverseWorld world
    ) {
        worldBorderAction(issuer, world, worldBorder -> {
            if (worldBorder.getWarningDistance() == distance) {
                issuer.sendMessage(MVCorei18n.WORLDBORDER_WARNINGDISTANCE_NOTHINGCHANGED,
                        Replace.WORLD.with(world.getAliasOrName()));
                return;
            }
            worldBorder.setWarningDistance(distance);
            issuer.sendMessage(MVCorei18n.WORLDBORDER_WARNINGDISTANCE_SUCCESS,
                    replace("{distance}").with(worldBorder.getWarningDistance()),
                    Replace.WORLD.with(world.getAliasOrName()));
        });
    }

    @Subcommand("warning time")
    void onWorldBorderWarningTime(
            MVCommandIssuer issuer,

            @Syntax("<time>")
            int time,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            LoadedMultiverseWorld world
    ) {
        worldBorderAction(issuer, world, worldBorder -> {
            if (worldBorder.getWarningTime() == time) {
                issuer.sendMessage(MVCorei18n.WORLDBORDER_WARNINGTIME_NOTHINGCHANGED,
                        Replace.WORLD.with(world.getAliasOrName()));
                return;
            }
            worldBorder.setWarningTime(time);
            issuer.sendMessage(MVCorei18n.WORLDBORDER_WARNINGTIME_SUCCESS,
                    replace("{time}").with(worldBorder.getWarningTime()),
                    Replace.WORLD.with(world.getAliasOrName()));
        });
    }

    private void worldBorderAction(MVCommandIssuer issuer, LoadedMultiverseWorld world, Consumer<WorldBorder> worldBorderAction) {
        Try.run(() -> world.getWorldBorder().peek(worldBorderAction))
                .onFailure(error -> issuer.sendError(error.getLocalizedMessage()));
    }
}
