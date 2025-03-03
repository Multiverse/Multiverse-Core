package org.mvplugins.multiverse.core.commands;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.dumptruckman.minecraft.util.Logging;
import jakarta.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.MultiversePluginsRegistration;
import org.mvplugins.multiverse.core.commands.DumpsLogPoster.LogsType;
import org.mvplugins.multiverse.core.commands.DumpsLogPoster.UploadType;
import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.event.MVDumpsDebugInfoEvent;
import org.mvplugins.multiverse.core.utils.FileUtils;
import org.mvplugins.multiverse.core.utils.StringFormatter;
import org.mvplugins.multiverse.core.world.WorldManager;

@Service
class DumpsService {

    private final MultiverseCore plugin;
    private final WorldManager worldManager;
    private final FileUtils fileUtils;

    @Inject
    DumpsService(@NotNull MultiverseCore plugin,
                 @NotNull WorldManager worldManager,
                 @NotNull FileUtils fileUtils) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.fileUtils = fileUtils;
    }

    void postLogs(MVCommandIssuer issuer, LogsType logsType, UploadType servicesType, boolean paranoid) {
        // Initialise and add info to the debug event
        MVDumpsDebugInfoEvent versionEvent = createAndCallDebugInfoEvent();

        // Add plugin list if user isn't paranoid
        if (!paranoid) {
            versionEvent.putDetailedDebugInfo("plugins.md", "# Plugins\n\n" + getPluginList());
        }

        new DumpsLogPoster(issuer, logsType, servicesType, paranoid, getLogs(), versionEvent)
                .runTaskAsynchronously(plugin);
    }

    /**
     * Get the contents of the latest.log file.
     *
     * @return A string containing the latest.log file
     */
    private @NotNull String getLogs() {
        // Get the Path of latest.log
        Path logsPath = fileUtils.getServerFolder().toPath().resolve("logs/latest.log");
        File logsFile = logsPath.toFile();

        if (!logsFile.exists()) {
            Logging.warning("Could not read logs/latest.log");
            return "Could not find log";
        }

        return readLogsFromFile(logsPath);
    }

    private @NotNull String readLogsFromFile(Path logsPath) {
        String logs = "Could not read log";

        // Try reading as ANSI encoded
        try {
            logs = Files.readString(logsPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Logging.severe("Could not read logs/latest.log. See below for stack trace");
            e.printStackTrace();

            try {
                logs = Files.readString(logsPath, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                // It is some other strange encoding
                Logging.severe("Could not read ./logs/latest.log. See below for stack trace");
                ex.printStackTrace();
            }
        }

        return logs;
    }

    private String getDebugInfoString() {
        return "# Multiverse-Core Version info" + "\n\n"
                + " - Multiverse-Core Version: " + this.plugin.getDescription().getVersion() + '\n'
                + " - Bukkit Version: " + this.plugin.getServer().getVersion() + '\n'
                + " - Loaded Worlds: " + worldManager.getLoadedWorlds() + '\n'
                + " - Multiverse Plugins Loaded: " + StringFormatter.joinAnd(MultiversePluginsRegistration.get().getRegisteredPlugins()) + '\n'
                + " - Multiverse Plugins Count: " + MultiversePluginsRegistration.get().getPluginCount() + '\n';
    }

    private MVDumpsDebugInfoEvent createAndCallDebugInfoEvent() {
        MVDumpsDebugInfoEvent event = new MVDumpsDebugInfoEvent();
        addDebugInfoToEvent(event);
        plugin.getServer().getPluginManager().callEvent(event);
        return event;
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

    String getPluginList() {
        return " - " + StringUtils.join(plugin.getServer().getPluginManager().getPlugins(), "\n - ");
    }
}
