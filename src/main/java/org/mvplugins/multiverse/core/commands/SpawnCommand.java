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
import jakarta.inject.Inject;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.flag.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.permissions.CorePermissionsChecker;
import org.mvplugins.multiverse.core.teleportation.AsyncSafetyTeleporter;
import org.mvplugins.multiverse.core.teleportation.TeleportFailureReason;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
@CommandAlias("mv")
final class SpawnCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final AsyncSafetyTeleporter safetyTeleporter;
    private final CorePermissionsChecker permissionsChecker;

    private final CommandFlag unsafeFlag = flag(CommandFlag.builder("--unsafe")
            .addAlias("-u")
            .build());

    @Inject
    SpawnCommand(@NotNull MVCommandManager commandManager,
                 @NotNull WorldManager worldManager,
                 @NotNull AsyncSafetyTeleporter safetyTeleporter,
                 @NotNull CorePermissionsChecker permissionsChecker) {
        super(commandManager);
        this.worldManager = worldManager;
        this.safetyTeleporter = safetyTeleporter;
        this.permissionsChecker = permissionsChecker;
    }

    @CommandAlias("mvspawn")
    @Subcommand("spawn")
    @CommandPermission("@mvspawn")
    @CommandCompletion("@playersarray:checkPermissions=@mvspawnother|@flags:groupName=mvspawncommand,resolveUntil=arg1"
            + " @flags:groupName=mvspawncommand")
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
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        Map<World, List<Player>> playersByWorld = Arrays.stream(players)
                .collect(Collectors.groupingBy(Entity::getWorld));
        playersByWorld.forEach((world, playerList) ->
                teleportPlayersToSpawn(issuer, world, playerList, !parsedFlags.hasFlag(unsafeFlag)));
    }

    private void teleportPlayersToSpawn(MVCommandIssuer issuer, World world,
                                        List<Player> players, boolean checkSafety) {
        LoadedMultiverseWorld mvWorld = worldManager.getLoadedWorld(world).getOrNull();
        if (mvWorld == null) {
            issuer.sendMessage("The world '" + world.getName() + "' is not a multiverse world!");
            return;
        }

        Player selfOrOther = players.stream()
                .filter(p -> !p.equals(issuer.getPlayer()))
                .findFirst()
                .orElse(issuer.getPlayer());
        if (!permissionsChecker.checkSpawnPermission(issuer.getIssuer(), selfOrOther, mvWorld)) {
            issuer.sendMessage("You do not have permission to use this command in this world!");
            return;
        }

        if (players.size() == 1) {
            handleSingleTeleport(issuer, mvWorld, players.get(0), checkSafety);
        } else {
            handleMultiTeleport(issuer, mvWorld, players, checkSafety);
        }
    }

    private void handleSingleTeleport(MVCommandIssuer issuer, LoadedMultiverseWorld mvWorld,
                                      Player player, boolean checkSafety) {
        safetyTeleporter.to(mvWorld.getSpawnLocation())
                .by(issuer)
                .checkSafety(checkSafety)
                .teleport(player)
                .onSuccess(() -> issuer.sendInfo(MVCorei18n.SPAWN_SUCCESS,
                        Replace.PLAYER.with(player.equals(issuer.getPlayer())
                                ? Message.of(MVCorei18n.GENERIC_YOU)
                                : Message.of(player.getName())),
                        Replace.WORLD.with(mvWorld.getName())))
                .onFailure(failure -> issuer.sendError(MVCorei18n.SPAWN_FAILED,
                        Replace.PLAYER.with(player.equals(issuer.getPlayer())
                                ? Message.of(MVCorei18n.GENERIC_YOU)
                                : Message.of(player.getName())),
                        Replace.WORLD.with(mvWorld.getName()),
                        Replace.REASON.with(failure.getFailureMessage())));
    }

    private void handleMultiTeleport(MVCommandIssuer issuer, LoadedMultiverseWorld mvWorld,
                                     List<Player> players, boolean checkSafety) {
        safetyTeleporter.to(mvWorld.getSpawnLocation())
                .by(issuer)
                .checkSafety(checkSafety)
                .teleport(players)
                .thenAccept(attempts -> {
                    int successCount = 0;
                    Map<TeleportFailureReason, Integer> failures = new EnumMap<>(TeleportFailureReason.class);
                    for (var attempt : attempts) {
                        if (attempt.isSuccess()) {
                            successCount++;
                        } else {
                            failures.compute(attempt.getFailureReason(),
                                    (reason, count) -> count == null ? 1 : count + 1);
                        }
                    }
                    if (successCount > 0) {
                        issuer.sendInfo(MVCorei18n.SPAWN_SUCCESS,
                                // TODO should use {count} instead of {player} most likely
                                Replace.PLAYER.with(successCount + " players"),
                                Replace.WORLD.with(mvWorld.getName()));
                    } else {
                        for (var entry : failures.entrySet()) {
                            issuer.sendError(MVCorei18n.SPAWN_FAILED,
                                    // TODO should use {count} instead of {player} most likely
                                    Replace.PLAYER.with(entry.getValue() + " players"),
                                    Replace.WORLD.with(mvWorld.getName()),
                                    Replace.REASON.with(entry.getKey().getMessageKey()));
                        }
                    }
                });
    }
}
