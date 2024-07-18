package com.onarandombox.MultiverseCore;

import java.util.Map;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.api.MultiverseCoreConfig;
import com.onarandombox.MultiverseCore.event.MVDebugModeEvent;
import me.main__.util.SerializationConfig.NoSuchPropertyException;
import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;
import org.bukkit.Bukkit;

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
     * @return True if the static instance of config is set.
     */
    public static boolean isSet() {
        return instance != null;
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
    private volatile boolean enforceaccess;
    @Property
    private volatile boolean prefixchat;
    @Property
    private volatile String prefixchatformat;
    @Property
    private volatile boolean useasyncchat;
    @Property
    private volatile boolean teleportintercept;
    @Property
    private volatile boolean firstspawnoverride;
    @Property
    private volatile boolean displaypermerrors;
    @Property
    private volatile boolean enablebuscript;
    @Property
    private volatile int globaldebug;
    @Property
    private volatile boolean silentstart;
    @Property
    private volatile int messagecooldown;
    @Property
    private volatile double version;
    @Property
    private volatile String firstspawnworld;
    @Property
    private volatile int teleportcooldown;
    @Property
    private volatile boolean defaultportalsearch;
    @Property
    private volatile int portalsearchradius;
    @Property
    private volatile boolean autopurge;
    @Property
    private volatile boolean idonotwanttodonate;
    @Property
    private volatile boolean clone_saving;

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
    protected void setDefaults() {
        // BEGIN CHECKSTYLE-SUPPRESSION: MagicNumberCheck
        enforceaccess = false;
        useasyncchat = true;
        prefixchat = false;
        prefixchatformat = "[%world%]%chat%";
        teleportintercept = true;
        firstspawnoverride = true;
        displaypermerrors = true;
        enablebuscript = true;
        globaldebug = 0;
        messagecooldown = 5000;
        teleportcooldown = 1000;
        this.version = 2.9;
        silentstart = false;
        defaultportalsearch = true;
        portalsearchradius = 128;
        autopurge = true;
        idonotwanttodonate = false;
        clone_saving=false;
        // END CHECKSTYLE-SUPPRESSION: MagicNumberCheck
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setConfigProperty(String property, String value) {
        try {
            return this.setProperty(property, value, true);
        } catch (NoSuchPropertyException e) {
            return false;
        }
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
    public String getPrefixChatFormat() {
        return this.prefixchatformat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPrefixChatFormat(String prefixChatFormat) {
        this.prefixchatformat = prefixChatFormat;
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
    public boolean getEnableBuscript() {
        return this.enablebuscript;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnableBuscript(boolean enableBuscript) {
        this.enablebuscript = enableBuscript;
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
        Logging.setDebugLevel(globalDebug);
        Bukkit.getPluginManager().callEvent(new MVDebugModeEvent(globalDebug));
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

    @Override
    public void setUseAsyncChat(boolean useAsyncChat) {
        this.useasyncchat = useAsyncChat;
    }

    @Override
    public boolean getUseAsyncChat() {
        return this.useasyncchat;
    }

    @Override
    public void setSilentStart(boolean silentStart) {
        Logging.setShowingConfig(!silentStart);
        this.silentstart = silentStart;
    }

    @Override
    public boolean getSilentStart() {
        return silentstart;
    }

    @Override
    public void setUseDefaultPortalSearch(boolean useDefaultPortalSearch) {
        defaultportalsearch = useDefaultPortalSearch;
    }

    @Override
    public boolean isUsingDefaultPortalSearch() {
        return defaultportalsearch;
    }

    @Override
    public void setPortalSearchRadius(int searchRadius) {
        this.portalsearchradius = searchRadius;
    }

    @Override
    public int getPortalSearchRadius() {
        return portalsearchradius;
    }

    @Override
    public boolean isAutoPurgeEnabled() {
        return autopurge;
    }

    @Override
    public void setAutoPurgeEnabled(boolean autopurge) {
        this.autopurge = autopurge;
    }

    @Override
    public boolean isShowingDonateMessage() {
        return !idonotwanttodonate;
    }

    @Override
    public boolean doCloneSaving(){
        return clone_saving;
    }

    @Override
    public void setCloneSaving(boolean doSaving){
        this.clone_saving=doSaving;
    }

    @Override
    public void setShowDonateMessage(boolean showDonateMessage) {
        this.idonotwanttodonate = !showDonateMessage;
    }
}
