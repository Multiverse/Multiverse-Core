package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.MVWorld;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.display.ContentDisplay;
import com.onarandombox.MultiverseCore.display.handlers.PagedSendHandler;
import com.onarandombox.MultiverseCore.display.parsers.MapContentProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * List all gamerules in your current or specified world.
 */
@Service
@CommandAlias("mv")
public class GamerulesCommand extends MultiverseCommand {

    @Inject
    public GamerulesCommand(@NotNull MVCommandManager commandManager) {
        super(commandManager);
    }

    @Subcommand("gamerules|rules")
    @CommandPermission("multiverse.core.gamerule.list")
    @CommandCompletion("@mvworlds @range:1-6")
    @Syntax("[world] [page]")
    @Description("{@@mv-core.gamerules.description}")
    public void onGamerulesCommand(@NotNull BukkitCommandIssuer issuer,
            @Optional
            @Syntax("<world>")
            @Description("{@@mv-core.gamerules.description.world}")
            MVWorld world,

            @Optional
            @Default("1")
            @Syntax("<page>")
            @Description("{@@mv-core.gamerules.description.page}")
            int page
    ) {
        Logging.finer("Page is: " + page);

        ContentDisplay.create()
                .addContent(
                        new MapContentProvider<>(getGameRuleMap(world.getCBWorld()))
                        .withKeyColor(ChatColor.AQUA)
                        .withValueColor(ChatColor.WHITE)
                )
                .withSendHandler(
                        new PagedSendHandler()
                                .withHeader(this.getTitle(issuer, world.getCBWorld()))
                                .doPagination(true)
                                .withLinesPerPage(8)
                                .withTargetPage(page)
                )

                .send(issuer);
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

    private String getTitle(CommandIssuer issuer, World world) {
        return this.commandManager.formatMessage(
                issuer,
                MessageType.INFO,
                MVCorei18n.GAMERULES_TITLE,
                "{world}", world.getName());
    }
}
