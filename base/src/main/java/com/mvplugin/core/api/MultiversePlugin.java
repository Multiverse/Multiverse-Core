package com.mvplugin.core.api;

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
     * Allows Multiverse or a plugin to query another Multiverse plugin to see what version its protocol is. This
     * number
     * should change when something will break the code.
     *
     * @return The Integer protocol version.
     */
    int getProtocolVersion();
}
