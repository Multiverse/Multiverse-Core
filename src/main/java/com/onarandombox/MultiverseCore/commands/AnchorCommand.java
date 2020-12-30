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
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseCore.commandTools.display.ColourAlternator;
import com.onarandombox.MultiverseCore.commandTools.display.PageDisplay;
import com.onarandombox.MultiverseCore.commandTools.PageFilter;
import com.onarandombox.MultiverseCore.utils.AnchorManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@CommandAlias("mv")
@Subcommand("anchors")
public class AnchorCommand extends MultiverseCommand {

    private final AnchorManager anchorManager;

    public AnchorCommand(MultiverseCore plugin) {
        super(plugin);
        this.anchorManager = plugin.getAnchorManager();
    }

    @Subcommand("create")
    @CommandPermission("multiverse.core.anchor.create")
    @Syntax("<name>")
    @Description("Create a new anchor point.")
    public void onCreateAnchorCommand(@NotNull Player player,

                                      @Syntax("<name>")
                                      @Description("Name of your new anchor.")
                                      @NotNull @Single @Flags("type=anchor name") String anchorName) {

        player.sendMessage((this.plugin.getAnchorManager().saveAnchorLocation(anchorName, player.getLocation()))
                ? "Anchor '" + anchorName + "' was successfully " + ChatColor.GREEN + "created!"
                : "Anchor '" + anchorName + "' was " + ChatColor.RED + " NOT " + ChatColor.WHITE + "created!");
    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.anchor.delete")
    @Syntax("<anchor>")
    @CommandCompletion("@anchors")
    @Description("Delete an existing anchor point.")
    public void onDeleteAnchorCommand(@NotNull CommandSender sender,

                                      @Syntax("<name>")
                                      @Description("Name of anchor you want to delete.")
                                      @NotNull @Single @Flags("type=anchor name") String anchorName) {

        sender.sendMessage((this.plugin.getAnchorManager().deleteAnchor(anchorName))
                ? "Anchor '" + anchorName + "' was successfully " + ChatColor.RED + "deleted!"
                : "Anchor '" + anchorName + "' was " + ChatColor.RED + " not " + ChatColor.WHITE + "deleted!");
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.anchor.list")
    @Syntax("[filter] [page]")
    @Description("Delete an existing anchor point.")
    public void onListAnchorCommand(@NotNull CommandSender sender,
                                    @NotNull PageFilter pageFilter) {

        Set<String> anchors = (sender instanceof Player)
                ? this.anchorManager.getAnchors((Player) sender)
                : this.anchorManager.getAllAnchors();

        List<String> anchorContent = new ArrayList<>();
        for (String anchor : anchors) {
            Location anchorLocation = this.anchorManager.getAnchorLocation(anchor);
            World world = anchorLocation.getWorld(); // this.plugin.getMVWorldManager().getMVWorld();

            String locationString = ChatColor.RED + "!!INVALID!!";
            if (world != null) {
                MultiverseWorld mvworld = this.plugin.getMVWorldManager().getMVWorld(world);
                locationString = (mvworld == null)
                        ? ChatColor.RED + world.getName() + "!!NOT MULTIVERSE WORLD!!"
                        : mvworld.getColoredWorldString() + " - " + this.plugin.getLocationManipulation().strAxis(anchorLocation);
            }
            anchorContent.add(anchor + ": " + locationString);
        }

        PageDisplay pageDisplay = new PageDisplay(
                sender,
                ChatColor.LIGHT_PURPLE + "====[ Multiverse Anchor List ]====",
                anchorContent,
                pageFilter,
                new ColourAlternator(ChatColor.YELLOW, ChatColor.DARK_AQUA)
        );

        pageDisplay.showContentAsync(this.plugin);
    }
}
