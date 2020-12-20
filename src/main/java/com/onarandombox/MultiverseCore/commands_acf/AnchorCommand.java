package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.AnchorManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public void onCreateAnchorCommand(@NotNull @Flags("onlyself") Player player,
                                      @NotNull @Flags("type=anchor name") String anchorName) {

        player.sendMessage((this.plugin.getAnchorManager().saveAnchorLocation(anchorName, player.getLocation()))
                ? "Anchor '" + anchorName + "' was successfully " + ChatColor.GREEN + "created!"
                : "Anchor '" + anchorName + "' was " + ChatColor.RED + " NOT " + ChatColor.WHITE + "created!");
    }

    @Subcommand("delete")
    @CommandPermission("multiverse.core.anchor.delete")
    @Syntax("<anchor>")
    @Description("Delete an existing anchor point.")
    public void onDeleteAnchorCommand(@NotNull CommandSender sender,
                                      @NotNull @Flags("type=anchor name") String anchorName) {

        sender.sendMessage((this.plugin.getAnchorManager().deleteAnchor(anchorName))
                ? "Anchor '" + anchorName + "' was successfully " + ChatColor.RED + "deleted!"
                : "Anchor '" + anchorName + "' was " + ChatColor.RED + " not " + ChatColor.WHITE + "deleted!");
    }

    //TODO: Filtering and paging
    @Subcommand("list")
    @CommandPermission("multiverse.core.anchor.list")
    @Syntax("[filter]")
    @Description("Delete an existing anchor point.")
    public void onListAnchorCommand(@NotNull CommandSender sender,
                                    @Nullable @Optional String filter) {

        Set<String> anchors = (sender instanceof Player)
                ? this.anchorManager.getAnchors((Player) sender)
                : this.anchorManager.getAllAnchors();

        sender.sendMessage(ChatColor.LIGHT_PURPLE + "====[ Multiverse Anchor List ]====");
        sender.sendMessage(String.join(", ", anchors));
    }
}
