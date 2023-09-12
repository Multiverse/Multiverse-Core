package org.mvplugins.multiverse.core.utils.webpaste;

/**
 * Used to construct {@link PasteService}s.
 */
public class PasteServiceFactory {
    private PasteServiceFactory() { }

    /**
     * Constructs a new {@link PasteService}.
     * @param type The {@link PasteServiceType}.
     * @param isPrivate Whether the new {@link PasteService} should create private pastes.
     * @return The newly created {@link PasteService}.
     */
    public static PasteService getService(PasteServiceType type, boolean isPrivate) {
        return switch (type) {
            case PASTEGG -> new PasteGGPasteService(isPrivate);
            case PASTEBIN -> new PastebinPasteService(isPrivate);
            case PASTESDEV -> new PastesDevPasteService();
            case GITHUB -> new GitHubPasteService(isPrivate);
            case MCLOGS -> new McloGsPasteService();
        };
    }
}
