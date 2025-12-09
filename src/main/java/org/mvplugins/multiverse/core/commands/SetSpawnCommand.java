package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.context.PlayerLocation;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.UnsafeFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.teleportation.BlockSafety;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
class SetSpawnCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final BlockSafety blockSafety;
    private final UnsafeFlags flags;

    @Inject
    SetSpawnCommand(@NotNull WorldManager worldManager, @NotNull BlockSafety blockSafety, @NotNull UnsafeFlags flags) {
        this.worldManager = worldManager;
        this.blockSafety = blockSafety;
        this.flags = flags;
    }

    @CommandAlias("mvsetspawn")
    @Subcommand("setspawn")
    @CommandPermission("multiverse.core.spawn.set")
    @CommandCompletion("@flags:groupName=" + UnsafeFlags.NAME + " @flags:resolveUntil=arg1,groupName=" + UnsafeFlags.NAME)
    @Syntax("[worldname:x,y,z[,pitch,yaw]] [--unsafe]")
    @Description("{@@mv-core.setspawn.description}")
    void onSetSpawnCommand(
            MVCommandIssuer issuer,

            @Syntax("[worldname:x,y,z[,pitch,yaw]]")
            @Description("{@@mv-core.setspawn.location.description}")
            PlayerLocation playerLocation,

            @Optional
            @Syntax("[--unsafe]")
            @Description("")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);
        Location location = playerLocation.value();

        if (!parsedFlags.hasFlag(flags.unsafe) && !blockSafety.canSpawnAtLocationSafely(location)) {
            issuer.sendMessage(MVCorei18n.SETSPAWN_UNSAFE);
            return;
        }

        worldManager.getLoadedWorld(location.getWorld())
                .peek(mvWorld -> mvWorld.setSpawnLocation(location)
                        .andThenTry(worldManager::saveWorldsConfig)
                        .onSuccess(ignore -> issuer.sendMessage(MVCorei18n.SETSPAWN_SUCCESS,
                                Replace.WORLD.with(mvWorld.getName()),
                                Replace.LOCATION.with(prettyLocation(location))))
                        .onFailure(e -> issuer.sendMessage(MVCorei18n.SETSPAWN_FAILED,
                                Replace.WORLD.with(mvWorld.getName()),
                                Replace.ERROR.with(e))))
                .onEmpty(() -> issuer.sendMessage(MVCorei18n.SETSPAWN_NOTMVWORLD,
                        Replace.WORLD.with(location.getWorld().getName())));
    }

    private String prettyLocation(Location location) {
        return "%.2f, %.2f, %.2f, pitch:%.2f, yaw:%.2f"
                .formatted(location.getX(), location.getY(), location.getZ(), location.getPitch(), location.getYaw());
    }

    @Service
    private static final class LegacyAlias extends SetSpawnCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull WorldManager worldManager, @NotNull BlockSafety blockSafety, @NotNull UnsafeFlags flags) {
            super(worldManager, blockSafety, flags);
        }

        @Override
        @CommandAlias("mvss")
        void onSetSpawnCommand(MVCommandIssuer issuer, PlayerLocation location, String[] flagArray) {
            super.onSetSpawnCommand(issuer, location, flagArray);
        }
    }
}
