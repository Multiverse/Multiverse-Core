package org.mvplugins.multiverse.core.api;

import io.vavr.control.Try;
import org.jvnet.hk2.annotations.Contract;

import org.mvplugins.multiverse.core.commandtools.queue.ConfirmMode;
import org.mvplugins.multiverse.core.configuration.handle.StringPropertyHandle;
import org.mvplugins.multiverse.core.placeholders.MultiverseCorePlaceholders;

import java.util.Locale;

@Contract
public interface MVConfig {

    /**
     * Loads the config from disk.
     * @return True if the config was loaded successfully.
     */
    Try<Void> load();

    /**
     * Whether the config has been loaded.
     * @return True if the config has been loaded.
     */
    boolean isLoaded();

    /**
     * Saves the config to disk.
     */
    Try<Void> save();

    /**
     * Gets the handler for managing config with string names and values.
     *
     * @return The config handle for string properties.
     */
    StringPropertyHandle getStringPropertyHandle();

    /**
     * Sets world access permissions should be enforced.
     * @param enforceAccess The new value.
     */
    void setEnforceAccess(boolean enforceAccess);

    /**
     * Gets enforceAccess.
     * @return enforceAccess.
     */
    boolean getEnforceAccess();

    /**
     * Sets whether the game mode should be enforced.
     * @param enforceGameMode The new value.
     */
    void setEnforceGameMode(boolean enforceGameMode);

    /**
     * Gets enforceGameMode value.
     * @return True if game mode should be enforced.
     */
    boolean getEnforceGameMode();

    /**
     * Sets whether or not the automatic purge of entities is enabled.
     *
     * @param autopurge True if automatic purge should be enabled.
     */
    void setAutoPurgeEntities(boolean autopurge);

    /**
     * Gets whether or not the automatic purge of entities is enabled.
     *
     * @return True if automatic purge is enabled.
     */
    boolean isAutoPurgeEntities();

    void setUseFinerTeleportPermissions(boolean useFinerTeleportPermissions);

    boolean getUseFinerTeleportPermissions();

    void setConcurrentTeleportLimit(int concurrentTeleportLimit);

    int getConcurrentTeleportLimit();

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
     * Sets resolveAliasInCommands.
     * @param resolveAliasInCommands The new value.
     */
    void setResolveAliasName(boolean resolveAliasInCommands);

    /**
     * Gets resolveAliasInCommands.
     * @return resolveAliasInCommands.
     */
    boolean getResolveAliasName();

    /**
     * Sets firstSpawnOverride.
     * @param firstSpawnOverride The new value.
     */
    void setFirstSpawnOverride(boolean firstSpawnOverride);

    void setSafeLocationHorizontalSearchRadius(int searchRadius);

    int getSafeLocationHorizontalSearchRadius();

    void setSafeLocationVerticalSearchRadius(int searchRadius);

    int getSafeLocationVerticalSearchRadius();

    /**
     * Gets firstSpawnOverride.
     * @return firstSpawnOverride.
     */
    boolean getFirstSpawnOverride();

    /**
     * Sets firstSpawnWorld.
     * @param firstSpawnWorld The new value.
     */
    void setFirstSpawnLocation(String firstSpawnWorld);

    /**
     * Gets firstSpawnWorld.
     * @return firstSpawnWorld.
     */
    String getFirstSpawnLocation();

    /**
     * Sets whether join destination should be enabled.
     * @param enableJoinDestination The new value
     */
    void setEnableJoinDestination(boolean enableJoinDestination);

    /**
     * Gets enableJoinDestination.
     * @return enableJoinDestination
     */
    boolean getEnableJoinDestination();

    /**
     * Sets alwaysSpawnDestination.
     * @param alwaysSpawnDestination The new value
     */
    void setJoinDestination(String alwaysSpawnDestination);

    /**
     * Gets alwaysSpawnDestination.
     * @return alwaysSpawnLocation
     */
    String getJoinDestination();

    /**
     * Sets defaultRespawnToWorldSpawn.
     * @param defaultRespawnToWorldSpawn The new value
     */
    void setDefaultRespawnWithinSameWorld(boolean defaultRespawnToWorldSpawn);

    /**
     * Gets defaultRespawnToWorldSpawn
     * @return defaultRespawnToWorldSpawn
     */
    boolean getDefaultRespawnWithinSameWorld();

    void setEnforceRespawnAtWorldSpawn(boolean enforceRespawnAtWorldSpawn);

    boolean getEnforceRespawnAtWorldSpawn();

    /**
     * Sets whether or not to let Bukkit determine portal search radius on its own or if Multiverse should give input.
     *
     * @param useDefaultPortalSearch True to let Bukkit determine portal search radius on its own.
     */
    void setUseCustomPortalSearch(boolean useDefaultPortalSearch);

    /**
     * Gets whether or not Bukkit will be determining portal search radius on its own or if Multiverse should help.
     *
     * @return True means Bukkit will use its own default values.
     */
    boolean isUsingCustomPortalSearch();

    /**
     * Sets the radius at which vanilla style portals will be searched for to connect to worlds together.
     *
     * @param searchRadius The portal search radius.
     */
    void setCustomPortalSearchRadius(int searchRadius);

    /**
     * Gets the radius at which vanilla style portals will be searched for to connect to worlds together.
     *
     * @return The portal search radius.
     */
    int getCustomPortalSearchRadius();

    /**
     * Sets prefixChat.
     * @param prefixChat The new value.
     */
    void setEnablePrefixChat(boolean prefixChat);

    /**
     * Gets prefixChat.
     * @return prefixChat.
     */
    boolean isEnablePrefixChat();

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
     * Sets whether to register the {@link MultiverseCorePlaceholders} class with PlaceholderAPI plugin.
     * @param registerPapiHook The new value.
     */
    void setRegisterPapiHook(boolean registerPapiHook);

    /**
     * Gets whether to register the {@link MultiverseCorePlaceholders} class with PlaceholderAPI plugin.
     * @return registerPapiHook.
     */
    boolean isRegisterPapiHook();

    /**
     * Sets default locale used for messages
     * @param defaultLocale The new value
     */
    void setDefaultLocale(Locale defaultLocale);

    /**
     * Gets default locale used for messages
     * @return default locale
     */
    Locale getDefaultLocale();

    /**
     * Sets whether to use each player's client locale.
     * @param perPlayerLocale   the new value
     */
    void setPerPlayerLocale(boolean perPlayerLocale);

    /**
     * Gets whether to use each player's client locale.
     * @return  True if per player locale should be used.
     */
    boolean getPerPlayerLocale();

    void setConfirmMode(ConfirmMode confirmMode);

    ConfirmMode getConfirmMode();

    void setUseConfirmOtp(boolean useConfirmOtp);

    boolean getUseConfirmOtp();

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
     * Sets debugPermissions
     * @param debugPermissions  The new value
     */
    void setDebugPermissions(boolean debugPermissions);

    /**
     * gets debugPermissions.
     * @return debugPermissions.
     */
    boolean getDebugPermissions();

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
