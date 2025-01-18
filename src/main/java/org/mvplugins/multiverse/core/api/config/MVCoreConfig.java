package org.mvplugins.multiverse.core.api.config;

import io.vavr.control.Try;
import org.jvnet.hk2.annotations.Contract;

import org.mvplugins.multiverse.core.api.commandtools.ConfirmMode;
import org.mvplugins.multiverse.core.api.configuration.StringPropertyHandle;

import java.util.Locale;

/**
 * Represents all the config options in config.yml file
 *
 * @since 5.0
 */
@Contract
public interface MVCoreConfig {

    /**
     * Loads the config from disk.
     *
     * @return True if the config was loaded successfully.
     * @since 5.0
     */
    Try<Void> load();

    /**
     * Whether the config has been loaded.
     *
     * @return True if the config has been loaded.
     * @since 5.0
     */
    boolean isLoaded();

    /**
     * Saves the config to disk.
     *
     * @since 5.0
     */
    Try<Void> save();

    /**
     * Gets the handler for managing config with string names and values.
     *
     * @return The config handle for string properties.
     * @since 5.0
     */
    StringPropertyHandle getStringPropertyHandle();

    /**
     * Sets world access permissions should be enforced.
     *
     * @param enforceAccess The new value.
     * @since 5.0
     */
    void setEnforceAccess(boolean enforceAccess);

    /**
     * Gets enforceAccess.
     *
     * @return enforceAccess.
     * @since 5.0
     */
    boolean getEnforceAccess();

    /**
     * Sets whether the game mode should be enforced.
     *
     * @param enforceGameMode The new value.
     * @since 5.0
     */
    void setEnforceGameMode(boolean enforceGameMode);

    /**
     * Gets enforceGameMode value.
     *
     * @return True if game mode should be enforced.
     * @since 5.0
     */
    boolean getEnforceGameMode();

    /**
     * Sets whether or not the automatic purge of entities is enabled.
     *
     * @param autopurge True if automatic purge should be enabled.
     * @since 5.0
     */
    void setAutoPurgeEntities(boolean autopurge);

    /**
     * Gets whether or not the automatic purge of entities is enabled.
     *
     * @return True if automatic purge is enabled.
     */
    boolean isAutoPurgeEntities();

    /**
     * Sets whether to use finer teleport permissions.
     *
     * @param useFinerTeleportPermissions   The new value
     * @since 5.0
     */
    void setUseFinerTeleportPermissions(boolean useFinerTeleportPermissions);

    /**
     * Gets whether to use finer teleport permissions.
     * @return useFinerTeleportPermissions
     * @since 5.0
     */
    boolean getUseFinerTeleportPermissions();

    /**
     * Sets the number of players allowed to teleport at once.
     *
     * @param concurrentTeleportLimit   The new value
     * @since 5.0
     */
    void setConcurrentTeleportLimit(int concurrentTeleportLimit);

    /**
     * Gets the number of players allowed to teleport at once.
     *
     * @return concurrentTeleportLimit
     * @since 5.0
     */
    int getConcurrentTeleportLimit();

    /**
     * Sets teleportIntercept.
     *
     * @param teleportIntercept The new value.
     * @since 5.0
     */
    void setTeleportIntercept(boolean teleportIntercept);

    /**
     * Gets teleportIntercept.
     *
     * @return teleportIntercept.
     * @since 5.0
     */
    boolean getTeleportIntercept();

    /**
     * Sets resolveAliasInCommands.
     *
     * @param resolveAliasInCommands The new value.
     * @since 5.0
     */
    void setResolveAliasName(boolean resolveAliasInCommands);

    /**
     * Gets resolveAliasInCommands.
     *
     * @return resolveAliasInCommands.
     * @since 5.0
     */
    boolean getResolveAliasName();

    /**
     * Sets firstSpawnOverride.
     *
     * @param firstSpawnOverride The new value.
     * @since 5.0
     */
    void setFirstSpawnOverride(boolean firstSpawnOverride);

    /**
     * Sets safeLocationHorizontalSearchRadius
     * @param searchRadius  The new value
     * @since 5.0
     */
    void setSafeLocationHorizontalSearchRadius(int searchRadius);

    /**
     * Gets safeLocationHorizontalSearchRadius
     *
     * @return safeLocationHorizontalSearchRadius
     * @since 5.0
     */
    int getSafeLocationHorizontalSearchRadius();

    /**
     * Gets safeLocationVerticalSearchRadius
     *
     * @param searchRadius  The new value
     * @since 5.0
     */
    void setSafeLocationVerticalSearchRadius(int searchRadius);

    int getSafeLocationVerticalSearchRadius();

    /**
     * Gets firstSpawnOverride.
     *
     * @return firstSpawnOverride.
     * @since 5.0
     */
    boolean getFirstSpawnOverride();

    /**
     * Sets firstSpawnWorld.
     *
     * @param firstSpawnWorld The new value.
     * @since 5.0
     */
    void setFirstSpawnLocation(String firstSpawnWorld);

    /**
     * Gets firstSpawnWorld.
     *
     * @return firstSpawnWorld.
     * @since 5.0
     */
    String getFirstSpawnLocation();

    /**
     * Sets whether join destination should be enabled.
     *
     * @param enableJoinDestination The new value
     * @since 5.0
     */
    void setEnableJoinDestination(boolean enableJoinDestination);

    /**
     * Gets enableJoinDestination.
     *
     * @return enableJoinDestination
     * @since 5.0
     */
    boolean getEnableJoinDestination();

    /**
     * Sets alwaysSpawnDestination.
     *
     * @param alwaysSpawnDestination The new value
     * @since 5.0
     */
    void setJoinDestination(String alwaysSpawnDestination);

    /**
     * Gets alwaysSpawnDestination.
     *
     * @return alwaysSpawnLocation
     * @since 5.0
     */
    String getJoinDestination();

    /**
     * Sets defaultRespawnToWorldSpawn.
     *
     * @param defaultRespawnToWorldSpawn The new value
     * @since 5.0
     */
    void setDefaultRespawnWithinSameWorld(boolean defaultRespawnToWorldSpawn);

    /**
     * Gets defaultRespawnToWorldSpawn
     *
     * @return defaultRespawnToWorldSpawn
     * @since 5.0
     */
    boolean getDefaultRespawnWithinSameWorld();

    /**
     * Sets enforceRespawnAtWorldSpawn
     *
     * @param enforceRespawnAtWorldSpawn    The new value
     * @since 5.0
     */
    void setEnforceRespawnAtWorldSpawn(boolean enforceRespawnAtWorldSpawn);

    /**
     * Gets enforceRespawnAtWorldSpawn
     * @return enforceRespawnAtWorldSpawn
     * @since 5.0
     */
    boolean getEnforceRespawnAtWorldSpawn();

    /**
     * Sets whether or not to let Bukkit determine portal search radius on its own or if Multiverse should give input.
     *
     * @param useDefaultPortalSearch True to let Bukkit determine portal search radius on its own.
     * @since 5.0
     */
    void setUseCustomPortalSearch(boolean useDefaultPortalSearch);

    /**
     * Gets whether or not Bukkit will be determining portal search radius on its own or if Multiverse should help.
     *
     * @return True means Bukkit will use its own default values.
     * @since 5.0
     */
    boolean isUsingCustomPortalSearch();

    /**
     * Sets the radius at which vanilla style portals will be searched for to connect to worlds together.
     *
     * @param searchRadius The portal search radius.
     * @since 5.0
     */
    void setCustomPortalSearchRadius(int searchRadius);

    /**
     * Gets the radius at which vanilla style portals will be searched for to connect to worlds together.
     *
     * @return The portal search radius.
     * @since 5.0
     */
    int getCustomPortalSearchRadius();

    /**
     * Sets prefixChat.
     *
     * @param prefixChat The new value.
     * @since 5.0
     */
    void setEnablePrefixChat(boolean prefixChat);

    /**
     * Gets prefixChat.
     *
     * @return prefixChat.
     * @since 5.0
     */
    boolean isEnablePrefixChat();

    /**
     * Sets prefixChatFormat.
     *
     * @param prefixChatFormat The new value.
     * @since 5.0
     */
    void setPrefixChatFormat(String prefixChatFormat);

    /**
     * Gets prefixChatFormat.
     *
     * @return prefixChatFormat.
     * @since 5.0
     */
    String getPrefixChatFormat();

    /**
     * Sets whether to register with PlaceholderAPI plugin.
     *
     * @param registerPapiHook The new value.
     * @since 5.0
     */
    void setRegisterPapiHook(boolean registerPapiHook);

    /**
     * Gets whether to register with PlaceholderAPI plugin.
     *
     * @return registerPapiHook.
     * @since 5.0
     */
    boolean isRegisterPapiHook();

    /**
     * Sets default locale used for messages
     *
     * @param defaultLocale The new value
     * @since 5.0
     */
    void setDefaultLocale(Locale defaultLocale);

    /**
     * Gets default locale used for messages
     *
     * @return default locale
     * @since 5.0
     */
    Locale getDefaultLocale();

    /**
     * Sets whether to use each player's client locale.
     *
     * @param perPlayerLocale   the new value
     * @since 5.0
     */
    void setPerPlayerLocale(boolean perPlayerLocale);

    /**
     * Gets whether to use each player's client locale.
     * @return  True if per player locale should be used.
     * @since 5.0
     */
    boolean getPerPlayerLocale();

    /**
     * Sets the mode to use for confirming dangerous commands
     *
     * @param confirmMode   The new value
     * @since 5.0
     */
    void setConfirmMode(ConfirmMode confirmMode);

    /**
     * Gets the mode to use for confirming dangerous commands
     *
     * @return The mode to use for confirming dangerous commands
     * @since 5.0
     */
    ConfirmMode getConfirmMode();

    /**
     * Sets whether to use confirm otp
     *
     * @param useConfirmOtp   The new value
     * @since 5.0
     */
    void setUseConfirmOtp(boolean useConfirmOtp);

    boolean getUseConfirmOtp();

    /**
     * Sets globalDebug.
     *
     * @param globalDebug The new value.
     * @since 5.0
     */
    void setGlobalDebug(int globalDebug);

    /**
     * Gets globalDebug.
     *
     * @return globalDebug.
     * @since 5.0
     */
    int getGlobalDebug();

    /**
     * Sets debugPermissions
     *
     * @param debugPermissions  The new value
     * @since 5.0
     */
    void setDebugPermissions(boolean debugPermissions);

    /**
     * gets debugPermissions.
     *
     * @return debugPermissions.
     * @since 5.0
     */
    boolean getDebugPermissions();

    /**
     * Sets whether to suppress startup messages.
     *
     * @param silentStart true to suppress messages.
     * @since 5.0
     */
    void setSilentStart(boolean silentStart);

    /**
     * Whether we are suppressing startup messages.
     *
     * @return true if we are suppressing startup messages.
     * @since 5.0
     */
    boolean getSilentStart();

    /**
     * Sets whether or not the donation/patreon messages are shown.
     *
     * @param showDonateMessage True if donation/patreon messages should be shown.
     * @since 5.0
     */
    void setShowDonateMessage(boolean showDonateMessage);

    /**
     * Gets whether or not the donation/patreon messages are shown.
     *
     * @return True if donation/patreon messages should be shown.
     * @since 5.0
     */
    boolean isShowingDonateMessage();
}
