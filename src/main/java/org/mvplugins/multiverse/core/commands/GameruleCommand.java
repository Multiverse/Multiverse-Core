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
import io.vavr.control.Option;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.context.GameRuleValue;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.PageFilterFlags;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.MapContentProvider;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
@Subcommand("gamerule|rule")
class GameruleCommand extends CoreCommand {

    private final PageFilterFlags flags;

    @Inject
    GameruleCommand(@NotNull PageFilterFlags flags) {
        this.flags = flags;
    }

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

    @SuppressWarnings("rawtypes,unchecked")
    @Subcommand("reset")
    @CommandPermission("multiverse.core.gamerule.set")
    @CommandCompletion("@gamerules @mvworlds:multiple|*")
    @Syntax("<Gamerule> [World or *]")
    @Description("{@@mv-core.gamerule.reset.description}")
    void onGameruleResetCommand(
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
                        .flatMap(bukkitWorld -> Option.of(bukkitWorld.getGameRuleDefault(gamerule))
                                .map(value -> bukkitWorld.setGameRule(gamerule, value)))
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

    @Subcommand("list")
    @CommandPermission("multiverse.core.gamerule.list")
    @CommandCompletion("@mvworlds|@flags:groupName=mvgamerulecommand @flags:groupName=" + PageFilterFlags.NAME)
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
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        ContentDisplay.create()
                .addContent(MapContentProvider.forContent(getGameRuleMap(world.getBukkitWorld().getOrNull()))
                        .withKeyColor(ChatColor.AQUA)
                        .withValueColor(ChatColor.WHITE))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader(this.getListTitle(world.getBukkitWorld().getOrNull()))
                        .doPagination(true)
                        .withTargetPage(parsedFlags.flagValue(flags.page, 1))
                        .withFilter(parsedFlags.flagValue(flags.filter, DefaultContentFilter.get())))
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

    private Message getListTitle(@Nullable World world) {
        return Message.of(MVCorei18n.GAMERULE_LIST_TITLE, Replace.WORLD.with(world == null ? "null" : world.getName()));
    }

    @Service
    private static final class LegacyAlias extends GameruleCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(@NotNull PageFilterFlags flags) {
            super(flags);
        }

        @Override
        @CommandAlias("mvrule|mvgamerule")
        void onGameruleSetCommand(MVCommandIssuer issuer, GameRule gamerule, GameRuleValue gameRuleValue, LoadedMultiverseWorld[] worlds) {
            super.onGameruleSetCommand(issuer, gamerule, gameRuleValue, worlds);
        }

        @Override
        @CommandAlias("mvrules|mvgamerules")
        void onGameruleListCommand(MVCommandIssuer issuer, LoadedMultiverseWorld world, String[] flags) {
            super.onGameruleListCommand(issuer, world, flags);
        }
    }
}
