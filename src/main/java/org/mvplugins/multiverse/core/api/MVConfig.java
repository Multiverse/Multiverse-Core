package org.mvplugins.multiverse.core.api;

import java.util.Collection;

import io.vavr.control.Try;
import org.jvnet.hk2.annotations.Contract;

import org.mvplugins.multiverse.core.configuration.node.NodeGroup;
import org.mvplugins.multiverse.core.placeholders.MultiverseCorePlaceholders;

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
     * Gets the nodes for the config.
     *
     * @return The nodes for the config.
     */
    NodeGroup getNodes();

    /**
     * Auto-complete suggestions for a property.
     *
     * @param name  The name of the node.
     * @param input The current user input.
     * @return A collection of possible string values.
     */
    Collection<String> suggestPropertyValues(String name, String input);

    /**
     * Gets a property from the config.
     *
     * @param name The name of the property.
     * @return A {@link Try} with the value of the property, otherwise a {@link Try.Failure} if there is no property by
     * that name.
     */
    Try<Object> getProperty(String name);

    /**
     * Sets a property in the config.
     *
     * @param name  The name of the property.
     * @param value The value of the property.
     * @return An empty {@link Try} if the property was set successfully, otherwise a {@link Try.Failure} with the
     *         exception explaining why the property could not be set.
     */
    Try<Void> setProperty(String name, Object value);

    /**
     * Sets a string property in the config.
     *
     * @param name  The name of the property.
     * @param value The string value of the property.
     * @return An empty {@link Try} if the property was set successfully, otherwise a {@link Try.Failure} with the
     *         exception explaining why the property could not be set.
     */
    Try<Void> setPropertyString(String name, String value);

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
