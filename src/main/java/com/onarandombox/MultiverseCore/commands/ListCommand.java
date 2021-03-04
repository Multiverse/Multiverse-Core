/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandTools.display.ContentCreator;
import com.onarandombox.MultiverseCore.commandTools.display.page.PageDisplay;
import com.onarandombox.MultiverseCore.commandTools.contexts.PageFilter;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("mv")
public class ListCommand extends MultiverseCommand {

    public ListCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.list.worlds")
    @Syntax("[filter] [page]")
    @Description("Displays a listing of all worlds that you can enter.")
    public void onListCommand(@NotNull CommandSender sender,
                              @Nullable @Optional Player player,
                              @NotNull PageFilter pageFilter) {

        new PageDisplay().withSender(sender)
                .withHeader(String.format("%s====[ Multiverse World List ]====", ChatColor.GOLD))
                .withCreator(getListContents(sender, player))
                .withPageFilter(pageFilter)
                .build()
                .runTaskAsynchronously(this.plugin);
    }

    private ContentCreator<List<String>> getListContents(@NotNull CommandSender sender,
                                           @Nullable @Optional Player player) {
        return () -> {
            List<String> worldList =  new ArrayList<>();
            plugin.getMVWorldManager().getMVWorlds().stream()
                    .filter(world -> player == null || plugin.getMVPerms().canEnterWorld(player, world))
                    .filter(world -> canSeeWorld(player, world))
                    .map(world -> hiddenText(world) + world.getColoredWorldString() + " - " + parseColouredEnvironment(world.getEnvironment()))
                    .sorted()
                    .forEach(worldList::add);

            plugin.getMVWorldManager().getUnloadedWorlds().stream()
                    .filter(world -> plugin.getMVPerms().hasPermission(sender, "multiverse.access." + world, true))
                    .map(world -> ChatColor.GRAY + world + " - UNLOADED")
                    .sorted()
                    .forEach(worldList::add);

            return worldList;
        };
    }

    private boolean canSeeWorld(Player player, MultiverseWorld world) {
        return !world.isHidden()
                || player == null
                || this.plugin.getMVPerms().hasPermission(player, "multiverse.core.modify", true);
    }

    private String hiddenText(MultiverseWorld world) {
        return (world.isHidden()) ? String.format("%s[H] ", ChatColor.GRAY) : "";
    }

    private String parseColouredEnvironment(World.Environment env) {
        ChatColor color = ChatColor.GOLD;
        switch (env) {
            case NETHER:
                color = ChatColor.RED;
                break;
            case NORMAL:
                color = ChatColor.GREEN;
                break;
            case THE_END:
                color = ChatColor.AQUA;
                break;
        }
        return color + env.toString();
    }
}
