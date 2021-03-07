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
import com.onarandombox.MultiverseCore.commandtools.contexts.PageFilter;
import com.onarandombox.MultiverseCore.displaytools.ContentDisplay;
import com.onarandombox.MultiverseCore.displaytools.ContentFilter;
import com.onarandombox.MultiverseCore.displaytools.DisplayHandlers;
import com.onarandombox.MultiverseCore.displaytools.DisplaySetting;
import com.onarandombox.MultiverseCore.displaytools.DisplaySettings;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
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

                                @NotNull
                                @Syntax("[filter] [page]")
                                @Description("Filter and paging.")
                                PageFilter pageFilter) {

        Player player = (sender instanceof Player) ? (Player) sender : null;
        Set<Player> visiblePlayers = getVisiblePlayers(player);

        new ContentDisplay.Builder<Collection<String>>()
                .sender(sender)
                .header("%s--- Worlds and their players --- %s%s/%s", ChatColor.GOLD, ChatColor.AQUA, visiblePlayers.size(), this.plugin.getServer().getMaxPlayers())
                .contents(buildAllWorlds(player, visiblePlayers))
                .displayHandler(DisplayHandlers.PAGE_LIST)
                .filter(pageFilter.getFilter())
                .setting(DisplaySettings.SHOW_PAGE, pageFilter.getPage())
                .display();
    }

    @Subcommand("who")
    @CommandPermission("multiverse.core.list.who")
    @Syntax("[world] [filter]")
    @CommandCompletion("@MVWorlds")
    public void onWhoCommand(@NotNull CommandSender sender,
                             @Nullable @Optional Player player,

                             @NotNull
                             @Syntax("[world]")
                             @Description("World to show player list.")
                             @Flags("other,defaultself,fallbackself")
                             @Conditions("hasWorldAccess") MultiverseWorld world,

                             @NotNull
                             @Syntax("[filter]")
                             @Description("Filter the player names.")
                             ContentFilter filter) {

        Set<Player> visiblePlayers = getVisiblePlayers(player);

        new ContentDisplay.Builder<Collection<String>>()
                .sender(sender)
                .header("%s===[ Players in %s%s ]===", ChatColor.AQUA, world.getColoredWorldString(), ChatColor.AQUA)
                .contents(buildPlayerList(world, visiblePlayers))
                .emptyMessage("%sNo players found.", ChatColor.GRAY)
                .displayHandler(DisplayHandlers.PAGE_LIST)
                //TODO: Filter
                .display();
    }

    private List<String> buildAllWorlds(@Nullable Player player,
                                        @NotNull Set<Player> visiblePlayers) {

        return this.plugin.getMVWorldManager().getMVWorlds().stream()
                .filter(world -> player == null || this.plugin.getMVPerms().canEnterWorld(player, world))
                .map(world -> getPLayersInWorld(world, visiblePlayers))
                .collect(Collectors.toList());
    }

    private String getPLayersInWorld(@NotNull MultiverseWorld world,
                                     @NotNull Set<Player> visiblePlayers) {

        return String.format("%s%s - %s",
                world.getColoredWorldString(), ChatColor.WHITE, buildPlayerList(world, visiblePlayers));
    }

    @NotNull
    private List<String> buildPlayerList(@NotNull MultiverseWorld world,
                                         @NotNull Set<Player> visiblePlayers) {

        return world.getCBWorld().getPlayers().stream()
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
