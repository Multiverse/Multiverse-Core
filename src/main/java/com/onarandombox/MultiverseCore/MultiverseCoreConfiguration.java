package com.onarandombox.MultiverseCore;

import java.util.Map;

import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;

/**
 * Our configuration.
 */
public class MultiverseCoreConfiguration extends SerializationConfig {
    @Property
    private boolean enforceAccess;
    @Property
    private boolean prefixChat;
    @Property
    private boolean teleportIntercept;
    @Property
    private boolean firstSpawnOverride;
    @Property
    private boolean displayPermErrors;
    @Property
    private int globalDebug;
    @Property
    private int messageCooldown;

    public MultiverseCoreConfiguration() {
        super();
    }

    public MultiverseCoreConfiguration(Map<String, Object> values) {
        super(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaults() {
        // BEGIN CHECKSTYLE-SUPPRESSION: MagicNumberCheck
        enforceAccess = false;
        prefixChat = true;
        teleportIntercept = true;
        firstSpawnOverride = true;
        displayPermErrors = true;
        globalDebug = 0;
        messageCooldown = 5000;
        // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck
    }

    // And here we go:

    /**
     * Gets enforceAccess.
     * @return enforceAccess.
     */
    public boolean getEnforceAccess() {
        return enforceAccess;
    }

    /**
     * Sets enforceAccess.
     * @param enforceAccess The new value.
     */
    public void setEnforceAccess(boolean enforceAccess) {
        this.enforceAccess = enforceAccess;
    }

    /**
     * Gets prefixChat.
     * @return prefixChat.
     */
    public boolean getPrefixChat() {
        return prefixChat;
    }

    /**
     * Sets prefixChat.
     * @param prefixChat The new value.
     */
    public void setPrefixChat(boolean prefixChat) {
        this.prefixChat = prefixChat;
    }

    /**
     * Gets teleportIntercept.
     * @return teleportIntercept.
     */
    public boolean getTeleportIntercept() {
        return teleportIntercept;
    }

    /**
     * Sets teleportIntercept.
     * @param teleportIntercept The new value.
     */
    public void setTeleportIntercept(boolean teleportIntercept) {
        this.teleportIntercept = teleportIntercept;
    }

    /**
     * Gets firstSpawnOverride.
     * @return firstSpawnOverride.
     */
    public boolean getFirstSpawnOverride() {
        return firstSpawnOverride;
    }

    /**
     * Sets firstSpawnOverride.
     * @param firstSpawnOverride The new value.
     */
    public void setFirstSpawnOverride(boolean firstSpawnOverride) {
        this.firstSpawnOverride = firstSpawnOverride;
    }

    /**
     * Gets displayPermErrors.
     * @return displayPermErrors.
     */
    public boolean getDisplayPermErrors() {
        return displayPermErrors;
    }

    /**
     * Sets displayPermErrors.
     * @param displayPermErrors The new value.
     */
    public void setDisplayPermErrors(boolean displayPermErrors) {
        this.displayPermErrors = displayPermErrors;
    }

    /**
     * Gets globalDebug.
     * @return globalDebug.
     */
    public int getGlobalDebug() {
        return globalDebug;
    }

    /**
     * Sets globalDebug.
     * @param globalDebug The new value.
     */
    public void setGlobalDebug(int globalDebug) {
        this.globalDebug = globalDebug;
    }

    /**
     * Gets messageCooldown.
     * @return messageCooldown.
     */
    public int getMessageCooldown() {
        return messageCooldown;
    }

    /**
     * Sets messageCooldown.
     * @param messageCooldown The new value.
     */
    public void setMessageCooldown(int messageCooldown) {
        this.messageCooldown = messageCooldown;
    }
}
