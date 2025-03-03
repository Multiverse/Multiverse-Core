package org.mvplugins.multiverse.core.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import co.aikar.commands.CommandIssuer;
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
import org.mvplugins.multiverse.core.commandtools.context.GameRuleValue;
import org.mvplugins.multiverse.core.commandtools.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.commandtools.flags.FilterCommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.PageCommandFlag;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.ContentFilter;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.MapContentProvider;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
@CommandAlias("mv")
@Subcommand("gamerule|rule|gamerules|rules")
final class GameruleCommand extends CoreCommand {

    private final CommandValueFlag<Integer> pageFlag = flag(PageCommandFlag.create());

    private final CommandValueFlag<ContentFilter> filterFlag = flag(FilterCommandFlag.create());

    @Inject
    GameruleCommand(@NotNull MVCommandManager commandManager) {
        super(commandManager);
    }

    @CommandAlias("mvrule|mvgamerule")
    @Subcommand("set")
    @CommandPermission("multiverse.core.gamerule.set")
    @CommandCompletion("@gamerules @gamerulesvalues @mvworlds:multiple|*")
    @Syntax("<Gamerule> <Gamerule value> [World or *]")
    @Description("{@@mv-core.gamerule.set.description}")
    void onGameruleSetCommand(
            MVCommandIssuer issuer,

            @Syntax("<Gamerule>")
            @Description("{@@mv-core.gamerule.set.gamerule.description}")
            GameRule gamerule,

            @Syntax("<Value>")
            @Description("{@@mv-core.gamerule.set.value.description}")
            GameRuleValue gameRuleValue,

            @Flags("resolve=issuerAware")
            @Syntax("[World or *]")
            @Description("{@@mv-core.gamerule.set.world.description}")
            LoadedMultiverseWorld[] worlds) {
        Object value = gameRuleValue.value();
        boolean success = true;
        for (LoadedMultiverseWorld world : worlds) {
            // Set gamerules and add false to list if it fails
            World bukkitWorld = world.getBukkitWorld().getOrNull();
            if (bukkitWorld == null || !bukkitWorld.setGameRule(gamerule, value)) {
                issuer.sendError(MVCorei18n.GAMERULE_SET_FAILED,
                        Replace.GAMERULE.with(gamerule.getName()),
                        Replace.VALUE.with(value.toString()),
                        Replace.WORLD.with(world.getName()),
                        replace("{type}").with(gamerule.getType().getName()));
                success = false;
            }
        }
        // Tell user if it was successful
        if (success) {
            if (worlds.length == 1) {
                issuer.sendInfo(MVCorei18n.GAMERULE_SET_SUCCESS_SINGLE,
                        Replace.GAMERULE.with(gamerule.getName()),
                        Replace.VALUE.with(value.toString()),
                        Replace.WORLD.with(worlds[0].getName()));
            } else if (worlds.length > 1) {
                issuer.sendInfo(MVCorei18n.GAMERULE_SET_SUCCESS_MULTIPLE,
                        Replace.GAMERULE.with(gamerule.getName()),
                        Replace.VALUE.with(value.toString()),
                        Replace.COUNT.with(String.valueOf(worlds.length)));
            }
        }
    }

    @Subcommand("reset")
    @CommandPermission("multiverse.core.gamerule.set")
    @CommandCompletion("@gamerules @mvworlds:multiple|*")
    @Syntax("<Gamerule> [World or *]")
    @Description("{@@mv-core.gamerule.reset.description}")
    void onGameruleSetCommand(
            MVCommandIssuer issuer,

            @Syntax("<Gamerule>")
            @Description("{@@mv-core.gamerule.reset.gamerule.description}")
            GameRule gamerule,

            @Flags("resolve=issuerAware")
            @Syntax("[World or *]")
            @Description("{@@mv-core.gamerule.reset.world.description}")
            LoadedMultiverseWorld[] worlds) {
        AtomicBoolean success = new AtomicBoolean(true);
        Arrays.stream(worlds)
                .forEach(world -> world.getBukkitWorld()
                .peek(bukkitWorld -> bukkitWorld.setGameRule(gamerule, bukkitWorld.getGameRuleDefault(gamerule)))
                .onEmpty(() -> {
                    success.set(false);
                    issuer.sendError(MVCorei18n.GAMERULE_RESET_FAILED,
                            Replace.GAMERULE.with(gamerule.getName()),
                            Replace.WORLD.with(world.getName()));
                }));

        // Tell user if it was successful
        if (success.get()) {
            if (worlds.length == 1) {
                issuer.sendInfo(MVCorei18n.GAMERULE_RESET_SUCCESS_SINGLE,
                        Replace.GAMERULE.with(gamerule.getName()),
                        Replace.WORLD.with(worlds[0].getName()));
            } else if (worlds.length > 1) {
                issuer.sendInfo(MVCorei18n.GAMERULE_RESET_SUCCESS_MULTIPLE,
                        Replace.GAMERULE.with(gamerule.getName()),
                        Replace.COUNT.with(String.valueOf(worlds.length)));
            }
        }
    }

    @CommandAlias("mvrules|mvgamerules")
    @Subcommand("list")
    @CommandPermission("multiverse.core.gamerule.list")
    @CommandCompletion("@mvworlds|@flags:groupName=mvgamerulecommand @flags:groupName=mvgamerulecommand")
    @Syntax("[world] [--page <page>] [--filter <filter>]")
    @Description("{@@mv-core.gamerule.list.description}")
    void onGameruleListCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("<world>")
            @Description("{@@mv-core.gamerule.list.description.world}")
            LoadedMultiverseWorld world,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            @Description("{@@mv-core.gamerule.list.description.page}")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        ContentDisplay.create()
                .addContent(MapContentProvider.forContent(getGameRuleMap(world.getBukkitWorld().getOrNull()))
                        .withKeyColor(ChatColor.AQUA)
                        .withValueColor(ChatColor.WHITE))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader(this.getListTitle(issuer, world.getBukkitWorld().getOrNull()))
                        .doPagination(true)
                        .withTargetPage(parsedFlags.flagValue(pageFlag, 1))
                        .withFilter(parsedFlags.flagValue(filterFlag, DefaultContentFilter.get())))
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
        if (world == null) {
            return gameRuleMap;
        }

        for (String gamerule : world.getGameRules()) {
            GameRule<?> gameruleEnum = GameRule.getByName(gamerule);
            if (gameruleEnum == null) {
                continue;
            }
            Object gameruleValue = world.getGameRuleValue(gameruleEnum);
            if (gameruleValue == null) {
                gameRuleMap.put(gameruleEnum.getName(), "null");
                continue;
            }
            gameRuleMap.put(gameruleEnum.getName(), gameruleValue.toString());
        }
        return gameRuleMap;
    }

    private String getListTitle(CommandIssuer issuer, World world) {
        return this.commandManager.formatMessage(
                issuer,
                MessageType.INFO,
                MVCorei18n.GAMERULE_LIST_TITLE,
                "{world}", world.getName());
    }
}
