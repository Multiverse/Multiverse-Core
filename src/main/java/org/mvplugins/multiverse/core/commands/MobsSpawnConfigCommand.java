package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.entity.SpawnCategory;
import org.jvnet.hk2.annotations.Optional;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.config.handle.PropertyModifyAction;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

@Service
final class MobsSpawnConfigCommand extends CoreCommand {

    @Subcommand("mobsspawnconfig info")
    @CommandPermission("multiverse.core.mobsspawnconfig")
    @CommandCompletion("@mvworlds:scope=both")
    @Syntax("[world]")
    @Description("")
    void onMobsSpawnConfigListCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            MultiverseWorld world
    ) {
        issuer.sendMessage(world.getMobsSpawnConfig().toString());
    }

    @Subcommand("mobsspawnconfig modify")
    @CommandPermission("multiverse.core.mobsspawnconfig")
    @CommandCompletion("@mvworlds:scope=both")
    @Syntax("[world] <spawn-category> <set|add|reset|remove> <key> [value]")
    @Description("")
    void onMobsSpawnConfigModifyCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            MultiverseWorld world,

            SpawnCategory spawnCategory,
            PropertyModifyAction action,
            String key,

            @Single
            @Optional
            String value
    ) {

    }
}
