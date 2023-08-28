package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
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
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import com.onarandombox.MultiverseCore.config.MVCoreConfig;
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
import java.util.HashMap;
import java.util.Map;

@Service
@CommandAlias("mv")
public class DumpsCommand extends MultiverseCommand {

    private final MVCoreConfig config;
    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;
    private boolean hasArgs = false;

    @Inject
    public DumpsCommand(@NotNull MVCommandManager commandManager,
                        @NotNull MultiverseCore plugin,
                        @NotNull MVCoreConfig config,
                        @NotNull MVWorldManager worldManager) {
        super(commandManager);
        this.config = config;
        this.plugin = plugin;
        this.worldManager = worldManager;

        registerFlagGroup(CommandFlagGroup.builder("mvdumps")
                .add(CommandFlag.builder("--pastebincom")
                        .addAlias("-b")
                        .build())
                .add(CommandFlag.builder("--pastesdev")
                        .addAlias("-d")
                        .build())
                .add(CommandFlag.builder("--pastegg")
                        .addAlias("-p")
                        .build())
                .add(CommandFlag.builder("--logs")
                        .addAlias("-l")
                        .build())
                .add(CommandFlag.builder("--exclude-plugin-list")
                        .addAlias("-l")
                        .build())
                .build());
    }

    @Subcommand("dumps")
    @CommandPermission("multiverse.core.dumps")
    @CommandCompletion("@flags:groupName=mvdumps")
    @Syntax("--pastebincom --pastesdev --pastegg --logs --exclude-plugin-list")
    @Description("{@@mv-core.dumps.description}")
    public void onDumpsCommand(CommandIssuer issuer,

                               @Optional
                               @Syntax("--pastebincom --pastesdev --pastegg --logs --exclude-plugin-list")
                               @Description("{@@mv-core.dumps.flags.description}")
                               String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        MVVersionEvent versionEvent = new MVVersionEvent();

        this.addDebugInfoToEvent(versionEvent);
        plugin.getServer().getPluginManager().callEvent(versionEvent);

        if (!parsedFlags.hasFlag("--exclude-plugin-list")) {
            versionEvent.putDetailedVersionInfo("plugins.md", "# Plugins\n\n" + getPluginList());
        }

        final Map<String, String> files = versionEvent.getDetailedVersionInfo();

        BukkitRunnable logPoster = new BukkitRunnable() {
            @Override
            public void run() {
                HashMap<String, String> pasteURLs = new HashMap<>();

                if (parsedFlags.hasFlag("--pastebincom")) {
                    hasArgs = true;
                    issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING, "{link}", "https://pastebin.com");
                    pasteURLs.put("pastebin.com", postToService(PasteServiceType.PASTEBIN, true, null, files));
                }

                if (parsedFlags.hasFlag("--pastesdev")) {
                    hasArgs = true;
                    issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING, "{link}", "https://pastes.dev");
                    pasteURLs.put("pastes.dev", postToService(PasteServiceType.PASTESDEV, true, null, files));
                }

                if (parsedFlags.hasFlag("--logs")) {
                    hasArgs = true;
                    issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING_LOGS, "{link}", "https://mclo.gs");
                    pasteURLs.putAll(uploadLogs());
                }

                if (parsedFlags.hasFlag("--pastegg")) {
                    issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING, "{link}", "https://paste.gg");
                    pasteURLs.put("paste.gg", postToService(PasteServiceType.PASTEGG, true, null, files));
                }

                // Fallback to paste.gg and logs if no other sites where specified
                if (!hasArgs) {
                    issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING, "{link}", "https://paste.gg");
                    pasteURLs.put("paste.gg", postToService(PasteServiceType.PASTEGG, true, null, files));

                    issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING_LOGS, "{link}", "https://mclo.gs");
                    pasteURLs.putAll(uploadLogs());
                }

                // Finally, loop through and print all URLs
                for (String service : pasteURLs.keySet()) {
                    String link = pasteURLs.get(service);
                    issuer.sendInfo(MVCorei18n.DUMPS_URL_LIST, "{service}", service, "{link}", link);
                }

            }
        };

        // Run async as it could take some time to upload the debug info
        logPoster.runTaskAsynchronously(plugin);
    }

    private HashMap<String, String> uploadLogs() {
        HashMap<String, String> outMap = new HashMap<>();



        // Get the Path of latest.log
        Path logsPath = plugin.getServer().getWorldContainer().toPath().resolve("logs").resolve("latest.log");

        String logs;
        // Try to read file
        try {
            logs = Files.readString(logsPath);
        } catch (IOException e) {
            Logging.warning("Could not read logs/latest.log");
            throw new RuntimeException(e);
        }

        outMap.put("Logs", postToService(PasteServiceType.MCLOGS, false, logs, null));
        return outMap;
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
