package com.onarandombox.MultiverseCore.api;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * The configuration of MultiverseCore.
 */
public interface MultiverseCoreConfig extends ConfigurationSerializable {
    /**
     * Sets a property using a {@link String}.
     * @param property The name of the property.
     * @param value The value.
     * @return True on success, false if the operation failed.
     */
    boolean setConfigProperty(String property, String value);

    /**
     * Sets portalCooldown.
     * @param portalCooldown The new value.
     */
    void setTeleportCooldown(int portalCooldown);

    /**
     * Gets portalCooldown.
     * @return portalCooldown.
     */
    int getTeleportCooldown();

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
     * Sets version.
     * @param version The new value.
     */
    void setVersion(int version);

    /**
     * Gets version.
     * @return version.
     */
    double getVersion();

    /**
     * Sets messageCooldown.
     * @param messageCooldown The new value.
     */
    void setMessageCooldown(int messageCooldown);

    /**
     * Gets messageCooldown.
     * @return messageCooldown.
     */
    int getMessageCooldown();

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
     * Sets displayPermErrors.
     * @param displayPermErrors The new value.
     */
    void setDisplayPermErrors(boolean displayPermErrors);

    /**
     * Gets displayPermErrors.
     * @return displayPermErrors.
     */
    boolean getDisplayPermErrors();

    /**
     * Sets enableBuscript.
     * @param enableBuscript The new value.
     */
    void setEnableBuscript(boolean enableBuscript);

    /**
     * Gets enableBuscript.
     * @return enableBuscript.
     */
    boolean getEnableBuscript();

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
     * Sets useasyncchat.
     * @param useAsyncChat The new value.
     */
    void setUseAsyncChat(boolean useAsyncChat);

    /**
     * Gets useasyncchat.
     * @return useasyncchat.
     */
    boolean getUseAsyncChat();

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
     * Gets whether or not the automatic purge of entities is enabled.
     *
     * @return True if automatic purge is enabled.
     */
    boolean isAutoPurgeEnabled();

    /**
     * Sets whether or not the automatic purge of entities is enabled.
     *
     * @param autopurge True if automatic purge should be enabled.
     */
    void setAutoPurgeEnabled(boolean autopurge);

    /**
     * Gets whether or not the donation/patreon messages are shown.
     *
     * @return True if donation/patreon messages should be shown.
     */
    boolean isShowingDonateMessage();

    boolean doCloneSaving();

    void setCloneSaving(boolean doSaving);

    /**
     * Sets whether or not the donation/patreon messages are shown.
     *
     * @param idonotwanttodonate True if donation/patreon messages should be shown.
     */
    void setShowDonateMessage(boolean idonotwanttodonate);
}
