package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.MultiverseCore;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class LoadCommand extends MultiverseCommand {
    public LoadCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("load")
    @CommandPermission("multiverse.core.load")
    @CommandCompletion("@mvworlds:scope=unloaded")
    @Syntax("<world>")
    @Description("Loads a world. World must be already in worlds.yml, else please use /mv import.")
    public void onLoadCommand(BukkitCommandIssuer issuer,

                              @Single
                              @Conditions("worldname:scope=unloaded")
                              @Syntax("<world>")
                              @Description("Name of world you want to load.")
                              String worldName
    ) {
        issuer.sendMessage(String.format("Loading world '%s'...", worldName));

        if (!this.plugin.getMVWorldManager().loadWorld(worldName)) {
            issuer.sendMessage(String.format("Error trying to load world '%s'!", worldName));
            return;
        }
        issuer.sendMessage(String.format("Loaded world '%s'!", worldName));
    }
}
