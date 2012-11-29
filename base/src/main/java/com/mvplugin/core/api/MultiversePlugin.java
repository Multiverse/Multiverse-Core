package com.mvplugin.core.api;

/**
 * Implement this if you would
 */
public interface MultiversePlugin {

    /**
     * Gets the reference to MultiverseCore.
     *
     * @return A valid {@link MultiverseCore}.
     */
    MultiverseCore getCore();

    /**
     * Sets the reference to MultiverseCore.
     *
     * @param core A valid {@link MultiverseCore}.
     */
    void setCore(MultiverseCore core);

    /**
     * Allows Multiverse or a plugin to query another Multiverse plugin to see what version its protocol is.
     *
     * This number should change when something will break the code.  Generally your plugin should be checking the
     * Multiverse-Core's protocol version to ensure compatibility.
     *
     * @return The protocol version.
     */
    int getProtocolVersion();
}
