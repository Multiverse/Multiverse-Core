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
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.queue.ConfirmMode;
import org.mvplugins.multiverse.core.config.handle.CommentedConfigurationHandle;
import org.mvplugins.multiverse.core.config.handle.StringPropertyHandle;
import org.mvplugins.multiverse.core.config.migration.action.BooleanMigratorAction;
import org.mvplugins.multiverse.core.config.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.config.migration.action.IntegerMigratorAction;
import org.mvplugins.multiverse.core.config.migration.action.InvertBoolMigratorAction;
import org.mvplugins.multiverse.core.config.migration.action.MoveMigratorAction;
import org.mvplugins.multiverse.core.config.migration.VersionMigrator;
import org.mvplugins.multiverse.core.config.migration.action.SetMigratorAction;
import org.mvplugins.multiverse.core.destination.DestinationsProvider;
import org.mvplugins.multiverse.core.teleportation.PassengerMode;
import org.mvplugins.multiverse.core.teleportation.PassengerModes;
import org.mvplugins.multiverse.core.world.helpers.DimensionFinder.DimensionFormat;

@Service
public final class CoreConfig {
    public static final String CONFIG_FILENAME = "config.yml";

    private final Path configPath;
    private final CoreConfigNodes configNodes;
    private final CommentedConfigurationHandle configHandle;
    private final StringPropertyHandle stringPropertyHandle;

    @Inject
    CoreConfig(
            @NotNull MultiverseCore core,
            @NotNull CoreConfigNodes configNodes
    ) {
        this.configPath = Path.of(core.getDataFolder().getPath(), CONFIG_FILENAME);
        this.configNodes = configNodes;
        this.configHandle = CommentedConfigurationHandle.builder(configPath, configNodes.getNodes())
                .logger(Logging.getLogger())
                .migrator(ConfigMigrator.builder(configNodes.version)
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
                                .addAction(MoveMigratorAction.of("multiverse-configuration.displaypermerrors", "misc.debug-permissions"))
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
                                .addAction(SetMigratorAction.of("command.show-legacy-aliases", true))
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

    public Try<Void> setAutoImportDefaultWorlds(boolean autoImportDefaultWorlds) {
        return configHandle.set(configNodes.autoImportDefaultWorlds, autoImportDefaultWorlds);
    }

    public boolean getAutoImportDefaultWorlds() {
        return configHandle.get(configNodes.autoImportDefaultWorlds);
    }

    public Try<Void> setAutoImport3rdPartyWorlds(boolean autoImport3rdPartyWorlds) {
        return configHandle.set(configNodes.autoImport3rdPartyWorlds, autoImport3rdPartyWorlds);
    }

    public boolean getAutoImport3rdPartyWorlds() {
        return configHandle.get(configNodes.autoImport3rdPartyWorlds);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setEnforceAccess(boolean enforceAccess) {
        return configHandle.set(configNodes.enforceAccess, enforceAccess);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getEnforceAccess() {
        return configHandle.get(configNodes.enforceAccess);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setEnforceGameMode(boolean enforceGameMode) {
        return configHandle.set(configNodes.enforceGamemode, enforceGameMode);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getEnforceGameMode() {
        return configHandle.get(configNodes.enforceGamemode);
    }

    public Try<Void> setEnforceFlight(boolean enforceFlight) {
        return configHandle.set(configNodes.enforceFlight, enforceFlight);
    }

    public boolean getEnforceFlight() {
        return configHandle.get(configNodes.enforceFlight);
    }

    @ApiStatus.AvailableSince("5.3")
    public Try<Void> setApplyEntitySpawnRate(boolean applyEntitySpawnRate) {
        return configHandle.set(configNodes.applyEntitySpawnRate, applyEntitySpawnRate);
    }

    @ApiStatus.AvailableSince("5.3")
    public boolean getApplyEntitySpawnRate() {
        return configHandle.get(configNodes.applyEntitySpawnRate);
    }

    @ApiStatus.AvailableSince("5.3")
    public Try<Void> setApplyEntitySpawnLimit(boolean applyEntitySpawnLimit) {
        return configHandle.set(configNodes.applyEntitySpawnLimit, applyEntitySpawnLimit);
    }

    @ApiStatus.AvailableSince("5.3")
    public boolean getApplyEntitySpawnLimit() {
        return configHandle.get(configNodes.applyEntitySpawnLimit);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setAutoPurgeEntities(boolean autopurge) {
        return configHandle.set(configNodes.autoPurgeEntities, autopurge);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAutoPurgeEntities() {
        return configHandle.get(configNodes.autoPurgeEntities);
    }

    public Try<Void> setNetherWorldNameFormat(DimensionFormat netherWorldNameFormat) {
        return configHandle.set(configNodes.netherWorldNameFormat, netherWorldNameFormat);
    }

    public DimensionFormat getNetherWorldNameFormat() {
        return configHandle.get(configNodes.netherWorldNameFormat);
    }

    public Try<Void> setEndWorldNameFormat(DimensionFormat endWorldNameFormat) {
        return configHandle.set(configNodes.endWorldNameFormat, endWorldNameFormat);
    }

    public DimensionFormat getEndWorldNameFormat() {
        return configHandle.get(configNodes.endWorldNameFormat);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setUseFinerTeleportPermissions(boolean useFinerTeleportPermissions) {
        return configHandle.set(configNodes.useFinerTeleportPermissions, useFinerTeleportPermissions);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUseFinerTeleportPermissions() {
        return configHandle.get(configNodes.useFinerTeleportPermissions);
    }

    /**
     * Sets the passenger mode
     *
     * @param passengerMode The passenger mode
     * @return The set result
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public Try<Void> setPassengerMode(PassengerModes passengerMode) {
        return configHandle.set(configNodes.passengerMode, passengerMode);
    }

    /**
     * Gets the passenger mode
     *
     * @return The passenger mode
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public PassengerMode getPassengerMode() {
        return configHandle.get(configNodes.passengerMode);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setConcurrentTeleportLimit(int concurrentTeleportLimit) {
        return configHandle.set(configNodes.concurrentTeleportLimit, concurrentTeleportLimit);
    }

    /**
     * {@inheritDoc}
     */
    public int getConcurrentTeleportLimit() {
        return configHandle.get(configNodes.concurrentTeleportLimit);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setTeleportIntercept(boolean teleportIntercept) {
        return configHandle.set(configNodes.teleportIntercept, teleportIntercept);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getTeleportIntercept() {
        return configHandle.get(configNodes.teleportIntercept);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setFirstSpawnOverride(boolean firstSpawnOverride) {
        return configHandle.set(configNodes.firstSpawnOverride, firstSpawnOverride);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setSafeLocationHorizontalSearchRadius(int searchRadius) {
        return configHandle.set(configNodes.safeLocationHorizontalSearchRadius, searchRadius);
    }

    /**
     * {@inheritDoc}
     */
    public int getSafeLocationHorizontalSearchRadius() {
        return configHandle.get(configNodes.safeLocationHorizontalSearchRadius);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setSafeLocationVerticalSearchRadius(int searchRadius) {
        return configHandle.set(configNodes.safeLocationVerticalSearchRadius, searchRadius);
    }

    /**
     * {@inheritDoc}
     */
    public int getSafeLocationVerticalSearchRadius() {
        return configHandle.get(configNodes.safeLocationVerticalSearchRadius);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getFirstSpawnOverride() {
        return configHandle.get(configNodes.firstSpawnOverride);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setFirstSpawnLocation(String firstSpawnWorld) {
        return configHandle.set(configNodes.firstSpawnLocation, firstSpawnWorld);
    }

    /**
     * {@inheritDoc}
     */
    public String getFirstSpawnLocation() {
        return configHandle.get(configNodes.firstSpawnLocation);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setEnableJoinDestination(boolean enableJoinDestination) {
        return configHandle.set(configNodes.enableJoinDestination, enableJoinDestination);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getEnableJoinDestination() {
        return  configHandle.get(configNodes.enableJoinDestination);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setJoinDestination(String alwaysSpawnDestination) {
        return configHandle.set(configNodes.joinDestination, alwaysSpawnDestination);
    }

    /**
     * {@inheritDoc}
     */
    public String getJoinDestination() {
        return  configHandle.get(configNodes.joinDestination);
    }

    public Try<Void> setDefaultRespawnInOverworld(boolean defaultRespawnInOverworld) {
        return configHandle.set(configNodes.defaultRespawnInOverworld, defaultRespawnInOverworld);
    }

    public boolean getDefaultRespawnInOverworld() {
        return configHandle.get(configNodes.defaultRespawnInOverworld);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setDefaultRespawnWithinSameWorld(boolean defaultRespawnToWorldSpawn) {
        return configHandle.set(configNodes.defaultRespawnWithinSameWorld, defaultRespawnToWorldSpawn);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getDefaultRespawnWithinSameWorld() {
        return configHandle.get(configNodes.defaultRespawnWithinSameWorld);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setEnforceRespawnAtWorldSpawn(boolean enforceRespawnAtWorldSpawn) {
        return configHandle.set(configNodes.enforceRespawnAtWorldSpawn, enforceRespawnAtWorldSpawn);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getEnforceRespawnAtWorldSpawn() {
        return configHandle.get(configNodes.enforceRespawnAtWorldSpawn);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setUseCustomPortalSearch(boolean useDefaultPortalSearch) {
        return configHandle.set(configNodes.useCustomPortalSearch, useDefaultPortalSearch);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isUsingCustomPortalSearch() {
        return configHandle.get(configNodes.useCustomPortalSearch);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setCustomPortalSearchRadius(int searchRadius) {
        return configHandle.set(configNodes.customPortalSearchRadius, searchRadius);
    }

    /**
     * {@inheritDoc}
     */
    public int getCustomPortalSearchRadius() {
        return configHandle.get(configNodes.customPortalSearchRadius);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setEnablePrefixChat(boolean prefixChat) {
        return configHandle.set(configNodes.enableChatPrefix, prefixChat);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnablePrefixChat() {
        return configHandle.get(configNodes.enableChatPrefix);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setPrefixChatFormat(String prefixChatFormat) {
        return configHandle.set(configNodes.chatPrefixFormat, prefixChatFormat);
    }

    /**
     * {@inheritDoc}
     */
    public String getPrefixChatFormat() {
        return configHandle.get(configNodes.chatPrefixFormat);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setRegisterPapiHook(boolean registerPapiHook) {
        return configHandle.set(configNodes.registerPapiHook, registerPapiHook);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRegisterPapiHook() {
        return configHandle.get(configNodes.registerPapiHook);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setDefaultLocale(Locale defaultLocale) {
        return configHandle.set(configNodes.defaultLocale, defaultLocale);
    }

    /**
     * {@inheritDoc}
     */
    public Locale getDefaultLocale() {
        return configHandle.get(configNodes.defaultLocale);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setPerPlayerLocale(boolean perPlayerLocale) {
        return configHandle.set(configNodes.perPlayerLocale, perPlayerLocale);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getPerPlayerLocale() {
        return configHandle.get(configNodes.perPlayerLocale);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setResolveAliasName(boolean resolveAliasInCommands) {
        return configHandle.set(configNodes.resolveAliasName, resolveAliasInCommands);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getResolveAliasName() {
        return configHandle.get(configNodes.resolveAliasName);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setConfirmMode(ConfirmMode confirmMode) {
        return configHandle.set(configNodes.confirmMode, confirmMode);
    }

    /**
     * {@inheritDoc}
     */
    public ConfirmMode getConfirmMode() {
        return configHandle.get(configNodes.confirmMode);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setUseConfirmOtp(boolean useConfirmOtp) {
        return configHandle.set(configNodes.useConfirmOtp, useConfirmOtp);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUseConfirmOtp() {
        return configHandle.get(configNodes.useConfirmOtp);
    }

    public Integer getConfirmTimeout() {
        return configHandle.get(configNodes.confirmTimeout);
    }

    public Try<Void> setConfirmTimeout(int confirmTimeout) {
        return configHandle.set(configNodes.confirmTimeout, confirmTimeout);
    }

    public boolean getShowLegacyAliases() {
        return configHandle.get(configNodes.showLegacyAliases);
    }

    public Try<Void> setShowLegacyAliases(boolean showLegacyAliases) {
        return configHandle.set(configNodes.showLegacyAliases, showLegacyAliases);
    }

    public Try<Void> setEventPriorityPlayerPortal(EventPriority eventPriorityPlayerPortal) {
        return configHandle.set(configNodes.eventPriorityPlayerPortal, eventPriorityPlayerPortal);
    }

    public EventPriority getEventPriorityPlayerPortal() {
        return configHandle.get(configNodes.eventPriorityPlayerPortal);
    }

    public Try<Void> setEventPriorityPlayerRespawn(EventPriority eventPriorityPlayerRespawn) {
        return configHandle.set(configNodes.eventPriorityPlayerRespawn, eventPriorityPlayerRespawn);
    }

    public EventPriority getEventPriorityPlayerRespawn() {
        return configHandle.get(configNodes.eventPriorityPlayerRespawn);
    }

    public Try<Void> getEventPriorityPlayerSpawnLocation(EventPriority eventPriorityPlayerSpawnLocation) {
        return configHandle.set(configNodes.eventPriorityPlayerSpawnLocation, eventPriorityPlayerSpawnLocation);
    }

    public EventPriority getEventPriorityPlayerSpawnLocation() {
        return configHandle.get(configNodes.eventPriorityPlayerSpawnLocation);
    }

    public Try<Void> setEventPriorityPlayerTeleport(EventPriority eventPriorityPlayerTeleport) {
        return configHandle.set(configNodes.eventPriorityPlayerTeleport, eventPriorityPlayerTeleport);
    }

    public EventPriority getEventPriorityPlayerTeleport() {
        return configHandle.get(configNodes.eventPriorityPlayerTeleport);
    }

    public Try<Void> setBukkitYmlPath(String bukkitYmlPath) {
        return configHandle.set(configNodes.bukkitYmlPath, bukkitYmlPath);
    }

    public String getBukkitYmlPath() {
        return configHandle.get(configNodes.bukkitYmlPath);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setGlobalDebug(int globalDebug) {
        return configHandle.set(configNodes.globalDebug, globalDebug);
    }

    /**
     * {@inheritDoc}
     */
    public int getGlobalDebug() {
        return configHandle.get(configNodes.globalDebug);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setDebugPermissions(boolean debugPermissions) {
        return configHandle.set(configNodes.debugPermissions, debugPermissions);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getDebugPermissions() {
        return configHandle.get(configNodes.debugPermissions);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setSilentStart(boolean silentStart) {
        return configHandle.set(configNodes.silentStart, silentStart);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getSilentStart() {
        return configHandle.get(configNodes.silentStart);
    }

    /**
     * {@inheritDoc}
     */
    public Try<Void> setShowDonateMessage(boolean showDonateMessage) {
        return configHandle.set(configNodes.showDonationMessage, showDonateMessage);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isShowingDonateMessage() {
        return configHandle.get(configNodes.showDonationMessage);
    }

    /**
     * Gets the underlying config file object. For internal use only.
     *
     * @return The config file
     */
    @ApiStatus.Internal
    public FileConfiguration getConfig() {
        return configHandle.getConfig();
    }
}
