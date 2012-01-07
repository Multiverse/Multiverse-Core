package com.onarandombox.MultiverseCore.event;

import org.bukkit.event.Event;

/**
 * Called when somebody requests version information about Multiverse.
 */
public class MVVersionEvent extends Event {

    private final StringBuilder versionInfoBuilder;

    public MVVersionEvent(String versionInfo) {
        super("MVVersionEvent");
        versionInfoBuilder = new StringBuilder(versionInfo);
    }

    /**
     * Gets the version-info currently saved in this event.
     * @return The version-info.
     */
    public String getVersionInfo() {
        return versionInfoBuilder.toString();
    }

    /**
     * Appends more version-info to the version-info currently saved in this event.
     * @param moreVersionInfo The version-info to add. Should end with '\n'.
     */
    public void appendVersionInfo(String moreVersionInfo) {
        versionInfoBuilder.append(moreVersionInfo);
    }
}
