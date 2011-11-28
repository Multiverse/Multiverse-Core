package com.onarandombox.MultiverseCore.utils.webpaste;

public class PasteServiceFactory {
    public static PasteService getService(PasteServiceType type, boolean isPrivate) {
        switch(type) {
            case PASTEBIN: return null;
            case PASTIE: return new PastiePasteService(isPrivate);
            default: return null;
        }
    }
}
