package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorld;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class UnloadCommand extends MultiverseCoreCommand {
    public UnloadCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("unload")
    @CommandPermission("multiverse.core.unload")
    @CommandCompletion("@mvworlds")
    @Syntax("<world>")
    @Description("Unloads a world from Multiverse. This does NOT remove the world folder. This does NOT remove it from the config file.")
    public void onUnloadCommand(BukkitCommandIssuer issuer,

                                @Syntax("<world>")
                                @Description("Name of the world you want to unload.")
                                MVWorld world
    ) {
        issuer.sendMessage(String.format("Unloading world '%s'...", world.getColoredWorldString()));

        //TODO API: Should be able to use MVWorld object directly
        if (!this.plugin.getMVWorldManager().unloadWorld(world.getName())) {
            issuer.sendMessage(String.format("Error unloading world '%s'! See console for more details.", world.getColoredWorldString()));
            return;
        }
        issuer.sendMessage(String.format("Unloaded world '%s'!", world.getColoredWorldString()));
    }
}
