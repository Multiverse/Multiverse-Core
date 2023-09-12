package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.commandtools.context.GameRuleValue;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;

@Service
@CommandAlias("mv")
class GameruleCommand extends MultiverseCommand {

    @Inject
    GameruleCommand(@NotNull MVCommandManager commandManager) {
        super(commandManager);
    }

    @Subcommand("gamerule")
    @CommandPermission("multiverse.core.gamerule.set")
    @CommandCompletion("@gamerules true|false|@range:1-10 @mvworlds:multiple|*")
    @Syntax("<Gamerule> <Gamerule value> [World or *]")
    @Description("{@@mv-core.gamerule.description}")
    void onGameruleCommand(
            BukkitCommandIssuer issuer,

            @Syntax("<Gamerule>")
            @Description("{@@mv-core.gamerule.gamerule.description}")
            GameRule gamerule,

            @Syntax("<Value>")
            @Description("{@@mv-core.gamerule.value.description}")
            GameRuleValue gameRuleValue,

            @Flags("resolve=issuerAware")
            @Syntax("[World or *]")
            @Description("{@@mv-core.gamerule.world.description}")
            LoadedMultiverseWorld[] worlds) {
        Object value = gameRuleValue.getValue();
        boolean success = true;
        for (LoadedMultiverseWorld world : worlds) {
            // Set gamerules and add false to list if it fails
            World bukkitWorld = world.getBukkitWorld().getOrNull();
            if (bukkitWorld == null || !bukkitWorld.setGameRule(gamerule, value)) {
                issuer.sendError(MVCorei18n.GAMERULE_FAILED,
                        "{gamerule}", gamerule.getName(),
                        "{value}", value.toString(),
                        "{world}", world.getName(),
                        "{type}", gamerule.getType().getName());
                success = false;
            }
        }
        // Tell user if it was successful
        if (success) {
            if (worlds.length == 1) {
                issuer.sendInfo(MVCorei18n.GAMERULE_SUCCESS_SINGLE,
                        "{gamerule}", gamerule.getName(),
                        "{value}", value.toString(),
                        "{world}", worlds[0].getName());
            } else if (worlds.length > 1) {
                issuer.sendInfo(MVCorei18n.GAMERULE_SUCCESS_MULTIPLE,
                        "{gamerule}", gamerule.getName(),
                        "{value}", value.toString(),
                        "{count}", String.valueOf(worlds.length));
            }
        }
    }
}
