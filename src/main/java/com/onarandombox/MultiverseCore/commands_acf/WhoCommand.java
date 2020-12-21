package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;

@CommandAlias("mv")
public class WhoCommand extends MultiverseCommand {

    public WhoCommand(MultiverseCore plugin) {
        super(plugin);
    }

    //TODO: Possible do paging.
    @Subcommand("whoall")
    @CommandPermission("multiverse.core.list.who.all")
    @Syntax("[filter]")
    public void onWhoAllCommand(@NotNull CommandSender sender,
                                @Nullable @Optional Player player,
                                @Nullable @Optional String filter) {

        final Set<Player> visiblePlayers = getVisiblePlayers(player);

        sender.sendMessage(ChatColor.AQUA + "--- Worlds and their players --- "
                + visiblePlayers.size() + "/" + this.plugin.getServer().getMaxPlayers());

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
                             @NotNull @Flags("other,defaultself,fallbackself") @Conditions("hasWorldAccess") MultiverseWorld world,
                             @Nullable @Optional String filter) {

        final Set<Player> visiblePlayers = getVisiblePlayers(player);

        sender.sendMessage(String.format("%s--- Players in %s%s ---", ChatColor.AQUA,
                world.getColoredWorldString(), ChatColor.AQUA));

        sender.sendMessage(buildPlayerString(world, filter, visiblePlayers));
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
