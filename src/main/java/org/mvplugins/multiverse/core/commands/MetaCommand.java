package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.PageFilterFlags;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.MapContentProvider;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.util.Locale;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
@Subcommand("meta")
final class MetaCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final PageFilterFlags flags;

    @Inject
    MetaCommand(@NotNull WorldManager worldManager, @NotNull PageFilterFlags flags) {
        this.worldManager = worldManager;
        this.flags = flags;
    }

    @Subcommand("info")
    @CommandPermission("multiverse.core.meta.info")
    @CommandCompletion("@mvworlds:scope=both|@flags:resolveUntil=arg1,groupName=" + PageFilterFlags.NAME + " " +
            "@flags:groupName=" + PageFilterFlags.NAME)
    @Syntax("[world] [--page <page>] [--filter <filter>]")
    @Description("{@@mv-core.meta.info.description}")
    void infoCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware,maxArgForAware=0")
            @Syntax("[world]")
            @Description("{@@mv-core.meta.info.world.description}")
            MultiverseWorld world,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            String[] flagArray
    ) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);
        ContentDisplay.create()
                .addContent(MapContentProvider.forContent(world.getAllMeta())
                        .withKeyColor(ChatColor.AQUA)
                        .withValueColor(ChatColor.WHITE))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader(Message.of(MVCorei18n.META_INFO_HEADER, replace("{world}").with(world.getName())))
                        .noContentMessage(Message.of(MVCorei18n.META_INFO_NOCONTENT, replace("{world}").with(world.getName())))
                        .withTargetPage(parsedFlags.flagValue(flags.page, 1))
                        .withFilter(parsedFlags.flagValue(flags.filter, DefaultContentFilter.get())))
                .send(issuer);
    }

    @Subcommand("modify")
    @CommandPermission("multiverse.core.meta.modify")
    @CommandCompletion("@mvworlds:scope=both set|remove @mvworldmetakey @empty")
    @Syntax("[world] <set|remove> <key> [value]")
    void modifyCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware,maxArgForAware=4")
            @Syntax("[world]")
            MultiverseWorld world,

            @Syntax("<set|remove>")
            String action,

            @Syntax("<key>")
            String key,

            @Optional
            @Single
            @Syntax("[value]")
            String value
    ) {
        switch (action.toLowerCase(Locale.ROOT)) {
            case "set" -> world.setMeta(key, value)
                    .onSuccess(ignore -> issuer.sendInfo(MVCorei18n.META_MODIFY_SET_SUCCESS,
                                    replace("{key}").with(key),
                                    replace("{value}").with(value),
                                    Replace.WORLD.with(world.getName())))
                    .onFailure(throwable -> issuer.sendError(MVCorei18n.META_MODIFY_SET_FAILURE,
                            replace("{key}").with(key),
                            replace("{value}").with(value),
                            Replace.WORLD.with(world.getName()),
                            Replace.ERROR.with(throwable)));
            case "remove" -> world.removeMeta(key)
                    .onSuccess(ignore -> issuer.sendInfo(MVCorei18n.META_MODIFY_REMOVE_SUCCESS,
                            replace("{key}").with(key),
                            Replace.WORLD.with(world.getName())))
                    .onFailure(throwable -> issuer.sendError(MVCorei18n.META_MODIFY_REMOVE_FAILURE,
                            replace("{key}").with(key),
                            Replace.WORLD.with(world.getName()),
                            Replace.ERROR.with(throwable)));
            default -> issuer.sendError(MVCorei18n.META_MODIFY_INVALIDACTION, replace("{action}").with(action));
        }
        worldManager.saveWorldsConfig();
    }
}
