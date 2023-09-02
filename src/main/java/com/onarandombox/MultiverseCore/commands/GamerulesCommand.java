package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import jakarta.inject.Inject;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * List all gamerules in your current or specified world
 */
@Service
@CommandAlias("mv")
public class GamerulesCommand extends MultiverseCommand {

    private final MVWorldManager worldManager;

    @Inject
    public GamerulesCommand(@NotNull MVCommandManager commandManager, @NotNull MVWorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("gamerules|rules")
    @CommandPermission("multiverse.core.gamerules.list")
    @CommandCompletion("@mvworlds")
    @Syntax("[World]")
    @Description("{@@mv-core.gamerules.description}") //TODO i18n
    public void onGamerulesCommand(@NotNull BukkitCommandIssuer issuer,

                                   @Flags("resolve=issuerOnly")
                                   Player player,

                                   @Single
                                   @Syntax("<world>")
                                   @Description("{@@mv-core.gamerules.world.description}") //TODO i18n
                                   MVWorld world
    ) {
        if (!issuer.isPlayer()) {
            if (world == null) {
                issuer.sendMessage("You must specify a world from the command line");
                return;
            }
        }

        // Get the players world if none is specified
        if (world == null) {
            Logging.finer("Getting the player's current world to list gamerules for");
            world = worldManager.getMVWorld(player.getWorld());
        }

        issuer.sendMessage("--- Gamerules for " + world.getName() + " ---\n" + encodeMap(getGameRuleMap(world.getCBWorld())));



    }

    /**
     * Gets all the gamerules and their values for a given world
     * @param world The world to find gamerules for
     * @return A map of the gamerules and their values
     */
    private Map<String, String> getGameRuleMap(World world) {
        Map<String, String> gameRuleMap = new HashMap<>();

        for (GameRule<?> gamerule : GameRule.values()) {
            Object gameruleValue = world.getGameRuleValue(gamerule);
            if (gameruleValue == null) {
                gameRuleMap.put(gamerule.getName(), "null");
                continue;
            }
            gameRuleMap.put(gamerule.getName(), gameruleValue.toString());
        }
        return gameRuleMap;
    }

    private String encodeMap(Map<String, String> inMap) { //TODO i18n
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : inMap.keySet()) {
            String value = inMap.get(key);

            stringBuilder
                    .append("&l")
                    .append(key)
                    .append("&f : &6")
                    .append(value)
                    .append("\n");
        }
        return stringBuilder.toString();
    }
}
