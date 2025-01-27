package org.mvplugins.multiverse.core.commands;

import java.util.Map;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.event.MVDumpsDebugInfoEvent;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.utils.webpaste.PasteFailedException;
import org.mvplugins.multiverse.core.utils.webpaste.PasteService;
import org.mvplugins.multiverse.core.utils.webpaste.PasteServiceFactory;
import org.mvplugins.multiverse.core.utils.webpaste.PasteServiceType;

final class DumpsLogPoster extends BukkitRunnable {

    enum UploadType {
        // BEGIN CHECKSTYLE-SUPPRESSION: JavadocVariable
        PASTEGG(PasteServiceType.PASTEGG, "paste.gg"),
        PASTESDEV(PasteServiceType.PASTESDEV, "pastes.dev");
        // END CHECKSTYLE-SUPPRESSION: JavadocVariable

        private final PasteServiceType pasteServiceType;
        private final String serviceName;

        UploadType(PasteServiceType pasteServiceType, String serviceName) {
            this.pasteServiceType = pasteServiceType;
            this.serviceName = serviceName;
        }
    }

    enum LogsType {
        /**
         * Appends the log file to the debug info.
         */
        APPEND,
        /**
         * Uploads the log file to a paste service.
         */
        MCLOGS
    }

    private final MVCommandIssuer issuer;
    private final LogsType logsType;
    private final UploadType uploadType;
    private final boolean paranoid;
    private final String logs;
    private final MVDumpsDebugInfoEvent versionEvent;

    DumpsLogPoster(@NotNull MVCommandIssuer issuer,
                   @NotNull DumpsLogPoster.LogsType logsType,
                   @NotNull DumpsLogPoster.UploadType uploadType,
                   boolean paranoid,
                   @NotNull String logs,
                   @NotNull MVDumpsDebugInfoEvent versionEvent) {
        this.issuer = issuer;
        this.logsType = logsType;
        this.uploadType = uploadType;
        this.paranoid = paranoid;
        this.logs = logs;
        this.versionEvent = versionEvent;
    }

    @Override
    public void run() {
        handleLogs();
        handleVersionEvent();
    }

    @SuppressWarnings("checkstyle:MissingSwitchDefault")
    private void handleLogs() {
        Logging.finer("Logs type is: " + logsType);

        if (!paranoid) {
            switch (logsType) {
                case MCLOGS -> sendDumpsUrl("Logs", postRawDataToMcLogs(logs));
                case APPEND -> versionEvent.putDetailedDebugInfo("latest.log", logs);
                default -> Logging.finer("Not uploading logs.");
            }
        } else {
            Logging.finer("Paranoid mode is on, not uploading logs.");
        }
    }

    private void handleVersionEvent() {
        Logging.finer("Upload service is: " + uploadType);

        final Map<String, String> files = versionEvent.getDetailedDebugInfo();
        sendDumpsUrl(uploadType.serviceName, postFilesToService(files));
    }

    private void sendDumpsUrl(String service, String url) {
        issuer.sendInfo(MVCorei18n.DUMPS_URL_LIST, "{service}", service, "{link}", url);
    }

    private String postRawDataToMcLogs(@NotNull String rawPasteData) {
        PasteService pasteService = PasteServiceFactory.getService(PasteServiceType.MCLOGS, true);

        return pasteToService(() -> pasteService.postData(rawPasteData));
    }

    private String postFilesToService(@NotNull Map<String, String> pasteFiles) {
        PasteService pasteService = PasteServiceFactory.getService(uploadType.pasteServiceType, true);

        return pasteToService(() -> {
            if (pasteService.supportsMultiFile()) {
                return pasteService.postData(pasteFiles);
            } else {
                return pasteService.postData(this.encodeAsString(pasteFiles));
            }
        });
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
