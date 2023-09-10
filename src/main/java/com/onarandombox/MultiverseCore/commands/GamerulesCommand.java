package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.commandtools.MVCommandIssuer;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandValueFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import com.onarandombox.MultiverseCore.display.ContentDisplay;
import com.onarandombox.MultiverseCore.display.filters.ContentFilter;
import com.onarandombox.MultiverseCore.display.filters.DefaultContentFilter;
import com.onarandombox.MultiverseCore.display.filters.RegexContentFilter;
import com.onarandombox.MultiverseCore.display.handlers.PagedSendHandler;
import com.onarandombox.MultiverseCore.display.parsers.MapContentProvider;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.worldnew.LoadedMultiverseWorld;
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

    private final CommandValueFlag<Integer> PAGE_FLAG = flag(CommandValueFlag
            .builder("--page", Integer.class)
            .addAlias("-p")
            .context(value -> {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new InvalidCommandArgument("Invalid page number: " + value);
                }
            })
            .build());

    private final CommandValueFlag<ContentFilter> FILTER_FLAG = flag(CommandValueFlag
            .builder("--filter", ContentFilter.class)
            .addAlias("-f")
            .context(value -> {
                try {
                    return RegexContentFilter.fromString(value);
                } catch (IllegalArgumentException e) {
                    throw new InvalidCommandArgument("Invalid filter: " + value);
                }
            })
            .build());

    @Inject
    GamerulesCommand(@NotNull MVCommandManager commandManager) {
        super(commandManager);
    }

    @Subcommand("gamerules|rules")
    @CommandPermission("multiverse.core.gamerule.list")
    @CommandCompletion("@mvworlds|@flags:groupName=mvgamerulescommand @flags:groupName=mvgamerulescommand")
    @Syntax("[world] [--page <page>] [--filter <filter>]")
    @Description("{@@mv-core.gamerules.description}")
    public void onGamerulesCommand(
            @NotNull MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("<world>")
            @Description("{@@mv-core.gamerules.description.world}")
            LoadedMultiverseWorld world,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            @Description("{@@mv-core.gamerules.description.page}")
            String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        ContentDisplay.create()
                .addContent(new MapContentProvider<>(getGameRuleMap(world.getBukkitWorld().getOrNull())) // TODO: Handle null
                        .withKeyColor(ChatColor.AQUA)
                        .withValueColor(ChatColor.WHITE))
                .withSendHandler(new PagedSendHandler()
                        .withHeader(this.getTitle(issuer, world.getBukkitWorld().getOrNull()))
                        .doPagination(true)
                        .withTargetPage(parsedFlags.flagValue(PAGE_FLAG, 1))
                        .withFilter(parsedFlags.flagValue(FILTER_FLAG, DefaultContentFilter.get())))
                .send(issuer);
    }

    /**
     * Gets all the gamerules and their values for a given world.
     *
     * @param world The world to find gamerules for.
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
