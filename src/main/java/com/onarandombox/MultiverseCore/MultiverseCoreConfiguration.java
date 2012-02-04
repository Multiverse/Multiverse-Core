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
    private boolean displaypermerrors;
    @Property
    private int globaldebug;
    @Property
    private int messagecooldown;
    @Property
    private double version;
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
        displaypermerrors = true;
        globaldebug = 0;
        messagecooldown = 5000;
        portalcooldown = 5000;
        this.version = 2.8;
        // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck
    }

    // And here we go:

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getEnforceAccess() {
        return this.enforceaccess;
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
        return this.prefixChat;
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
        return this.teleportintercept;
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
        return this.firstspawnoverride;
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
        return this.displaypermerrors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayPermErrors(boolean displayPermErrors) {
        this.displaypermerrors = displayPermErrors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGlobalDebug() {
        return this.globaldebug;
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
        return this.messagecooldown;
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
    public double getVersion() {
        return this.version;
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
        return this.firstspawnworld;
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
        return this.portalcooldown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPortalCooldown(int portalCooldown) {
        this.portalcooldown = portalCooldown;
    }
}
