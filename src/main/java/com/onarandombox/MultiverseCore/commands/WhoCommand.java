/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandTools.display.ContentCreator;
import com.onarandombox.MultiverseCore.commandTools.display.ContentFilter;
import com.onarandombox.MultiverseCore.commandTools.display.inline.ListDisplay;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CommandAlias("mv")
public class WhoCommand extends MultiverseCoreCommand {

    public WhoCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("whoall")
    @CommandPermission("multiverse.core.list.who.all")
    @Syntax("[filter]")
    public void onWhoAllCommand(@NotNull CommandSender sender,
                                @Nullable @Optional Player player,

                                @Syntax("[filter]")
                                @Description("Filter the player names.")
                                @NotNull ContentFilter filter) {

        Set<Player> visiblePlayers = getVisiblePlayers(player);

        sender.sendMessage(String.format("%s--- Worlds and their players --- %s%s/%s",
                ChatColor.GOLD, ChatColor.AQUA, visiblePlayers.size(), this.plugin.getServer().getMaxPlayers()));
        if (filter.hasFilter()) {
            sender.sendMessage(String.format("[ %s ]", filter.getFormattedString()));
        }

        ListDisplay display = new ListDisplay().withSender(sender)
                .withFilter(filter);

        this.plugin.getMVWorldManager().getMVWorlds().stream()
                .filter(world -> player == null || this.plugin.getMVPerms().canEnterWorld(player, world))
                .forEach(world -> showPLayersInWorld(world, display, visiblePlayers));
    }

    private void showPLayersInWorld(@NotNull MultiverseWorld world,
                                    @NotNull ListDisplay display,
                                    @NotNull Set<Player> visiblePlayers) {

        String prefix = String.format("%s%s - ", world.getColoredWorldString(), ChatColor.WHITE);

        display.withCreator(buildPlayerList(world, visiblePlayers))
                .withPrefix(prefix)
                .withEmptyMessage(String.format("%s%sNo players found.", prefix, ChatColor.GRAY))
                .build()
                .run();
    }

    @Subcommand("who")
    @CommandPermission("multiverse.core.list.who")
    @Syntax("[world] [filter]")
    @CommandCompletion("@MVWorlds")
    public void onWhoCommand(@NotNull CommandSender sender,
                             @Nullable @Optional Player player,

                             @Syntax("[world]")
                             @Description("World to show player list.")
                             @NotNull
                             @Flags("other,defaultself,fallbackself")
                             @Conditions("hasWorldAccess") MultiverseWorld world,

                             @Syntax("[filter]")
                             @Description("Filter the player names.")
                             @NotNull ContentFilter filter) {

        Set<Player> visiblePlayers = getVisiblePlayers(player);

        new ListDisplay().withSender(sender)
                .withHeader(String.format("%s===[ Players in %s%s ]===", ChatColor.AQUA, world.getColoredWorldString(), ChatColor.AQUA))
                .withCreator(buildPlayerList(world, visiblePlayers))
                .withFilter(filter)
                .withEmptyMessage(String.format("%sNo players found.", ChatColor.GRAY))
                .build()
                .runTaskAsynchronously(this.plugin);
    }

    @NotNull
    private ContentCreator<List<String>> buildPlayerList(@NotNull MultiverseWorld world,
                                                         @NotNull Set<Player> visiblePlayers) {

        return () -> world.getCBWorld().getPlayers().stream()
            .filter(visiblePlayers::contains)
            .map(Player::getDisplayName)
            .collect(Collectors.toList());
    }

    @NotNull
    private Set<Player> getVisiblePlayers(@Nullable Player player) {
        return this.plugin.getServer().getOnlinePlayers().stream()
                .filter(targetPLayer -> player == null || player.canSee(targetPLayer))
                .collect(Collectors.toSet());
    }
}
