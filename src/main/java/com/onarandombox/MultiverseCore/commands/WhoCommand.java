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
import com.onarandombox.MultiverseCore.commandTools.display.ColourAlternator;
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
public class WhoCommand extends MultiverseCommand {

    public WhoCommand(MultiverseCore plugin) {
        super(plugin);
    }

    //TODO ACF: Possibly do paging.
    @Subcommand("whoall")
    @CommandPermission("multiverse.core.list.who.all")
    @Syntax("[filter]")
    public void onWhoAllCommand(@NotNull CommandSender sender,
                                @Nullable @Optional Player player,

                                @Syntax("[filter]")
                                @Description("Filter the player names.")
                                @Nullable @Optional String filter) {

        Set<Player> visiblePlayers = getVisiblePlayers(player);

        sender.sendMessage(String.format("%s--- Worlds and their players --- %s%s/%s",
                ChatColor.GOLD, ChatColor.AQUA, visiblePlayers.size(), this.plugin.getServer().getMaxPlayers()));

        this.plugin.getMVWorldManager().getMVWorlds().stream()
                .filter(world -> player == null || this.plugin.getMVPerms().canEnterWorld(player, world))
                .forEach(world -> sender.sendMessage(String.format("%s%s - %s",
                        world.getColoredWorldString(), ChatColor.WHITE, buildPlayerString(world, filter, visiblePlayers))));
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

        ListDisplay display = new ListDisplay(
                this.plugin,
                sender,
                String.format("%s===[ Players in %s%s ]===", ChatColor.AQUA, world.getColoredWorldString(), ChatColor.AQUA),
                buildPlayerList(world, player),
                filter,
                new ColourAlternator()
        );

        display.showContentAsync();
    }

    @NotNull
    private ContentCreator<List<String>> buildPlayerList(@NotNull MultiverseWorld world,
                                                         @Nullable Player player) {

        return () -> {
            Set<Player> visiblePlayers = getVisiblePlayers(player);
            List<String> players = world.getCBWorld().getPlayers().stream()
                .filter(visiblePlayers::contains)
                .map(Player::getDisplayName)
                .collect(Collectors.toList());

            if (players.isEmpty()) {
                players.add("No players found.");
            }

            return players;
        };
    }

    @NotNull
    private Set<Player> getVisiblePlayers(@Nullable Player player) {
        return this.plugin.getServer().getOnlinePlayers().stream()
                .filter(targetPLayer -> player == null || player.canSee(targetPLayer))
                .collect(Collectors.toSet());
    }

    @NotNull
    private String buildPlayerString(@NotNull MultiverseWorld world,
                                     @Nullable String filter,
                                     @NotNull Set<Player> visiblePlayers) {

        String playersInWorld = world.getCBWorld().getPlayers().stream()
                .filter(visiblePlayers::contains)
                .filter(p -> filter == null || p.getDisplayName().contains(filter))
                .map(Player::getDisplayName)
                .collect(Collectors.joining(", "));

        return (playersInWorld.isEmpty()) ? "No players found." : playersInWorld;
    }
}
