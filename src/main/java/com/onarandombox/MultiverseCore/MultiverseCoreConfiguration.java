package com.onarandombox.MultiverseCore;

import java.util.Map;

import com.onarandombox.MultiverseCore.api.MultiverseCoreConfig;

import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;

/**
 * Our configuration.
 */
public class MultiverseCoreConfiguration extends SerializationConfig implements MultiverseCoreConfig {
    private static MultiverseCoreConfiguration instance;

    /**
     * Sets the statically saved instance.
     * @param instance The new instance.
     */
    public static void setInstance(MultiverseCoreConfiguration instance) {
        MultiverseCoreConfiguration.instance = instance;
    }

    /**
     * Gets the statically saved instance.
     * @return The statically saved instance.
     */
    public static MultiverseCoreConfiguration getInstance() {
        if (instance == null)
            throw new IllegalStateException("The instance wasn't set!");
        return instance;
    }

    @Property
    private boolean enforceaccess;
    @Property
    private boolean prefixchat;
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
    private int teleportcooldown;

    public MultiverseCoreConfiguration() {
        super();
        MultiverseCoreConfiguration.setInstance(this);
    }

    public MultiverseCoreConfiguration(Map<String, Object> values) {
        super(values);
        MultiverseCoreConfiguration.setInstance(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDefaults() {
        // BEGIN CHECKSTYLE-SUPPRESSION: MagicNumberCheck
        enforceaccess = false;
        prefixchat = true;
        teleportintercept = true;
        firstspawnoverride = true;
        displaypermerrors = true;
        globaldebug = 0;
        messagecooldown = 5000;
        teleportcooldown = 1000;
        this.version = 2.9;
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
        return this.prefixchat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrefixChat(boolean prefixChat) {
        this.prefixchat = prefixChat;
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
    public int getTeleportCooldown() {
        return this.teleportcooldown;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTeleportCooldown(int teleportCooldown) {
        this.teleportcooldown = teleportCooldown;
    }
}
