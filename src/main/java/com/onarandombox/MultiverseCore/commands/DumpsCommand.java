package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.CommandIssuer;
import co.aikar.commands.CommandManager;
import co.aikar.commands.MessageType;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.locales.MessageKeyProvider;
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
import org.bukkit.entity.Player;
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
    private final MVCommandManager commandManager;

    @Inject
    public DumpsCommand(@NotNull MVCommandManager commandManager,
                        @NotNull MultiverseCore plugin,
                        @NotNull MVCoreConfig config,
                        @NotNull MVWorldManager worldManager) {
        super(commandManager);
        this.config = config;
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.commandManager = commandManager;

        registerFlagGroup(CommandFlagGroup.builder("mvdumps")
                .add(CommandFlag.builder("--pastebincom")
                        .addAlias("-b")
                        .build())
                .add(CommandFlag.builder("--github")
                        .addAlias("-g")
                        .build())
                .add(CommandFlag.builder("--hastebin")
                        .addAlias("-h")
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
    @Syntax("--pastebincom --hastebin --pastegg --logs --exclude-plugin-list")
    @Description("{@@mv-core.dumps.description}")
    public void onDumpsCommand(CommandIssuer issuer,

                               @Optional
                               @Syntax("--pastebincom --hastebin --pastegg --logs --exclude-plugin-list")
                               @Description("{@@mv-core.dumps.flags.description}")
                               String[] flags
    ) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        MVVersionEvent versionEvent = new MVVersionEvent();

        this.addDebugInfoToEvent(versionEvent);
        plugin.getServer().getPluginManager().callEvent(versionEvent);

        final String versionInfo = versionEvent.getVersionInfo();

        if (!parsedFlags.hasFlag("--exclude-plugin-list")) {
            versionEvent.putDetailedVersionInfo("plugins.md", "# Plugins\n\n" + getPluginList());
        }

        final Map<String, String> files = versionEvent.getDetailedVersionInfo();

        BukkitRunnable logPoster = new BukkitRunnable() {
            @Override
            public void run() {
                HashMap<String, String> pasteURLs = new HashMap<>();
                boolean hasArgs = false;

                if (parsedFlags.hasFlag("--pastebincom")) {
                    hasArgs = true;
                    issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING, "{link}", "https://pastebin.com");
                    pasteURLs.put("pastebin.com", postToService(PasteServiceType.PASTEBIN, true, versionInfo, files));
                }

                if (parsedFlags.hasFlag("--hastebin")) {
                    hasArgs = true;
                    issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING, "{link}", "need to yeet"); //TODO yeet it
                    pasteURLs.put("hastebin.com", postToService(PasteServiceType.HASTEBIN, true, versionInfo, files));
                }

                if (parsedFlags.hasFlag("--logs")) {
                    hasArgs = true;
                    issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING_LOGS, "{link}", "https://mclo.gs");

                    // Get the Path of latest.log
                    Path logsPath = plugin.getServer().getWorldContainer().toPath().resolve("logs").resolve("latest.log");

                    String logs;
                    // Try to read file
                    try {
                        logs = Files.readString(logsPath);
                    } catch (IOException e) {
                        logs = "Could not read logs/latest.log";
                        Logging.warning("Could not read logs/latest.log");
                        throw new RuntimeException(e);
                    }

                    pasteURLs.put("Logs", postToService(PasteServiceType.MCLOGS, false, logs, null));
                }

                // Fallback to paste.gg if no other sites where specified
                if (parsedFlags.hasFlag("--pastegg") || !hasArgs) {
                    issuer.sendInfo(MVCorei18n.DUMPS_UPLOADING, "{link}", "https://paste.gg");
                    pasteURLs.put("paste.gg", postToService(PasteServiceType.PASTEGG, true, versionInfo, files));
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
     * Send the current contents of this.pasteBinBuffer to a web service.
     *
     * @param type       Service type to send paste data to.
     * @param isPrivate  Should the paste be marked as private.
     * @param pasteData  Legacy string only data to post to a service.
     * @param pasteFiles Map of filenames/contents of debug info.
     * @return URL of visible paste
     */
    private static String postToService(PasteServiceType type, boolean isPrivate, @Nullable String pasteData, @Nullable Map<String, String> pasteFiles) {
        PasteService pasteService = PasteServiceFactory.getService(type, isPrivate);

        try {
            String result;
            if (pasteService.supportsMultiFile()) {
                result = pasteService.postData(pasteFiles);
            } else {
                result = pasteService.postData(pasteData);
            }

            return result;
        } catch (PasteFailedException e) {
            e.printStackTrace();
            return "Error posting to service.";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "That service isn't supported yet.";
        }
    }
}
