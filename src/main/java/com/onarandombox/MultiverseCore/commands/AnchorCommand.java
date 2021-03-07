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
import com.onarandombox.MultiverseCore.commandtools.display.ContentCreator;
import com.onarandombox.MultiverseCore.commandtools.contexts.PageFilter;
import com.onarandombox.MultiverseCore.displaytools.ColorAlternator;
import com.onarandombox.MultiverseCore.displaytools.ContentDisplay;
import com.onarandombox.MultiverseCore.displaytools.ContentFilter;
import com.onarandombox.MultiverseCore.displaytools.DisplayHandlers;
import com.onarandombox.MultiverseCore.displaytools.DisplaySettings;
import com.onarandombox.MultiverseCore.utils.AnchorManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@CommandAlias("mv")
@Subcommand("anchors")
public class AnchorCommand extends MultiverseCoreCommand {

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

                                      @NotNull
                                      @Syntax("<name>")
                                      @Description("Name of your new anchor.")
                                      @Flags("type=anchor name")
                                      @Single String anchorName) {

        player.sendMessage((this.plugin.getAnchorManager().saveAnchorLocation(anchorName, player.getLocation()))

                ? String.format("Anchor '%s%s%s' was successfully %s created!",
                ChatColor.AQUA, anchorName, ChatColor.WHITE, ChatColor.GREEN)

                : String.format("%sThere was an error creating anchor '%s'! Check console for errors.",
                ChatColor.RED, anchorName));

    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.anchor.delete")
    @Syntax("<anchor>")
    @CommandCompletion("@anchors")
    @Description("Delete an existing anchor point.")
    public void onDeleteAnchorCommand(@NotNull CommandSender sender,

                                      @NotNull
                                      @Syntax("<name>")
                                      @Description("Name of anchor you want to delete.")
                                      @Flags("type=anchor name")
                                      @Single String anchorName) {

        sender.sendMessage((this.plugin.getAnchorManager().deleteAnchor(anchorName))

                ? String.format("Anchor '%s%s%s' was successfully %s deleted!",
                ChatColor.AQUA, anchorName, ChatColor.WHITE, ChatColor.RED)

                : String.format("%sThere was an error deleting anchor '%s'! Check console for errors.",
                ChatColor.RED, anchorName));
    }

    @Subcommand("list")
    @CommandPermission("multiverse.core.anchor.list")
    @Syntax("[filter] [page]")
    @Description("View a list of current anchors.")
    public void onListAnchorCommand(@NotNull CommandSender sender,
                                    @NotNull PageFilter pageFilter) {

        new ContentDisplay.Builder<Collection<String>>()
                .sender(sender)
                .header("%s====[ Multiverse Anchor List ]====", ChatColor.LIGHT_PURPLE)
                .contents(buildAnchorList(sender))
                .emptyMessage("You do not any anchors set. See /mv anchors create to get started!")
                .displayHandler(DisplayHandlers.PAGE_LIST)
                .colorTool(ColorAlternator.with(ChatColor.YELLOW, ChatColor.DARK_AQUA))
                .setting(DisplaySettings.SHOW_PAGE, pageFilter.getPage())
                .display();
    }

    private List<String> buildAnchorList(@NotNull CommandSender sender) {
        Set<String> anchors = (sender instanceof Player)
                ? this.anchorManager.getAnchors((Player) sender)
                : this.anchorManager.getAllAnchors();

        List<String> anchorContent = new ArrayList<>();
        for (String anchor : anchors) {
            Location anchorLocation = this.anchorManager.getAnchorLocation(anchor);
            World world = anchorLocation.getWorld(); // this.plugin.getMVWorldManager().getMVWorld();

            String locationString = ChatColor.RED + "!!INVALID!!";
            if (world != null) {
                MultiverseWorld mvWorld = this.plugin.getMVWorldManager().getMVWorld(world);
                locationString = (mvWorld == null)
                        ? String.format("%s%s !!NOT MULTIVERSE WORLD!!", ChatColor.RED, world.getName())
                        : String.format("%s - %s", mvWorld.getColoredWorldString(), this.plugin.getLocationManipulation().strAxis(anchorLocation));
            }
            anchorContent.add(String.format("%s: %s", anchor, locationString));
        }

        return anchorContent;
    }
}
