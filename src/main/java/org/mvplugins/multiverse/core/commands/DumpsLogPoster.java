package org.mvplugins.multiverse.core.commands;

import java.util.Map;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.event.MVDumpsDebugInfoEvent;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.webpaste.PasteFailedException;
import org.mvplugins.multiverse.core.utils.webpaste.PasteService;
import org.mvplugins.multiverse.core.utils.webpaste.PasteServiceFactory;
import org.mvplugins.multiverse.core.utils.webpaste.PasteServiceType;

final class DumpsLogPoster extends BukkitRunnable {

    enum UploadType {
        PASTEGG,
        PASTESDEV
    }

    enum LogsType {
        APPEND,
        MCLOGS
    }

    private final MVCommandIssuer issuer;
    private final LogsType logsType;
    private final UploadType servicesType;
    private final boolean paranoid;
    private final String logs;
    private final MVDumpsDebugInfoEvent versionEvent;

    DumpsLogPoster(@NotNull MVCommandIssuer issuer,
                   @NotNull DumpsLogPoster.LogsType logsType,
                   @NotNull DumpsLogPoster.UploadType servicesType,
                   boolean paranoid,
                   @Nullable String logs,
                   @NotNull MVDumpsDebugInfoEvent versionEvent) {
        this.issuer = issuer;
        this.logsType = logsType;
        this.servicesType = servicesType;
        this.paranoid = paranoid;
        this.logs = logs;
        this.versionEvent = versionEvent;
    }

    @Override
    public void run() {
        // TODO: Refactor into smaller methods
        Logging.finer("Logs type is: " + logsType);
        Logging.finer("Services is: " + servicesType);

        // Deal with logs flag
        if (!paranoid) {
            switch (logsType) {
                case MCLOGS -> issuer.sendInfo(MVCorei18n.DUMPS_URL_LIST,
                        "{service}", "Logs",
                        "{link}", postToService(PasteServiceType.MCLOGS, logs, null));
                case APPEND -> versionEvent.putDetailedDebugInfo("latest.log", logs);
            }
        }

        // Get the files from the event
        final Map<String, String> files = versionEvent.getDetailedDebugInfo();

        // Deal with uploading debug info
        switch (servicesType) {
            case PASTEGG -> issuer.sendInfo(MVCorei18n.DUMPS_URL_LIST,
                    "{service}", "paste.gg",
                    "{link}", postToService(PasteServiceType.PASTEGG, null, files));
            case PASTESDEV -> issuer.sendInfo(MVCorei18n.DUMPS_URL_LIST,
                    "{service}", "pastes.dev",
                    "{link}", postToService(PasteServiceType.PASTESDEV, null, files));
        }
    }

    private String postToService(@NotNull PasteServiceType type, @Nullable String rawPasteData, @Nullable Map<String, String> pasteFiles) {
        PasteService pasteService = PasteServiceFactory.getService(type, true);

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

    private String encodeAsString(Map<String, String> files) {
        StringBuilder uploadData = new StringBuilder();
        for (Map.Entry<String, String> entry : files.entrySet()) {
            String file = entry.getKey();
            String data = entry.getValue();
            uploadData.append("# ---------- ")
                    .append(file)
                    .append(" ----------\n\n")
                    .append(data)
                    .append("\n\n");
        }

        return uploadData.toString();
    }
}
