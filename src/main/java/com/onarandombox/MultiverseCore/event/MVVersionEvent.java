package com.onarandombox.MultiverseCore.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Map;

/**
 * Called when somebody requests version information about Multiverse.
 */
public class MVVersionEvent extends Event {

    private final StringBuilder versionInfoBuilder;
    private final Map<String, String> detailedVersionInfo;

    public MVVersionEvent(String legacyVersionInfo, Map<String, String> files) {
        this.versionInfoBuilder = new StringBuilder(legacyVersionInfo);
        this.detailedVersionInfo = files;
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
     * Gets the key/value pair of the detailed version info.
     *
     * This information is used for advanced paste services that would prefer
     * to get the information as several files. Examples include config.yml or
     * portals.yml.
     *
     * The keys are filenames, the values are the contents of the files.
     *
     * @return The key value mapping of files and the contents of those files.
     */
    public Map<String, String> getDetailedVersionInfo() {
        return this.detailedVersionInfo;
    }

    /**
     * Appends more version-info to the version-info currently saved in this event.
     * @param moreVersionInfo The version-info to add. Should end with '\n'.
     */
    public void appendVersionInfo(String moreVersionInfo) {
        this.versionInfoBuilder.append(moreVersionInfo);
    }

    /**
     * Adds a file to to the detailed version-info currently saved in this event.
     * @param filename The name of the file.
     * @param text The file's content.
     */
    public void putDetailedVersionInfo(String filename, String text) {
        this.detailedVersionInfo.put(filename, text);
    }
}
