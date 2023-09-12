package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.MultiverseCommand;
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;
import org.mvplugins.multiverse.core.utils.MVCorei18n;
import org.mvplugins.multiverse.core.worldnew.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.worldnew.WorldManager;
import org.mvplugins.multiverse.core.worldnew.options.UnloadWorldOptions;

@Service
@CommandAlias("mv")
class UnloadCommand extends MultiverseCommand {

    private final WorldManager worldManager;

    private final CommandFlag REMOVE_PLAYERS_FLAG = flag(CommandFlag.builder("--remove-players")
            .addAlias("-r")
            .build());

    private final CommandFlag NO_SAVE_FLAG = flag(CommandFlag.builder("--no-save")
            .addAlias("-n")
            .build());

    @Inject
    UnloadCommand(@NotNull MVCommandManager commandManager, @NotNull WorldManager worldManager) {
        super(commandManager);
        this.worldManager = worldManager;
    }

    @Subcommand("unload")
    @CommandPermission("multiverse.core.unload")
    @CommandCompletion("@mvworlds @flags:groupName=mvunloadcommand")
    @Syntax("<world>")
    @Description("{@@mv-core.unload.description}")
    void onUnloadCommand(
            MVCommandIssuer issuer,

            @Syntax("<world>")
            @Description("{@@mv-core.unload.world.description}")
            LoadedMultiverseWorld world,

            @Optional
            @Syntax("[--remove-players] [--no-save]")
            @Description("{@@mv-core.gamerules.description.page}")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendInfo(MVCorei18n.UNLOAD_UNLOADING, "{world}", world.getAlias());
        UnloadWorldOptions unloadWorldOptions = UnloadWorldOptions.world(world)
                .removePlayers(parsedFlags.hasFlag(REMOVE_PLAYERS_FLAG))
                .saveBukkitWorld(!parsedFlags.hasFlag(NO_SAVE_FLAG));
        worldManager.unloadWorld(unloadWorldOptions)
                .onSuccess(loadedWorld -> {
                    Logging.fine("World unload success: " + loadedWorld);
                    issuer.sendInfo(MVCorei18n.UNLOAD_SUCCESS, "{world}", loadedWorld.getName());
                }).onFailure(failure -> {
                    Logging.fine("World unload failure: " + failure);
                    issuer.sendError(failure.getFailureMessage());
                });
    }
}
