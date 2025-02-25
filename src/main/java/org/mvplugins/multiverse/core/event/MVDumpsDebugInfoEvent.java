package org.mvplugins.multiverse.core.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when somebody requests version information about Multiverse.
 */
public final class MVDumpsDebugInfoEvent extends Event {

    private final StringBuilder debugInfoBuilder;
    private final Map<String, String> detailedDebugInfo;

    public MVDumpsDebugInfoEvent() {
        debugInfoBuilder = new StringBuilder();
        detailedDebugInfo = new HashMap<>();
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
     *
     * @return A list of HANDLERS.
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Gets the version-info currently saved in this event.
     *
     * @return The version-info.
     */
    public String getDebugInfo() {
        return this.debugInfoBuilder.toString();
    }

    /**
     * Gets the key/value pair of the detailed version info.
     * <p>
     * This information is used for advanced paste services that would prefer
     * to get the information as several files. Examples include config.yml or
     * portals.yml. Note that the map returned is immutable.
     * <p>
     * The keys are filenames, the values are the contents of the files.
     *
     * @return The immutable key value mapping of files and the contents of those files.
     */
    public Map<String, String> getDetailedDebugInfo() {
        return Collections.unmodifiableMap(this.detailedDebugInfo);
    }

    /**
     * Appends more version-info to the version-info currently saved in this event.
     *
     * @param moreVersionInfo The version-info to add. Should end with '\n'.
     */
    public void appendDebugInfo(String moreVersionInfo) {
        this.debugInfoBuilder.append(moreVersionInfo);
    }

    private String readFile(final String filename) {
        StringBuilder result;

        try {
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            result = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            Logging.severe("Unable to find %s. Here's the traceback: %s", filename, e.getMessage());
            e.printStackTrace();
            result = new StringBuilder(String.format("ERROR: Could not load: %s", filename));
        } catch (IOException e) {
            Logging.severe("Something bad happend when reading %s. Here's the traceback: %s", filename, e.getMessage());
            e.printStackTrace();
            result = new StringBuilder(String.format("ERROR: Could not load: %s", filename));
        }

        return result.toString();
    }

    /**
     * Adds a file to to the detailed version-info currently saved in this event.
     *
     * @param fileName The name of the file.
     * @param contents The contents of the file.
     */
    public void putDetailedDebugInfo(String fileName, String contents) {
        this.detailedDebugInfo.put(fileName, contents);
    }

    /**
     * Adds a file to to the detailed version-info currently saved in this event.
     *
     * @param filename The name of the file.
     * @param file     The file.
     */
    public void putDetailedDebugInfo(String filename, File file) {
        this.putDetailedDebugInfo(filename, readFile(file.getAbsolutePath()));
    }
}
