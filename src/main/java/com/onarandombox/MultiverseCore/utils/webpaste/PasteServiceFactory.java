package com.onarandombox.MultiverseCore.utils.webpaste;

public class PasteServiceFactory {
    private PasteServiceFactory() { }

    public static PasteService getService(PasteServiceType type, boolean isPrivate) {
        switch(type) {
            case PASTEBIN: return new PastebinPasteService(isPrivate);
            case PASTIE: return new PastiePasteService(isPrivate);
            default: return null;
        }
    }
}
