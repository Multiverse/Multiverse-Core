package org.mvplugins.multiverse.core.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.ConfirmMode;
import org.mvplugins.multiverse.core.configuration.handle.CommentedConfigurationHandle;
import org.mvplugins.multiverse.core.configuration.handle.StringPropertyHandle;
import org.mvplugins.multiverse.core.configuration.migration.BooleanMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.configuration.migration.IntegerMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.InvertBoolMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.MoveMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.VersionMigrator;

@Service
public class MVCoreConfig {
    public static final String CONFIG_FILENAME = "config.yml";

    private final Path configPath;
    private final MVCoreConfigNodes configNodes;
    private final CommentedConfigurationHandle configHandle;
    private final StringPropertyHandle stringPropertyHandle;

    @Inject
    MVCoreConfig(
            @NotNull MultiverseCore core,
            @NotNull PluginManager pluginManager,
            @NotNull Provider<MVCommandManager> commandManager // config needs to be instantiated before the command manager
    ) {
        this.configPath = Path.of(core.getDataFolder().getPath(), CONFIG_FILENAME);
        this.configNodes = new MVCoreConfigNodes(pluginManager, commandManager);
        this.configHandle = CommentedConfigurationHandle.builder(configPath, configNodes.getNodes())
                .logger(Logging.getLogger())
                .migrator(ConfigMigrator.builder(configNodes.VERSION)
                        .addVersionMigrator(VersionMigrator.builder(5.0)
                                .addAction(MoveMigratorAction.of("multiverse-configuration.enforceaccess", "world.enforce-access"))
                                .addAction(BooleanMigratorAction.of("world.enforce-access"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.prefixchat", "messaging.enable-chat-prefix"))
                                .addAction(BooleanMigratorAction.of("messaging.enable-chat-prefix"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.prefixchatformat", "messaging.chat-prefix-format"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.teleportintercept", "world.teleport-intercept"))
                                .addAction(BooleanMigratorAction.of("world.teleport-intercept"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.firstspawnoverride", "spawn.first-spawn-override"))
                                .addAction(BooleanMigratorAction.of("spawn.first-spawn-override"))
                                //.addAction(MoveMigratorAction.of("multiverse-configuration.displaypermerrors", ""))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.globaldebug", "misc.global-debug"))
                                .addAction(IntegerMigratorAction.of("misc.global-debug"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.silentstart", "misc.silent-start"))
                                .addAction(BooleanMigratorAction.of("misc.silent-start"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.firstspawnworld", "spawn.first-spawn-location"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.defaultportalsearch", "portal.use-custom-portal-search"))
                                .addAction(BooleanMigratorAction.of("portal.use-custom-portal-search"))
                                .addAction(InvertBoolMigratorAction.of("portal.use-custom-portal-search"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.portalsearchradius", "portal.custom-portal-search-radius"))
                                .addAction(IntegerMigratorAction.of("portal.custom-portal-search-radius"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.autopurge", "world.auto-purge-entities"))
                                .addAction(BooleanMigratorAction.of("world.auto-purge-entities"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.idonotwanttodonate", "misc.show-donation-message"))
                                .addAction(BooleanMigratorAction.of("misc.show-donation-message"))
                                .addAction(InvertBoolMigratorAction.of("misc.show-donation-message"))
                                .build())
                        .addVersionMigrator(VersionMigrator.builder(5.1)
                                .addAction(MoveMigratorAction.of("world.teleport-intercept", "teleport.teleport-intercept"))
                                .addAction(MoveMigratorAction.of("world.resolve-alias-name", "command.resolve-alias-name"))
                                .build())
                        .addVersionMigrator(VersionMigrator.builder(5.2)
                                .addAction(MoveMigratorAction.of("spawn.default-respawn-to-world-spawn", "world.enforce-respawn-at-world-spawn"))
                                .build())
                        .build())
                .build();
        this.stringPropertyHandle = new StringPropertyHandle(configHandle);
    }

    private void migrateFromOldConfigFile() {
        String content;
        try {
            content = Files.readString(configPath);
        } catch (IOException e) {
            return;
        }
        // Remove the old config section if it is still in the old ConfigurationSerializable.
        content = content.replace("==: com.onarandombox.MultiverseCore.MultiverseCoreConfiguration", "");
        try {
            Files.writeString(configPath, content);
        } catch (IOException e) {
            // ignore
        }
    }

    public Try<Void> load() {
        return Try.run(this::migrateFromOldConfigFile)
                .flatMap(ignore -> configHandle.load())
                .onFailure(e -> {
                    Logging.severe("Failed to load Multiverse-Core config.yml!");
                    e.printStackTrace();
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLoaded() {
        return configHandle.isLoaded();
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> save() {
        return configHandle.save();
    }

    /**
     * {@inheritDoc}
     */
    public StringPropertyHandle getStringPropertyHandle() {
        return stringPropertyHandle;
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setEnforceAccess(boolean enforceAccess) {
        return configHandle.set(configNodes.ENFORCE_ACCESS, enforceAccess);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getEnforceAccess() {
        return configHandle.get(configNodes.ENFORCE_ACCESS);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setEnforceGameMode(boolean enforceGameMode) {
        return configHandle.set(configNodes.ENFORCE_GAMEMODE, enforceGameMode);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getEnforceGameMode() {
        return configHandle.get(configNodes.ENFORCE_GAMEMODE);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setAutoPurgeEntities(boolean autopurge) {
        return configHandle.set(configNodes.AUTO_PURGE_ENTITIES, autopurge);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAutoPurgeEntities() {
        return configHandle.get(configNodes.AUTO_PURGE_ENTITIES);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setUseFinerTeleportPermissions(boolean useFinerTeleportPermissions) {
        return configHandle.set(configNodes.USE_FINER_TELEPORT_PERMISSIONS, useFinerTeleportPermissions);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUseFinerTeleportPermissions() {
        return configHandle.get(configNodes.USE_FINER_TELEPORT_PERMISSIONS);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setConcurrentTeleportLimit(int concurrentTeleportLimit) {
        return configHandle.set(configNodes.CONCURRENT_TELEPORT_LIMIT, concurrentTeleportLimit);
    }

    /**
     * {@inheritDoc}
     */
    public int getConcurrentTeleportLimit() {
        return configHandle.get(configNodes.CONCURRENT_TELEPORT_LIMIT);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setTeleportIntercept(boolean teleportIntercept) {
        return configHandle.set(configNodes.TELEPORT_INTERCEPT, teleportIntercept);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getTeleportIntercept() {
        return configHandle.get(configNodes.TELEPORT_INTERCEPT);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setFirstSpawnOverride(boolean firstSpawnOverride) {
        return configHandle.set(configNodes.FIRST_SPAWN_OVERRIDE, firstSpawnOverride);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setSafeLocationHorizontalSearchRadius(int searchRadius) {
        return configHandle.set(configNodes.SAFE_LOCATION_HORIZONTAL_SEARCH_RADIUS, searchRadius);
    }

    /**
     * {@inheritDoc}
     */
    public int getSafeLocationHorizontalSearchRadius() {
        return configHandle.get(configNodes.SAFE_LOCATION_HORIZONTAL_SEARCH_RADIUS);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setSafeLocationVerticalSearchRadius(int searchRadius) {
        return configHandle.set(configNodes.SAFE_LOCATION_VERTICAL_SEARCH_RADIUS, searchRadius);
    }

    /**
     * {@inheritDoc}
     */
    public int getSafeLocationVerticalSearchRadius() {
        return configHandle.get(configNodes.SAFE_LOCATION_VERTICAL_SEARCH_RADIUS);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getFirstSpawnOverride() {
        return configHandle.get(configNodes.FIRST_SPAWN_OVERRIDE);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setFirstSpawnLocation(String firstSpawnWorld) {
        return configHandle.set(configNodes.FIRST_SPAWN_LOCATION, firstSpawnWorld);
    }

    /**
     * {@inheritDoc}
     */
    public String getFirstSpawnLocation() {
        return configHandle.get(configNodes.FIRST_SPAWN_LOCATION);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setEnableJoinDestination(boolean enableJoinDestination) {
        return configHandle.set(configNodes.ENABLE_JOIN_DESTINATION, enableJoinDestination);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getEnableJoinDestination() {
        return  configHandle.get(configNodes.ENABLE_JOIN_DESTINATION);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setJoinDestination(String alwaysSpawnDestination) {
        return configHandle.set(configNodes.JOIN_DESTINATION, alwaysSpawnDestination);
    }

    /**
     * {@inheritDoc}
     */
    public String getJoinDestination() {
        return  configHandle.get(configNodes.JOIN_DESTINATION);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setDefaultRespawnWithinSameWorld(boolean defaultRespawnToWorldSpawn) {
        return configHandle.set(configNodes.DEFAULT_RESPAWN_WITHIN_SAME_WORLD, defaultRespawnToWorldSpawn);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getDefaultRespawnWithinSameWorld() {
        return configHandle.get(configNodes.DEFAULT_RESPAWN_WITHIN_SAME_WORLD);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setEnforceRespawnAtWorldSpawn(boolean enforceRespawnAtWorldSpawn) {
        return configHandle.set(configNodes.ENFORCE_RESPAWN_AT_WORLD_SPAWN, enforceRespawnAtWorldSpawn);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getEnforceRespawnAtWorldSpawn() {
        return configHandle.get(configNodes.ENFORCE_RESPAWN_AT_WORLD_SPAWN);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setUseCustomPortalSearch(boolean useDefaultPortalSearch) {
        return configHandle.set(configNodes.USE_CUSTOM_PORTAL_SEARCH, useDefaultPortalSearch);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUsingCustomPortalSearch() {
        return configHandle.get(configNodes.USE_CUSTOM_PORTAL_SEARCH);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setCustomPortalSearchRadius(int searchRadius) {
        return configHandle.set(configNodes.CUSTOM_PORTAL_SEARCH_RADIUS, searchRadius);
    }

    /**
     * {@inheritDoc}
     */
    public int getCustomPortalSearchRadius() {
        return configHandle.get(configNodes.CUSTOM_PORTAL_SEARCH_RADIUS);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setEnablePrefixChat(boolean prefixChat) {
        return configHandle.set(configNodes.ENABLE_CHAT_PREFIX, prefixChat);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnablePrefixChat() {
        return configHandle.get(configNodes.ENABLE_CHAT_PREFIX);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setPrefixChatFormat(String prefixChatFormat) {
        return configHandle.set(configNodes.CHAT_PREFIX_FORMAT, prefixChatFormat);
    }

    /**
     * {@inheritDoc}
     */
    public String getPrefixChatFormat() {
        return configHandle.get(configNodes.CHAT_PREFIX_FORMAT);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setRegisterPapiHook(boolean registerPapiHook) {
        return configHandle.set(configNodes.REGISTER_PAPI_HOOK, registerPapiHook);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRegisterPapiHook() {
        return configHandle.get(configNodes.REGISTER_PAPI_HOOK);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setDefaultLocale(Locale defaultLocale) {
        return configHandle.set(configNodes.DEFAULT_LOCALE, defaultLocale);
    }

    /**
     * {@inheritDoc}
     */
    public Locale getDefaultLocale() {
        return configHandle.get(configNodes.DEFAULT_LOCALE);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setPerPlayerLocale(boolean perPlayerLocale) {
        return configHandle.set(configNodes.PER_PLAYER_LOCALE, perPlayerLocale);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getPerPlayerLocale() {
        return configHandle.get(configNodes.PER_PLAYER_LOCALE);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setResolveAliasName(boolean resolveAliasInCommands) {
        return configHandle.set(configNodes.RESOLVE_ALIAS_NAME, resolveAliasInCommands);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getResolveAliasName() {
        return configHandle.get(configNodes.RESOLVE_ALIAS_NAME);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setConfirmMode(ConfirmMode confirmMode) {
        return configHandle.set(configNodes.CONFIRM_MODE, confirmMode);
    }

    /**
     * {@inheritDoc}
     */
    public ConfirmMode getConfirmMode() {
        return configHandle.get(configNodes.CONFIRM_MODE);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setUseConfirmOtp(boolean useConfirmOtp) {
        return configHandle.set(configNodes.USE_CONFIRM_OTP, useConfirmOtp);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUseConfirmOtp() {
        return configHandle.get(configNodes.USE_CONFIRM_OTP);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setGlobalDebug(int globalDebug) {
        return configHandle.set(configNodes.GLOBAL_DEBUG, globalDebug);
    }

    /**
     * {@inheritDoc}
     */
    public int getGlobalDebug() {
        return configHandle.get(configNodes.GLOBAL_DEBUG);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setDebugPermissions(boolean debugPermissions) {
        return configHandle.set(configNodes.DEBUG_PERMISSIONS, debugPermissions);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getDebugPermissions() {
        return configHandle.get(configNodes.DEBUG_PERMISSIONS);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setSilentStart(boolean silentStart) {
        return configHandle.set(configNodes.SILENT_START, silentStart);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getSilentStart() {
        return configHandle.get(configNodes.SILENT_START);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setShowDonateMessage(boolean showDonateMessage) {
        return configHandle.set(configNodes.SHOW_DONATION_MESSAGE, showDonateMessage);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isShowingDonateMessage() {
        return configHandle.get(configNodes.SHOW_DONATION_MESSAGE);
    }

    /**
     * Gets the underlying config file object
     *
     * @return The config file
     */
    public FileConfiguration getConfig() {
        return configHandle.getConfig();
    }
}
