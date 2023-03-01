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
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import org.jetbrains.annotations.NotNull;

@CommandAlias("mv")
public class LoadCommand extends MultiverseCoreCommand {
    public LoadCommand(@NotNull MultiverseCore plugin) {
        super(plugin);
    }

    @Subcommand("load")
    @CommandPermission("multiverse.core.load")
    @CommandCompletion("@mvworlds:scope=unloaded")
    @Syntax("<world>")
    @Description("{@@mv-core.load.description}")
    public void onLoadCommand(BukkitCommandIssuer issuer,

                              @Single
                              @Conditions("validWorldName:scope=unloaded")
                              @Syntax("<world>")
                              @Description("{@@mv-core.load.world.description}")
                              String worldName
    ) {
        issuer.sendInfo(MVCorei18n.LOAD_LOADING,
            "{world}", worldName);

        if (!this.plugin.getMVWorldManager().loadWorld(worldName)) {
            issuer.sendInfo(MVCorei18n.LOAD_FAILED,
                    "{world}", worldName);
            return;
        }
        issuer.sendInfo(MVCorei18n.LOAD_SUCCESS,
                "{world}", worldName);
    }
}
