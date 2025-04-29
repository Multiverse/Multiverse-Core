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
import org.bukkit.entity.SpawnCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.command.flags.PageFilterFlags;
import org.mvplugins.multiverse.core.config.handle.PropertyModifyAction;
import org.mvplugins.multiverse.core.display.ContentDisplay;
import org.mvplugins.multiverse.core.display.filters.DefaultContentFilter;
import org.mvplugins.multiverse.core.display.handlers.PagedSendHandler;
import org.mvplugins.multiverse.core.display.parsers.ListContentProvider;
import org.mvplugins.multiverse.core.locale.message.Message;
import org.mvplugins.multiverse.core.utils.StringFormatter;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;
import org.mvplugins.multiverse.core.world.entity.SpawnCategoryConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Subcommand("entity-spawn-config")
final class EntitySpawnConfigCommand extends CoreCommand {

    private final WorldManager worldManager;
    private final PageFilterFlags flags;

    @Inject
    EntitySpawnConfigCommand(@NotNull WorldManager worldManager, @NotNull PageFilterFlags flags) {
        this.worldManager = worldManager;
        this.flags = flags;
    }

    @Subcommand("info")
    @CommandPermission("multiverse.core.entityspawnconfig.info")
    @CommandCompletion("@mvworlds:scope=both|@flags:resolveUntil=arg1,groupName=" + PageFilterFlags.NAME + " @flags:groupName=" + PageFilterFlags.NAME)
    @Syntax("[world] [--page <page>] [--filter <filter>]")
    @Description("")
    void onInfoCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            MultiverseWorld world,

            @Optional
            @Syntax("[--page <page>] [--filter <filter>]")
            String[] flagArray
    ) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);
        ContentDisplay.create()
                .addContent(ListContentProvider.forContent(getEntitySpawnConfigList(world)))
                .withSendHandler(PagedSendHandler.create()
                        .withHeader(Message.of("==== [ Entity Spawn Config '" + world.getName() + "' ] ===="))
                        .withLinesPerPage(8)
                        .withTargetPage(parsedFlags.flagValue(flags.page, 1))
                        .withFilter(parsedFlags.flagValue(flags.filter, DefaultContentFilter.get())))
                .send(issuer);
    }

    private List<String> getEntitySpawnConfigList(MultiverseWorld world) {
        List<String> list = new ArrayList<>();
        Arrays.stream(SpawnCategory.values()).forEach(spawnCategory -> {
            list.add(spawnCategory.name() + ": ");
            SpawnCategoryConfig spawnCategoryConfig = world.getEntitySpawnConfig().getSpawnCategoryConfig(spawnCategory);
            list.add("  spawn: " + spawnCategoryConfig.isSpawn());
            list.add("  tick-rate: " + spawnCategoryConfig.getTickRate());
            list.add("  exceptions: " + StringFormatter.join(spawnCategoryConfig.getExceptions(), ", "));
        });
        return list;
    }

    @Subcommand("modify")
    @CommandPermission("multiverse.core.entityspawnconfig.modify")
    @CommandCompletion("@mvworlds:scope=both @spawncategories @propsmodifyaction @spawncategorypropsname @spawncategorypropsvalue")
    @Syntax("[world] <spawn-category> <set|add|reset|remove> <property> [value]")
    @Description("")
    void onModifyCommand(
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            MultiverseWorld world,

            @Syntax("<spawn-category>")
            SpawnCategory spawnCategory,

            @Syntax("<set|add|reset|remove>")
            PropertyModifyAction action,

            @Syntax("<property>")
            String property,

            @Optional
            @Single
            @Syntax("[value]")
            @Nullable String value
    ) {
        world.getEntitySpawnConfig()
                .getSpawnCategoryConfig(spawnCategory)
                .getStringPropertyHandle()
                .modifyPropertyString(property, value, action)
                .onSuccess(ignore -> issuer.sendMessage("Successfully set " + property + " to " + value
                        + " for " + spawnCategory.name() + " in " + world.getName()))
                .onFailure(e -> issuer.sendMessage("Unable to set " + property + " to " + value
                        + " for " + spawnCategory.name() + " in " + world.getName() + ": " + e.getMessage()));

        worldManager.saveWorldsConfig();
    }
}
