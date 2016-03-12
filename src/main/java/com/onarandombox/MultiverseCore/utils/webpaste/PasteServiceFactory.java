package com.onarandombox.MultiverseCore.utils.webpaste;

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
        switch(type) {
            case PASTEBIN:
                return new PastebinPasteService(isPrivate);
            case PASTIE:
                return new PastiePasteService(isPrivate);
            case GITHUB:
                return new GithubPasteService(isPrivate);
            default:
                return null;
        }
    }
}
