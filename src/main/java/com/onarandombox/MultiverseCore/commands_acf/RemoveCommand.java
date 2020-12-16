package com.onarandombox.MultiverseCore.commands_acf;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class RemoveCommand extends MultiverseCommand {

    public RemoveCommand(MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("remove")
    @CommandPermission("multiverse.core.spawn.other")
    @CommandCompletion("@MVWorlds|@unloadedWorlds")
    @Syntax("<world>")
    @Description("Unloads a world from Multiverse and removes it from worlds.yml, this does NOT DELETE the world folder.")
    public void onRemoveCommand(@NotNull CommandSender sender,
                                @NotNull @Flags("other") MultiverseWorld world) {

        String resultMessage = (this.plugin.getMVWorldManager().removeWorldFromConfig(world.getName()))
                ? "World removed from config!"
                : "Error trying to remove world from config!";

        sender.sendMessage(resultMessage);
    }
}
