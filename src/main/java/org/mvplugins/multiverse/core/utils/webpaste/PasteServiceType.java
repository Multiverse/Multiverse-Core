package org.mvplugins.multiverse.core.utils.webpaste;

/**
 * An enum containing all known {@link PasteService}s.
 *
 * @see PasteService
 * @see PasteServiceFactory
 */
public enum PasteServiceType {
    /**
     * @see PasteGGPasteService
     */
    PASTEGG,
    /**
     * @see PastebinPasteService
     */
    PASTEBIN,
    /**
     * @see PastesDevPasteService
     */
    PASTESDEV,
    /**
     * @see GitHubPasteService
     */
    GITHUB,
    /**
     * @see McloGsPasteService
     */
    MCLOGS
}
