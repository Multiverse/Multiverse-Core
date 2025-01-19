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
import org.mvplugins.multiverse.core.api.config.MVCoreConfig;
import org.mvplugins.multiverse.core.api.configuration.StringPropertyHandle;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.api.commandtools.ConfirmMode;
import org.mvplugins.multiverse.core.configuration.handle.CommentedConfigurationHandle;
import org.mvplugins.multiverse.core.configuration.handle.SimpleStringPropertyHandle;
import org.mvplugins.multiverse.core.configuration.migration.BooleanMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.configuration.migration.IntegerMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.InvertBoolMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.MoveMigratorAction;
import org.mvplugins.multiverse.core.configuration.migration.VersionMigrator;

@Service
public class SimpleMVCoreConfig implements MVCoreConfig {
    public static final String CONFIG_FILENAME = "config.yml";

    private final Path configPath;
    private final MVCoreConfigNodes configNodes;
    private final CommentedConfigurationHandle configHandle;
    private final StringPropertyHandle stringPropertyHandle;

    @Inject
    SimpleMVCoreConfig(
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
        this.stringPropertyHandle = new SimpleStringPropertyHandle(configHandle);
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

    @Override
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
    @Override
    public boolean isLoaded() {
        return configHandle.isLoaded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> save() {
        return configHandle.save();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringPropertyHandle getStringPropertyHandle() {
        return stringPropertyHandle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setEnforceAccess(boolean enforceAccess) {
        return configHandle.set(configNodes.ENFORCE_ACCESS, enforceAccess);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getEnforceAccess() {
        return configHandle.get(configNodes.ENFORCE_ACCESS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setEnforceGameMode(boolean enforceGameMode) {
        return configHandle.set(configNodes.ENFORCE_GAMEMODE, enforceGameMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getEnforceGameMode() {
        return configHandle.get(configNodes.ENFORCE_GAMEMODE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setAutoPurgeEntities(boolean autopurge) {
        return configHandle.set(configNodes.AUTO_PURGE_ENTITIES, autopurge);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAutoPurgeEntities() {
        return configHandle.get(configNodes.AUTO_PURGE_ENTITIES);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setUseFinerTeleportPermissions(boolean useFinerTeleportPermissions) {
        return configHandle.set(configNodes.USE_FINER_TELEPORT_PERMISSIONS, useFinerTeleportPermissions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getUseFinerTeleportPermissions() {
        return configHandle.get(configNodes.USE_FINER_TELEPORT_PERMISSIONS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setConcurrentTeleportLimit(int concurrentTeleportLimit) {
        return configHandle.set(configNodes.CONCURRENT_TELEPORT_LIMIT, concurrentTeleportLimit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getConcurrentTeleportLimit() {
        return configHandle.get(configNodes.CONCURRENT_TELEPORT_LIMIT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setTeleportIntercept(boolean teleportIntercept) {
        return configHandle.set(configNodes.TELEPORT_INTERCEPT, teleportIntercept);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getTeleportIntercept() {
        return configHandle.get(configNodes.TELEPORT_INTERCEPT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setFirstSpawnOverride(boolean firstSpawnOverride) {
        return configHandle.set(configNodes.FIRST_SPAWN_OVERRIDE, firstSpawnOverride);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setSafeLocationHorizontalSearchRadius(int searchRadius) {
        return configHandle.set(configNodes.SAFE_LOCATION_HORIZONTAL_SEARCH_RADIUS, searchRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSafeLocationHorizontalSearchRadius() {
        return configHandle.get(configNodes.SAFE_LOCATION_HORIZONTAL_SEARCH_RADIUS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setSafeLocationVerticalSearchRadius(int searchRadius) {
        return configHandle.set(configNodes.SAFE_LOCATION_VERTICAL_SEARCH_RADIUS, searchRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSafeLocationVerticalSearchRadius() {
        return configHandle.get(configNodes.SAFE_LOCATION_VERTICAL_SEARCH_RADIUS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getFirstSpawnOverride() {
        return configHandle.get(configNodes.FIRST_SPAWN_OVERRIDE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setFirstSpawnLocation(String firstSpawnWorld) {
        return configHandle.set(configNodes.FIRST_SPAWN_LOCATION, firstSpawnWorld);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFirstSpawnLocation() {
        return configHandle.get(configNodes.FIRST_SPAWN_LOCATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setEnableJoinDestination(boolean enableJoinDestination) {
        return configHandle.set(configNodes.ENABLE_JOIN_DESTINATION, enableJoinDestination);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getEnableJoinDestination() {
        return  configHandle.get(configNodes.ENABLE_JOIN_DESTINATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setJoinDestination(String alwaysSpawnDestination) {
        return configHandle.set(configNodes.JOIN_DESTINATION, alwaysSpawnDestination);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJoinDestination() {
        return  configHandle.get(configNodes.JOIN_DESTINATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setDefaultRespawnWithinSameWorld(boolean defaultRespawnToWorldSpawn) {
        return configHandle.set(configNodes.DEFAULT_RESPAWN_WITHIN_SAME_WORLD, defaultRespawnToWorldSpawn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getDefaultRespawnWithinSameWorld() {
        return configHandle.get(configNodes.DEFAULT_RESPAWN_WITHIN_SAME_WORLD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setEnforceRespawnAtWorldSpawn(boolean enforceRespawnAtWorldSpawn) {
        return configHandle.set(configNodes.ENFORCE_RESPAWN_AT_WORLD_SPAWN, enforceRespawnAtWorldSpawn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getEnforceRespawnAtWorldSpawn() {
        return configHandle.get(configNodes.ENFORCE_RESPAWN_AT_WORLD_SPAWN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setUseCustomPortalSearch(boolean useDefaultPortalSearch) {
        return configHandle.set(configNodes.USE_CUSTOM_PORTAL_SEARCH, useDefaultPortalSearch);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUsingCustomPortalSearch() {
        return configHandle.get(configNodes.USE_CUSTOM_PORTAL_SEARCH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setCustomPortalSearchRadius(int searchRadius) {
        return configHandle.set(configNodes.CUSTOM_PORTAL_SEARCH_RADIUS, searchRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCustomPortalSearchRadius() {
        return configHandle.get(configNodes.CUSTOM_PORTAL_SEARCH_RADIUS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setEnablePrefixChat(boolean prefixChat) {
        return configHandle.set(configNodes.ENABLE_CHAT_PREFIX, prefixChat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnablePrefixChat() {
        return configHandle.get(configNodes.ENABLE_CHAT_PREFIX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setPrefixChatFormat(String prefixChatFormat) {
        return configHandle.set(configNodes.CHAT_PREFIX_FORMAT, prefixChatFormat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPrefixChatFormat() {
        return configHandle.get(configNodes.CHAT_PREFIX_FORMAT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setRegisterPapiHook(boolean registerPapiHook) {
        return configHandle.set(configNodes.REGISTER_PAPI_HOOK, registerPapiHook);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRegisterPapiHook() {
        return configHandle.get(configNodes.REGISTER_PAPI_HOOK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setDefaultLocale(Locale defaultLocale) {
        return configHandle.set(configNodes.DEFAULT_LOCALE, defaultLocale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getDefaultLocale() {
        return configHandle.get(configNodes.DEFAULT_LOCALE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setPerPlayerLocale(boolean perPlayerLocale) {
        return configHandle.set(configNodes.PER_PLAYER_LOCALE, perPlayerLocale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getPerPlayerLocale() {
        return configHandle.get(configNodes.PER_PLAYER_LOCALE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setResolveAliasName(boolean resolveAliasInCommands) {
        return configHandle.set(configNodes.RESOLVE_ALIAS_NAME, resolveAliasInCommands);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getResolveAliasName() {
        return configHandle.get(configNodes.RESOLVE_ALIAS_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setConfirmMode(ConfirmMode confirmMode) {
        return configHandle.set(configNodes.CONFIRM_MODE, confirmMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConfirmMode getConfirmMode() {
        return configHandle.get(configNodes.CONFIRM_MODE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setUseConfirmOtp(boolean useConfirmOtp) {
        return configHandle.set(configNodes.USE_CONFIRM_OTP, useConfirmOtp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getUseConfirmOtp() {
        return configHandle.get(configNodes.USE_CONFIRM_OTP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setGlobalDebug(int globalDebug) {
        return configHandle.set(configNodes.GLOBAL_DEBUG, globalDebug);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGlobalDebug() {
        return configHandle.get(configNodes.GLOBAL_DEBUG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setDebugPermissions(boolean debugPermissions) {
        return configHandle.set(configNodes.DEBUG_PERMISSIONS, debugPermissions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getDebugPermissions() {
        return configHandle.get(configNodes.DEBUG_PERMISSIONS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setSilentStart(boolean silentStart) {
        return configHandle.set(configNodes.SILENT_START, silentStart);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getSilentStart() {
        return configHandle.get(configNodes.SILENT_START);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> setShowDonateMessage(boolean showDonateMessage) {
        return configHandle.set(configNodes.SHOW_DONATION_MESSAGE, showDonateMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
