package com.onarandombox.MultiverseCore.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVConfig;
import com.onarandombox.MultiverseCore.configuration.migration.BooleanMigratorAction;
import com.onarandombox.MultiverseCore.configuration.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.configuration.migration.IntegerMigratorAction;
import com.onarandombox.MultiverseCore.configuration.migration.InvertBoolMigratorAction;
import com.onarandombox.MultiverseCore.configuration.migration.MoveMigratorAction;
import com.onarandombox.MultiverseCore.configuration.migration.VersionMigrator;

public class DefaultMVConfig implements MVConfig {
    public static final String CONFIG_FILENAME = "config.yml";
    public static final double CONFIG_VERSION = 5.0;

    /**
     * Creates a new DefaultMVConfig instance and loads the configuration automatically.
     *
     * @param core The MultiverseCore instance.
     * @return The new DefaultMVConfig instance.
     */
    public static DefaultMVConfig init(MultiverseCore core) {
        var config = new DefaultMVConfig(core);
        config.load();
        config.save();
        return config;
    }

    private final Path configPath;
    private final ConfigHandle configHandle;

    public DefaultMVConfig(MultiverseCore core) {
        configPath = Path.of(core.getDataFolder().getPath(), CONFIG_FILENAME);

        migrateFromOldConfigFile();

        configHandle = ConfigHandle.builder(configPath)
                .logger(Logging.getLogger())
                .nodes(MVConfigNodes.getNodes())
                .migrator(ConfigMigrator.builder(MVConfigNodes.VERSION)
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
                        .build())
                .build();
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
    public boolean load() {
        return configHandle.load();
    }

    @Override
    public void save() {
        configHandle.save();
    }

    @Override
    public Object getProperty(String name) {
        return configHandle.get(name);
    }

    @Override
    public boolean setProperty(String name, Object value) {
        return configHandle.set(name, value);
    }

    @Override
    public void setEnforceAccess(boolean enforceAccess) {
        configHandle.set(MVConfigNodes.ENFORCE_ACCESS, enforceAccess);
    }

    @Override
    public boolean getEnforceAccess() {
        return configHandle.get(MVConfigNodes.ENFORCE_ACCESS);
    }

    @Override
    public void setEnforceGameMode(boolean enforceGameMode) {
        configHandle.set(MVConfigNodes.ENFORCE_GAMEMODE, enforceGameMode);
    }

    @Override
    public boolean getEnforceGameMode() {
        return configHandle.get(MVConfigNodes.ENFORCE_GAMEMODE);
    }

    @Override
    public void setAutoPurgeEntities(boolean autopurge) {
        configHandle.set(MVConfigNodes.AUTO_PURGE_ENTITIES, autopurge);
    }

    @Override
    public boolean isAutoPurgeEntities() {
        return configHandle.get(MVConfigNodes.AUTO_PURGE_ENTITIES);
    }

    @Override
    public void setTeleportIntercept(boolean teleportIntercept) {
        configHandle.set(MVConfigNodes.TELEPORT_INTERCEPT, teleportIntercept);
    }

    @Override
    public boolean getTeleportIntercept() {
        return configHandle.get(MVConfigNodes.TELEPORT_INTERCEPT);
    }

    @Override
    public void setFirstSpawnOverride(boolean firstSpawnOverride) {
        configHandle.set(MVConfigNodes.FIRST_SPAWN_OVERRIDE, firstSpawnOverride);
    }

    @Override
    public boolean getFirstSpawnOverride() {
        return configHandle.get(MVConfigNodes.FIRST_SPAWN_OVERRIDE);
    }

    @Override
    public void setFirstSpawnLocation(String firstSpawnWorld) {
        configHandle.set(MVConfigNodes.FIRST_SPAWN_LOCATION, firstSpawnWorld);
    }

    @Override
    public String getFirstSpawnLocation() {
        return configHandle.get(MVConfigNodes.FIRST_SPAWN_LOCATION);
    }

    @Override
    public void setUseCustomPortalSearch(boolean useDefaultPortalSearch) {
        configHandle.set(MVConfigNodes.USE_CUSTOM_PORTAL_SEARCH, useDefaultPortalSearch);
    }

    @Override
    public boolean isUsingCustomPortalSearch() {
        return configHandle.get(MVConfigNodes.USE_CUSTOM_PORTAL_SEARCH);
    }

    @Override
    public void setCustomPortalSearchRadius(int searchRadius) {
        configHandle.set(MVConfigNodes.CUSTOM_PORTAL_SEARCH_RADIUS, searchRadius);
    }

    @Override
    public int getCustomPortalSearchRadius() {
        return configHandle.get(MVConfigNodes.CUSTOM_PORTAL_SEARCH_RADIUS);
    }

    @Override
    public void setEnablePrefixChat(boolean prefixChat) {
        configHandle.set(MVConfigNodes.ENABLE_CHAT_PREFIX, prefixChat);
    }

    @Override
    public boolean isEnablePrefixChat() {
        return configHandle.get(MVConfigNodes.ENABLE_CHAT_PREFIX);
    }

    @Override
    public void setPrefixChatFormat(String prefixChatFormat) {
        configHandle.set(MVConfigNodes.CHAT_PREFIX_FORMAT, prefixChatFormat);
    }

    @Override
    public String getPrefixChatFormat() {
        return configHandle.get(MVConfigNodes.CHAT_PREFIX_FORMAT);
    }

    @Override
    public void setRegisterPapiHook(boolean registerPapiHook) {
        configHandle.set(MVConfigNodes.REGISTER_PAPI_HOOK, registerPapiHook);
    }

    @Override
    public boolean isRegisterPapiHook() {
        return configHandle.get(MVConfigNodes.REGISTER_PAPI_HOOK);
    }

    @Override
    public void setGlobalDebug(int globalDebug) {
        configHandle.set(MVConfigNodes.GLOBAL_DEBUG, globalDebug);
    }

    @Override
    public int getGlobalDebug() {
        return configHandle.get(MVConfigNodes.GLOBAL_DEBUG);
    }

    @Override
    public void setSilentStart(boolean silentStart) {
        configHandle.set(MVConfigNodes.SILENT_START, silentStart);
    }

    @Override
    public boolean getSilentStart() {
        return configHandle.get(MVConfigNodes.SILENT_START);
    }

    @Override
    public void setShowDonateMessage(boolean showDonateMessage) {
        configHandle.set(MVConfigNodes.SHOW_DONATION_MESSAGE, showDonateMessage);
    }

    @Override
    public boolean isShowingDonateMessage() {
        return configHandle.get(MVConfigNodes.SHOW_DONATION_MESSAGE);
    }
}
