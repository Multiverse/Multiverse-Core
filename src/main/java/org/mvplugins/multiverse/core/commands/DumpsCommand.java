package org.mvplugins.multiverse.core.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.commands.DumpsLogPoster.LogsType;
import org.mvplugins.multiverse.core.commands.DumpsLogPoster.UploadType;
import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.flag.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.event.MVDumpsDebugInfoEvent;
import org.mvplugins.multiverse.core.utils.FileUtils;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
@CommandAlias("mv")
final class DumpsCommand extends CoreCommand {

    private final MultiverseCore plugin;
    private final WorldManager worldManager;
    private final FileUtils fileUtils;

    private final CommandValueFlag<LogsType> logsFlag = flag(CommandValueFlag
            .enumBuilder("--logs", LogsType.class)
            .addAlias("-l")
            .build());

    private final CommandValueFlag<UploadType> uploadFlag = flag(CommandValueFlag
            .enumBuilder("--upload", UploadType.class)
            .addAlias("-u")
            .build());

    // Does not upload logs or plugin list (except if --logs mclogs is there)
    private final CommandFlag paranoidFlag = flag(CommandFlag.builder("--paranoid")
            .addAlias("-p")
            .build());

    @Inject
    DumpsCommand(@NotNull MVCommandManager commandManager,
                        @NotNull MultiverseCore plugin,
                        @NotNull WorldManager worldManager,
                        @NotNull FileUtils fileUtils) {
        super(commandManager);
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.fileUtils = fileUtils;
    }

    @Subcommand("dumps")
    @CommandPermission("multiverse.core.dumps")
    @CommandCompletion("@flags:groupName=mvdumpscommand")
    @Syntax("[--logs <mclogs | append>] [--upload <pastesdev | pastegg>] [--paranoid]")
    @Description("{@@mv-core.dumps.description}")
    void onDumpsCommand(
            MVCommandIssuer issuer,

            @Optional
            @Syntax("[--logs <mclogs | append>] [--upload <pastesdev | pastegg>] [--paranoid]")
            String[] flags) {
        final ParsedCommandFlags parsedFlags = parseFlags(flags);

        // Grab all our flags
        final boolean paranoid = parsedFlags.hasFlag(paranoidFlag);
        final LogsType logsType = parsedFlags.flagValue(logsFlag, LogsType.MCLOGS);
        final UploadType servicesType = parsedFlags.flagValue(uploadFlag, UploadType.PASTESDEV);

        // Initialise and add info to the debug event
        MVDumpsDebugInfoEvent versionEvent = new MVDumpsDebugInfoEvent();
        this.addDebugInfoToEvent(versionEvent);
        plugin.getServer().getPluginManager().callEvent(versionEvent);

        // Add plugin list if user isn't paranoid
        if (!paranoid) {
            versionEvent.putDetailedDebugInfo("plugins.md", "# Plugins\n\n" + getPluginList());
        }

        new DumpsLogPoster(issuer, logsType, servicesType, paranoid, getLogs(), versionEvent)
                .runTaskAsynchronously(plugin);
    }

    /**
     * Get the contents of the latest.log file
     *
     * @return A string containing the latest.log file
     */
    private String getLogs() {
        // Get the Path of latest.log
        Path logsPath = fileUtils.getServerFolder().toPath().resolve("logs/latest.log");
        File logsFile = logsPath.toFile();

        if (!logsFile.exists()) {
            Logging.warning("Could not read logs/latest.log");
            return "Could not find log";
        }

        // Try reading as ANSI encoded
        try {
            return Files.readString(logsPath, StandardCharsets.ISO_8859_1);
        } catch (IOException e) {
            Logging.finer("Log is not ANSI encoded. Trying UTF-8");
            // Must be a UTF-8 encoded log then
        }

        // Try reading as UTF-8 encoded
        try {
            return Files.readString(logsPath, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            // It is some other strange encoding
            Logging.severe("Could not read ./logs/latest.log. See below for stack trace");
            ex.printStackTrace();
        }
        return "Could not read log";
    }

    private String getDebugInfoString() {
        return "# Multiverse-Core Version info" + "\n\n"
                + " - Multiverse-Core Version: " + this.plugin.getDescription().getVersion() + '\n'
                + " - Bukkit Version: " + this.plugin.getServer().getVersion() + '\n'
                + " - Loaded Worlds: " + worldManager.getLoadedWorlds() + '\n'
                + " - Multiverse Plugins Loaded: " + this.plugin.getPluginCount() + '\n';
    }

    private void addDebugInfoToEvent(MVDumpsDebugInfoEvent event) {
        // Add the legacy file, but as markdown, so it's readable
        event.putDetailedDebugInfo("version.md", this.getDebugInfoString());

        // add config.yml
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        event.putDetailedDebugInfo("Multiverse-Core/config.yml", configFile);

        // add worlds.yml
        File worldsFile = new File(plugin.getDataFolder(), "worlds.yml");
        event.putDetailedDebugInfo("Multiverse-Core/worlds.yml", worldsFile);

        // Add bukkit.yml if we found it
        if (fileUtils.getBukkitConfig() != null) {
            event.putDetailedDebugInfo(fileUtils.getBukkitConfig().getPath(), fileUtils.getBukkitConfig());
        } else {
            Logging.warning("/mv dumps could not find bukkit.yml. Not including file");
        }

        // Add server.properties if we found it
        if (fileUtils.getServerProperties() != null) {
            event.putDetailedDebugInfo(fileUtils.getServerProperties().getPath(), fileUtils.getServerProperties());
        } else {
            Logging.warning("/mv dumps could not find server.properties. Not including file");
        }

    }

    private String getPluginList() {
        return " - " + StringUtils.join(plugin.getServer().getPluginManager().getPlugins(), "\n - ");
    }
}
