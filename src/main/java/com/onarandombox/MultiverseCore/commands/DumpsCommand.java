package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandValueFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import com.onarandombox.MultiverseCore.event.MVVersionEvent;
import com.onarandombox.MultiverseCore.utils.MVCorei18n;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteFailedException;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteService;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceFactory;
import com.onarandombox.MultiverseCore.utils.webpaste.PasteServiceType;
import jakarta.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@CommandAlias("mv")
public class DumpsCommand extends MultiverseCommand {

    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;
    private boolean hasArgs = false;

    @Inject
    public DumpsCommand(@NotNull MVCommandManager commandManager,
                        @NotNull MultiverseCore plugin,
                        @NotNull MVWorldManager worldManager) {
        super(commandManager);
        this.plugin = plugin;
        this.worldManager = worldManager;

        registerFlagGroup(CommandFlagGroup.builder("mvdumps")
                .add(CommandValueFlag.builder("--logs", LogsType.class)
                        .addAlias("-l")
                        .context((value) -> {
                            try {
                                return LogsType.valueOf(value.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                throw new InvalidCommandArgument("Invalid logs type " + value + " in --logs");
                            }
                        })
                        .completion(() -> {
                            List<String> types = new ArrayList<>();
                            for (LogsType type : LogsType.values()) {
                                types.add(type.name().toLowerCase());
                            }
                            return types;
                        })
                        .build())
                .add(CommandValueFlag.builder("--upload", Services.class)
                        .addAlias("-u")
                        .context((value) -> {
                            try {
                                return Services.valueOf(value.toUpperCase());
                            } catch (IllegalArgumentException e) {
                                throw new InvalidCommandArgument("Invalid service " + value + " in --upload");
                            }
                        })
                        .completion(() -> {
                            List<String> types = new ArrayList<>();
                            for (Services type : Services.values()) {
                                types.add(type.name().toLowerCase());
                            }
                            return types;
                        })
                        .build())
                .add(CommandFlag.builder("--paranoid")// Does not upload logs or plugin list (except if --logs mclogs is there)
                        .addAlias("-p")
                        .build())
                .build());
    }

    private enum Services {
        PASTEGG,
        PASTESDEV
    }

    private enum LogsType {
        APPEND,
        MCLOGS
    }

    @Subcommand("dumps")
    @CommandPermission("multiverse.core.dumps")
    @CommandCompletion("@flags:groupName=mvdumps")
    @Syntax("--logs [mclogs|append] --upload [pastesdev|pastegg --paranoid")
    @Description("{@@mv-core.dumps.description}")
    public void onDumpsCommand(CommandIssuer issuer,

                               @Optional
                               @Syntax("--logs [mclogs|append] --upload [pastesdev|pastegg] --paranoid")
                               String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        MVVersionEvent versionEvent = new MVVersionEvent();

        this.addDebugInfoToEvent(versionEvent);
        plugin.getServer().getPluginManager().callEvent(versionEvent);

        // Add plugin list if user isn't paranoid
        if (!parsedFlags.hasFlag("--paranoid")) {
            versionEvent.putDetailedVersionInfo("plugins.md", "# Plugins\n\n" + getPluginList());
        }


        // Deal with default case of logs
        LogsType logsType = parsedFlags.flagValue("--logs", LogsType.class);
        if (logsType == null) {
            logsType = LogsType.MCLOGS;
            Logging.finer("Logs was null so set to mclogs");
        }




        // Deal with default case of upload
        Services services = parsedFlags.flagValue("--upload", Services.class);
        if (services == null) {
            services = Services.PASTEGG;
            Logging.finer("Upload was null so set to pastegg");
        }


        // Need to be final for some reason...
        final LogsType finalLogsType = logsType;
        final Services finalServices = services;
        BukkitRunnable logPoster = new BukkitRunnable() {
            @Override
            public void run() {
                HashMap<String, String> pasteURLs = new HashMap<>();
                Logging.finer("Logs type is: " + finalLogsType);
                Logging.finer("Services is: " + finalServices);

                // Deal with logs flag
                if (!parsedFlags.hasFlag("--paranoid")) {
                    switch (finalLogsType) {
                        case MCLOGS -> {
                            issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING_LOGS, "{link}", "https://mclo.gs");
                            pasteURLs.put("Logs", postToService(PasteServiceType.MCLOGS, false, getLogs(), null));
                        }
                        case APPEND -> {
                            versionEvent.putDetailedVersionInfo("latest.log", getLogs());
                        }
                    }
                }

                // Get the files from the event
                final Map<String, String> files = versionEvent.getDetailedVersionInfo();

                // Deal with uploading debug info
                switch (finalServices) {
                    case PASTEGG -> {
                        issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING, "{link}", "https://paste.gg");
                        pasteURLs.put("paste.gg", postToService(PasteServiceType.PASTEGG, true, null, files));
                    }
                    case PASTESDEV -> {
                        issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING, "{link}", "https://pastes.dev");
                        pasteURLs.put("pastes.dev", postToService(PasteServiceType.PASTESDEV, true, null, files));
                    }
                }

                // Finally, loop through and print all URLs
                for (String service : pasteURLs.keySet()) {
                    String link = pasteURLs.get(service);
                    issuer.sendInfo(MVCorei18n.DUMPS_URL_LIST, "{service}", service, "{link}", link);
                }

            }
        };

        // Run the uploader async as it could take some time to upload the debug info
        logPoster.runTaskAsynchronously(plugin);
    }

    /**
     *
     * @return A string containing the latest.log file
     */
    private String getLogs() {

        // Get the Path of latest.log
        Path logsPath = plugin.getServer().getWorldContainer().toPath().resolve("logs").resolve("latest.log");

        // Try to read file
        try {
            return Files.readString(logsPath);
        } catch (IOException e) {
            Logging.warning("Could not read logs/latest.log");
            throw new RuntimeException(e);
        }
    }

    private String getVersionString() {
        return "# Multiverse-Core Version info" + "\n\n"
                + " - Multiverse-Core Version: " + this.plugin.getDescription().getVersion() + '\n'
                + " - Bukkit Version: " + this.plugin.getServer().getVersion() + '\n'
                + " - Loaded Worlds: " + worldManager.getMVWorlds() + '\n'
                + " - Multiverse Plugins Loaded: " + this.plugin.getPluginCount() + '\n';
    }

    private void addDebugInfoToEvent(MVVersionEvent event) {

        // Add the legacy file, but as markdown, so it's readable
        event.putDetailedVersionInfo("version.md", this.getVersionString());

        // add config.yml
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        event.putDetailedVersionInfo("multiverse-core/config.yml", configFile);

        // add worlds.yml
        File worldsFile = new File(plugin.getDataFolder(), "worlds.yml");
        event.putDetailedVersionInfo("multiverse-core/worlds.yml", worldsFile);

        // Add bukkit.yml if we found it
        if (plugin.getBukkitConfig() != null) {
            event.putDetailedVersionInfo(plugin.getBukkitConfig().getPath(), plugin.getBukkitConfig());
        } else {
            Logging.warning("/mv version could not find bukkit.yml. Not including file");
        }

        // Add server.properties if we found it
        if (plugin.getServerProperties() != null) {
            event.putDetailedVersionInfo(plugin.getServerProperties().getPath(), plugin.getServerProperties());
        } else {
            Logging.warning("/mv version could not find server.properties. Not including file");
        }

    }

    private String getPluginList() {
        return " - " + StringUtils.join(plugin.getServer().getPluginManager().getPlugins(), "\n - ");
    }

    /**
     * Turns a list of files in to a string containing askii art
     * @param files Map of filenames/contents
     * @return The askii art
     */
    private String encodeAsString(Map<String, String> files) {
        StringBuilder uploadData = new StringBuilder();
        for (String file : files.keySet()) {
            String data = files.get(file);
            uploadData.append("# ---------- ")
                    .append(file)
                    .append(" ----------\n\n")
                    .append(data)
                    .append("\n\n");
        }

        return uploadData.toString();
    }

    /**
     * Send the current contents of this.pasteBinBuffer to a web service.
     *
     * @param type Service type to send paste data to.
     * @param isPrivate Should the paste be marked as private.
     * @param rawPasteData Legacy string containing only data to post to a service.
     * @param pasteFiles Map of filenames/contents of debug info.
     * @return URL of visible paste
     */
    private String postToService(@NotNull PasteServiceType type, boolean isPrivate, @Nullable String rawPasteData, @Nullable Map<String, String> pasteFiles) {
        PasteService pasteService = PasteServiceFactory.getService(type, isPrivate);

        try {
            // Upload normally when multi file is supported
            if (pasteService.supportsMultiFile()) {
                return pasteService.postData(pasteFiles);
            }

            // When there is raw paste data, use that
            if (rawPasteData != null) { // For the logs
                return pasteService.postData(rawPasteData);
            }

            // If all we have are files and the paste service does not support multi file then encode them
            if (pasteFiles != null) {
                return pasteService.postData(this.encodeAsString(pasteFiles));
            }

            // Should never get here
            return "No data specified in code";

        } catch (PasteFailedException e) {
            e.printStackTrace();
            return "Error posting to service.";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "That service isn't supported yet.";
        }
    }
}
