package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.commandtools.context.GameRuleValue;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class GameruleCommand extends MultiverseCoreCommand {
    public GameruleCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("gamerule")
    @CommandPermission("multiverse.core.gamerule")
    @CommandCompletion("@gamerules true|false|@range:1-10 @mvworlds:multiple|*")
    @Syntax("<Gamerule> <Gamerule value> [World or *]")
    @Description("{@@mv-core.gamerule.description}")
    public void onGameruleCommand(BukkitCommandIssuer issuer,

                                  @Syntax("<Gamerule>")
                                  @Description("{@@mv-core.gamerule.gamerule.description}")
                                  GameRule gamerule,

                                  @Syntax("<Value>")
                                  @Description("{@@mv-core.gamerule.value.description}")
                                  GameRuleValue gameRuleValue,

                                  @Flags("resolve=issuerAware")
                                  @Syntax("[World or *]")
                                  @Description("{@@mv-core.gamerule.world.description}")
                                  MVWorld[] worlds
    ) {
        Object value = gameRuleValue.getValue();
        boolean success = true;
        for(MVWorld world : worlds) {
            // Set gamerules and add false to list if it fails
            if (!world.getCBWorld().setGameRule(gamerule, value)) {
                issuer.sendInfo(MVCorei18n.GAMERULE_FAILED,
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
            }
            else if (worlds.length > 1) {
                issuer.sendInfo(MVCorei18n.GAMERULE_SUCCESS_MULTIPLE,
                        "{gamerule}", gamerule.getName(),
                        "{value}", value.toString(),
                        "{count}", String.valueOf(worlds.length));
            }
        }
    }
}
