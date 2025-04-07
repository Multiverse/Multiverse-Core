package org.mvplugins.multiverse.core.commands;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.UnsafeFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.TeleportFailureReason;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
final class SpawnCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final AsyncSafetyTeleporter safetyTeleporter;
    private final CorePermissionsChecker permissionsChecker;
    private final UnsafeFlags flags;

    @Inject
    SpawnCommand(
            @NotNull WorldManager worldManager,
            @NotNull AsyncSafetyTeleporter safetyTeleporter,
            @NotNull CorePermissionsChecker permissionsChecker,
            @NotNull UnsafeFlags flags
    ) {
        this.worldManager = worldManager;
        this.safetyTeleporter = safetyTeleporter;
        this.permissionsChecker = permissionsChecker;
        this.flags = flags;
    }

    @CommandAlias("mvspawn")
    @Subcommand("spawn")
    @CommandPermission("@mvspawn")
    @CommandCompletion("@playersarray:checkPermissions=@mvspawnother|@flags:groupName=" + UnsafeFlags.NAME + ",resolveUntil=arg1"
            + " @flags:groupName=" + UnsafeFlags.NAME)
    @Syntax("[player]")
    @Description("{@@mv-core.spawn.description}")
    void onSpawnTpCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[player]")
            @Description("{@@mv-core.spawn.player.description}")
            Player[] players,

            @Optional
            @Syntax("[--unsafe]")
            @Description("")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        Map<World, List<Entity>> playersByWorld = Arrays.stream(players)
                .collect(Collectors.groupingBy(Entity::getWorld));
        playersByWorld.forEach((world, entities) ->
                teleportPlayersToSpawn(issuer, world, entities, !parsedFlags.hasFlag(flags.unsafe)));
    }

    private void teleportPlayersToSpawn(MVCommandIssuer issuer, World world,
                                        List<Entity> entities, boolean checkSafety) {
        LoadedMultiverseWorld mvWorld = worldManager.getLoadedWorld(world).getOrNull();
        if (mvWorld == null) {
            issuer.sendMessage("The world '" + world.getName() + "' is not a multiverse world!");
            return;
        }
        if (!permissionsChecker.checkSpawnPermission(issuer.getIssuer(), entities, mvWorld)) {
            issuer.sendMessage("You do not have permission to use this command in this world!");
            return;
        }

        if (entities.size() == 1) {
            handleSingleTeleport(issuer, mvWorld, entities.get(0), checkSafety);
        } else {
            handleMultiTeleport(issuer, mvWorld, entities, checkSafety);
        }
    }

    private void handleSingleTeleport(MVCommandIssuer issuer, LoadedMultiverseWorld mvWorld,
                                      Entity entity, boolean checkSafety) {
        safetyTeleporter.to(mvWorld.getSpawnLocation())
                .by(issuer)
                .checkSafety(checkSafety)
                .teleport(entity)
                .onSuccess(() -> issuer.sendInfo(MVCorei18n.SPAWN_SUCCESS,
                        Replace.PLAYER.with(entity.equals(issuer.getPlayer())
                                ? Message.of(MVCorei18n.GENERIC_YOU)
                                : Message.of(entity.getName())),
                        Replace.WORLD.with(mvWorld.getName())))
                .onFailure(failure -> issuer.sendError(MVCorei18n.SPAWN_FAILED,
                        Replace.PLAYER.with(entity.equals(issuer.getPlayer())
                                ? Message.of(MVCorei18n.GENERIC_YOU)
                                : Message.of(entity.getName())),
                        Replace.WORLD.with(mvWorld.getName()),
                        Replace.REASON.with(failure.getFailureMessage())));
    }

    private void handleMultiTeleport(MVCommandIssuer issuer, LoadedMultiverseWorld mvWorld,
                                     List<Entity> entities, boolean checkSafety) {
        safetyTeleporter.to(mvWorld.getSpawnLocation())
                .by(issuer)
                .checkSafety(checkSafety)
                .teleport(entities)
                .onSuccessCount(successCount ->  issuer.sendMessage(MVCorei18n.SPAWN_SUCCESS,
                        Replace.PLAYER.with(successCount + " players"), //todo: replace this with localised "{count} players"
                        Replace.WORLD.with(mvWorld.getName())))
                .onFailureCount(reasonsCountMap -> {
                    for (var entry : reasonsCountMap.entrySet()) {
                        Logging.finer("Failed to teleport %s players to %s: %s",
                                entry.getValue(), mvWorld.getName(), entry.getKey());
                        issuer.sendError(MVCorei18n.SPAWN_FAILED,
                                Replace.PLAYER.with(entry.getValue() + " players"),
                                Replace.WORLD.with(mvWorld.getName()),
                                Replace.REASON.with(entry.getKey().getMessageKey()));
                    }
                });
    }
}
