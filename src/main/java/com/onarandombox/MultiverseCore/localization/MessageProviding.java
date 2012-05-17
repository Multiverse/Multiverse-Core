package com.onarandombox.MultiverseCore.localization;

/**
 * Multiverse 2 MessageProviding
 * <p>
 * This interface is implemented by classes that use a {@link MessageProvider}.
 */
public interface MessageProviding {
    /**
     * @return The {@link MessageProvider} used by the Core.
     */
    MessageProvider getMessageProvider();

    /**
     * Sets the {@link MessageProvider} used by the core.
     *
     * @param provider The new {@link MessageProvider}. Must not be null!
     */
    void setMessageProvider(MessageProvider provider);
}
