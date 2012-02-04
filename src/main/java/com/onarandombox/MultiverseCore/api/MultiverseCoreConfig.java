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
    boolean setProperty(String property, String value);

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
     * Sets enforceAccess.
     * @param enforceAccess The new value.
     */
    void setEnforceAccess(boolean enforceAccess);

    /**
     * Gets enforceAccess.
     * @return enforceAccess.
     */
    boolean getEnforceAccess();
}
