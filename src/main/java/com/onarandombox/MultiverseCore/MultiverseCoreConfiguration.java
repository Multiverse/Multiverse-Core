package com.onarandombox.MultiverseCore;

import java.util.Map;

import com.onarandombox.MultiverseCore.api.MultiverseCoreConfig;

import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;

/**
 * Our configuration.
 */
public class MultiverseCoreConfiguration extends SerializationConfig implements MultiverseCoreConfig {
    @Property
    private boolean enforceaccess;
    @Property
    private boolean prefixChat;
    @Property
    private boolean teleportintercept;
    @Property
    private boolean firstspawnoverride;
    @Property
    private boolean displayPermErrors;
    @Property
    private int globaldebug;
    @Property
    private int messagecooldown;
    @Property
    private int version;
    @Property
    private String firstspawnworld;
    @Property
    private int portalcooldown;

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
        enforceaccess = false;
        prefixChat = true;
        teleportintercept = true;
        firstspawnoverride = true;
        displayPermErrors = true;
        globaldebug = 0;
        messagecooldown = 5000;
        portalcooldown = 5000;
        // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck
    }

    // And here we go:

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getEnforceAccess() {
        return enforceaccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnforceAccess(boolean enforceAccess) {
        this.enforceaccess = enforceAccess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getPrefixChat() {
        return prefixChat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrefixChat(boolean prefixChat) {
        this.prefixChat = prefixChat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getTeleportIntercept() {
        return teleportintercept;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTeleportIntercept(boolean teleportIntercept) {
        this.teleportintercept = teleportIntercept;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getFirstSpawnOverride() {
        return firstspawnoverride;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstSpawnOverride(boolean firstSpawnOverride) {
        this.firstspawnoverride = firstSpawnOverride;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getDisplayPermErrors() {
        return displayPermErrors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayPermErrors(boolean displayPermErrors) {
        this.displayPermErrors = displayPermErrors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGlobalDebug() {
        return globaldebug;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGlobalDebug(int globalDebug) {
        this.globaldebug = globalDebug;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMessageCooldown() {
        return messagecooldown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMessageCooldown(int messageCooldown) {
        this.messagecooldown = messageCooldown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getVersion() {
        return version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFirstSpawnWorld() {
        return firstspawnworld;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFirstSpawnWorld(String firstSpawnWorld) {
        this.firstspawnworld = firstSpawnWorld;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPortalCooldown() {
        return portalcooldown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPortalCooldown(int portalCooldown) {
        this.portalcooldown = portalCooldown;
    }
}
