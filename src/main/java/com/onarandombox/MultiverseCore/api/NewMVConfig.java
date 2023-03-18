package com.onarandombox.MultiverseCore.api;

public interface NewMVConfig {
    /**
     * Sets enforceAccess.
     * @param enforceAccess The new value.
     */
    void setEnforceAccess(boolean enforceAccess);

    /**
     * Gets enforceAccess.
     * @return enforceAccess.
     */
    boolean getEnforceAccess();

    /**
     * Sets whether or not the automatic purge of entities is enabled.
     *
     * @param autopurge True if automatic purge should be enabled.
     */
    void setAutoPurgeEnabled(boolean autopurge);

    /**
     * Gets whether or not the automatic purge of entities is enabled.
     *
     * @return True if automatic purge is enabled.
     */
    boolean isAutoPurgeEnabled();

    /**
     * Sets teleportIntercept.
     * @param teleportIntercept The new value.
     */
    void setTeleportIntercept(boolean teleportIntercept);

    /**
     * Gets teleportIntercept.
     * @return teleportIntercept.
     */
    boolean getTeleportIntercept();

    /**
     * Sets firstSpawnOverride.
     * @param firstSpawnOverride The new value.
     */
    void setFirstSpawnOverride(boolean firstSpawnOverride);

    /**
     * Gets firstSpawnOverride.
     * @return firstSpawnOverride.
     */
    boolean getFirstSpawnOverride();

    /**
     * Sets firstSpawnWorld.
     * @param firstSpawnWorld The new value.
     */
    void setFirstSpawnWorld(String firstSpawnWorld);

    /**
     * Gets firstSpawnWorld.
     * @return firstSpawnWorld.
     */
    String getFirstSpawnWorld();

    /**
     * Sets whether or not to let Bukkit determine portal search radius on its own or if Multiverse should give input.
     *
     * @param useDefaultPortalSearch True to let Bukkit determine portal search radius on its own.
     */
    void setUseDefaultPortalSearch(boolean useDefaultPortalSearch);

    /**
     * Gets whether or not Bukkit will be determining portal search radius on its own or if Multiverse should help.
     *
     * @return True means Bukkit will use its own default values.
     */
    boolean isUsingDefaultPortalSearch();

    /**
     * Sets the radius at which vanilla style portals will be searched for to connect to worlds together.
     *
     * @param searchRadius The portal search radius.
     */
    void setPortalSearchRadius(int searchRadius);

    /**
     * Gets the radius at which vanilla style portals will be searched for to connect to worlds together.
     *
     * @return The portal search radius.
     */
    int getPortalSearchRadius();

    /**
     * Sets prefixChat.
     * @param prefixChat The new value.
     */
    void setPrefixChat(boolean prefixChat);

    /**
     * Gets prefixChat.
     * @return prefixChat.
     */
    boolean getPrefixChat();

    /**
     * Sets prefixChatFormat.
     * @param prefixChatFormat The new value.
     */
    void setPrefixChatFormat(String prefixChatFormat);

    /**
     * Gets prefixChatFormat.
     * @return prefixChatFormat.
     */
    String getPrefixChatFormat();

    /**
     * Sets globalDebug.
     * @param globalDebug The new value.
     */
    void setGlobalDebug(int globalDebug);

    /**
     * Gets globalDebug.
     * @return globalDebug.
     */
    int getGlobalDebug();

    /**
     * Sets whether to suppress startup messages.
     *
     * @param silentStart true to suppress messages.
     */
    void setSilentStart(boolean silentStart);

    /**
     * Whether we are suppressing startup messages.
     *
     * @return true if we are suppressing startup messages.
     */
    boolean getSilentStart();

    /**
     * Sets whether or not the donation/patreon messages are shown.
     *
     * @param idonotwanttodonate True if donation/patreon messages should be shown.
     */
    void setShowDonateMessage(boolean idonotwanttodonate);

    /**
     * Gets whether or not the donation/patreon messages are shown.
     *
     * @return True if donation/patreon messages should be shown.
     */
    boolean isShowingDonateMessage();
}
