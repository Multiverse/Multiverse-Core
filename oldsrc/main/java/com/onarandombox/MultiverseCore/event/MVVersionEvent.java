package com.onarandombox.MultiverseCore.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when somebody requests version information about Multiverse.
 */
public class MVVersionEvent extends Event {

    private final StringBuilder versionInfoBuilder;

    public MVVersionEvent(String versionInfo) {
        this.versionInfoBuilder = new StringBuilder(versionInfo);
    }

    private static final HandlerList HANDLERS = new HandlerList();

    /**
     * {@inheritDoc}
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the handler list. This is required by the event system.
     * @return A list of HANDLERS.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Gets the version-info currently saved in this event.
     * @return The version-info.
     */
    public String getVersionInfo() {
        return this.versionInfoBuilder.toString();
    }

    /**
     * Appends more version-info to the version-info currently saved in this event.
     * @param moreVersionInfo The version-info to add. Should end with '\n'.
     */
    public void appendVersionInfo(String moreVersionInfo) {
        this.versionInfoBuilder.append(moreVersionInfo);
    }
}
