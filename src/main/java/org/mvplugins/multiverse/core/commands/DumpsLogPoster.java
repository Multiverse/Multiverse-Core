package org.mvplugins.multiverse.core.commands;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.event.MVDumpsDebugInfoEvent;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.webpaste.PasteFailedException;
import org.mvplugins.multiverse.core.utils.webpaste.PasteService;
import org.mvplugins.multiverse.core.utils.webpaste.PasteServiceFactory;
import org.mvplugins.multiverse.core.utils.webpaste.PasteServiceType;

final class DumpsLogPoster extends BukkitRunnable {

    enum UploadType {
        // BEGIN CHECKSTYLE-SUPPRESSION: JavadocVariable
        // PASTEBIN(PasteServiceType.PASTEBIN, "pastebin.com"),
        PASTESDEV(PasteServiceType.PASTESDEV, "pastes.dev"),
        MCLOGS(PasteServiceType.MCLOGS, "mclo.gs"),
        ;
        // END CHECKSTYLE-SUPPRESSION: JavadocVariable

        private final PasteServiceType pasteServiceType;
        private final String serviceName;

        UploadType(PasteServiceType pasteServiceType, String serviceName) {
            this.pasteServiceType = pasteServiceType;
            this.serviceName = serviceName;
        }
    }

    @NotNull
    private final MultiverseCore plugin;
    private final MVCommandIssuer issuer;
    private final UploadType uploadType;
    private final String logs;
    private final MVDumpsDebugInfoEvent versionEvent;

    DumpsLogPoster(@NotNull MultiverseCore core,
                   @NotNull MVCommandIssuer issuer,
                   @NotNull DumpsLogPoster.UploadType uploadType,
                   @NotNull String logs,
                   @NotNull MVDumpsDebugInfoEvent versionEvent) {
        this.plugin = core;
        this.issuer = issuer;
        this.uploadType = uploadType;
        this.logs = logs;
        this.versionEvent = versionEvent;
    }

    @Override
    public void run() {
        handleLogs();
        handleVersionEvent();
    }

    private void handleLogs() {
        versionEvent.putDetailedDebugInfo("latest.log", logs);
    }

    private void handleVersionEvent() {
        Logging.finer("Upload service is: " + uploadType);
        final Map<String, String> files = versionEvent.getDetailedDebugInfo();
        sendDumpsUrl(uploadType.serviceName, postFilesToService(files));
    }

    private void sendDumpsUrl(String service, String url) {
        issuer.sendInfo(MVCorei18n.DUMPS_URL_LIST, "{service}", service, "{link}", url);
    }

    private String postFilesToService(@NotNull Map<String, String> pasteFiles) {
        PasteService pasteService = PasteServiceFactory.getService(uploadType.pasteServiceType, false);
        return pasteToService(() -> pasteService.postData(encodePasteAsJson(pasteFiles)));
    }

    private String encodePasteAsJson(Map<String, String> files) {
        JSONObject rootObject = new JSONObject();
        JSONArray plugins = new JSONArray();
        JSONArray filesObject = new JSONArray();

        for (Plugin plugin : plugin.getServer().getPluginManager().getPlugins()) {
            plugins.add(new JSONObject(Map.of(
                    "name", plugin.getName(),
                    "version", plugin.getDescription().getVersion(),
                    "description", Option.of(plugin.getDescription().getDescription()).getOrElse(""),
                    "authors", plugin.getDescription().getAuthors(),
                    "website", Option.of(plugin.getDescription().getWebsite()).getOrElse(""),
                    "enabled", plugin.isEnabled()
            )));
        }

        for (Map.Entry<String, String> entry : files.entrySet()) {
            String fileType = "text";
            if (entry.getKey().endsWith(".yml") || entry.getKey().endsWith(".yaml")) {
                fileType = "yaml";
            } else if (entry.getKey().endsWith(".json")) {
                fileType = "json";
            } else if (entry.getKey().endsWith(".md")) {
                fileType = "markdown";
            } else if (entry.getKey().endsWith(".log")) {
                fileType = "accesslog";
            }

            filesObject.add(new JSONObject(Map.of(
                    "name", entry.getKey(),
                    "data", entry.getValue(),
                    "type", fileType
            )));
        }
        rootObject.put("server", new JSONObject(Map.of(
                "name", plugin.getServer().getName(),
                "version", plugin.getServer().getVersion(),
                "bukkitVersion", plugin.getServer().getBukkitVersion(),
                "onlineMode", plugin.getServer().getOnlineMode(),
                "javaVersion", Runtime.version().toString(),
                "operatingSystem", System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch"),
                "uptime", calculateUptimeString(),
                "memory", "%d free / %d total / %d max".formatted(
                        toMb(Runtime.getRuntime().freeMemory()),
                        toMb(Runtime.getRuntime().totalMemory()),
                        toMb(Runtime.getRuntime().maxMemory()))
        )));

        rootObject.put("plugins", plugins);
        rootObject.put("files", filesObject);
        rootObject.put("createdAt", Instant.now().toString());

        return rootObject.toJSONString();
    }

    private long toMb(long value) {
        return value / 1024 / 1024;
    }

    private String calculateUptimeString() {
        long uptimeMs = System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime();
        long totalSeconds = uptimeMs / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%d hrs, %d mins, %d sec", hours, minutes, seconds);
    }

    private String pasteToService(LogPaster paster) {
        try {
            return paster.postLogs();
        } catch (PasteFailedException e) {
            e.printStackTrace();
            return "Error posting to service.";
        }
    }

    private interface LogPaster {
        String postLogs() throws PasteFailedException;
    }
}
