package org.mvplugins.multiverse.core.commands;

import java.util.HashMap;
import java.util.Map;

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
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.commandtools.flags.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.filters.RegexContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.MapContentProvider;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;

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
                .addContent(MapContentProvider.forContent(getGameRuleMap(world.getBukkitWorld().getOrNull())) // TODO: Handle null
                        .withKeyColor(ChatColor.AQUA)
                        .withValueColor(ChatColor.WHITE))
                .withSendHandler(PagedSendHandler.create()
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
