package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commands_helper.PageDisplay;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("mv")
public class ListCommand extends MultiverseCommand {

    public ListCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.list.worlds")
    @Syntax("[page]")
    @Description("Displays a listing of all worlds that you can enter.")
    public void onListCommand(@NotNull CommandSender sender,
                              @Nullable @Optional Player player,
                              @Default("1") int page) {

        //TODO: Hidden and unloaded
        List<String> worldList =  new ArrayList<>();
        this.plugin.getMVWorldManager().getMVWorlds().stream()
                .filter(world -> player == null || this.plugin.getMVPerms().canEnterWorld(player, world))
                .filter(world -> canSeeHidden(player, world))
                .map(world -> hiddenText(world) + world.getColoredWorldString() + " - " + parseColouredEnvironment(world.getEnvironment()))
                .sorted()
                .forEach(worldList::add);

        this.plugin.getMVWorldManager().getUnloadedWorlds().stream()
                .filter(world -> this.plugin.getMVPerms().hasPermission(sender, "multiverse.access." + world, true))
                .map(world -> ChatColor.GRAY + world + " - UNLOADED")
                .sorted()
                .forEach(worldList::add);

        PageDisplay pageDisplay = new PageDisplay(
                sender,
                ChatColor.LIGHT_PURPLE + "====[ Multiverse World List ]====",
                worldList,
                page
        );

        pageDisplay.showPageAsync(this.plugin);
    }

    private boolean canSeeHidden(Player player, MultiverseWorld world) {
        return !world.isHidden() || player == null || this.plugin.getMVPerms().hasPermission(player, "multiverse.core.modify", true);
    }

    private String hiddenText(MultiverseWorld world) {
        return (world.isHidden()) ? ChatColor.GRAY + "[H] " : "";
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
