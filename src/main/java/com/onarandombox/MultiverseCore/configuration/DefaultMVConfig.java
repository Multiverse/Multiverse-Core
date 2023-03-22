package com.onarandombox.MultiverseCore.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVConfig;
import com.onarandombox.MultiverseCore.utils.settings.MVSettings;
import com.onarandombox.MultiverseCore.utils.settings.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.utils.settings.migration.InvertBoolMigratorAction;
import com.onarandombox.MultiverseCore.utils.settings.migration.MoveMigratorAction;
import com.onarandombox.MultiverseCore.utils.settings.migration.VersionMigrator;

public class DefaultMVConfig implements MVConfig {
    public static final String CONFIG_FILENAME = "config.yml";
    public static final double CONFIG_VERSION = 5.0;

    private final Path configPath;
    private final MVSettings settings;

    public DefaultMVConfig(MultiverseCore core) {
        configPath = Path.of(core.getDataFolder().getPath(), CONFIG_FILENAME);

        migrateFromOldConfigFile();

        settings = MVSettings.builder(configPath)
                .logger(Logging.getLogger())
                .defaultNodes(MVConfigNodes.getNodes())
                .migrator(ConfigMigrator.builder(MVConfigNodes.VERSION)
                        .addVersionMigrator(VersionMigrator.builder(5.0)
                                .addAction(MoveMigratorAction.of("multiverse-configuration.enforceaccess", "world.enforce-access"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.prefixchat", "messaging.enable-chat-prefix"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.prefixchatformat", "messaging.chat-prefix-format"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.teleportintercept", "world.teleport-intercept"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.firstspawnoverride", "spawn.first-spawn-override"))
                                //.addAction(MoveMigratorAction.of("multiverse-configuration.displaypermerrors", ""))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.globaldebug", "misc.global-debug"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.silentstart", "misc.silent-start"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.firstspawnworld", "worlds.first-spawn-location"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.defaultportalsearch", "portals.use-custom-portal-search"))
                                .addAction(InvertBoolMigratorAction.of("portals.use-custom-portal-search"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.portalsearchradius", "portals.custom-portal-search-radius"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.autopurge", "world.auto-purge-entities"))
                                .addAction(MoveMigratorAction.of("multiverse-configuration.idonotwanttodonate", "misc.i-dont-want-to-donate"))
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
        if (content.contains("version: 2.5")) {
            content = content.replace("==: com.onarandombox.MultiverseCore.MultiverseCoreConfiguration", "");
        }
        try {
            Files.writeString(configPath, content);
        } catch (IOException e) {
            // ignore
        }
    }

    public boolean load() {
        return settings.load();
    }

    public void save() {
        settings.save();
    }

    @Override
    public void setEnforceAccess(boolean enforceAccess) {
        settings.set(MVConfigNodes.ENFORCE_ACCESS, enforceAccess);
    }

    @Override
    public boolean getEnforceAccess() {
        return settings.get(MVConfigNodes.ENFORCE_ACCESS);
    }

    @Override
    public void setAutoPurgeEnabled(boolean autopurge) {
        settings.set(MVConfigNodes.AUTO_PURGE_ENTITIES, autopurge);
    }

    @Override
    public boolean isAutoPurgeEnabled() {
        return settings.get(MVConfigNodes.AUTO_PURGE_ENTITIES);
    }

    @Override
    public void setTeleportIntercept(boolean teleportIntercept) {
        settings.set(MVConfigNodes.TELEPORT_INTERCEPT, teleportIntercept);
    }

    @Override
    public boolean getTeleportIntercept() {
        return settings.get(MVConfigNodes.TELEPORT_INTERCEPT);
    }

    @Override
    public void setFirstSpawnOverride(boolean firstSpawnOverride) {
        settings.set(MVConfigNodes.FIRST_SPAWN_OVERRIDE, firstSpawnOverride);
    }

    @Override
    public boolean getFirstSpawnOverride() {
        return settings.get(MVConfigNodes.FIRST_SPAWN_OVERRIDE);
    }

    @Override
    public void setFirstSpawnWorld(String firstSpawnWorld) {
        settings.set(MVConfigNodes.FIRST_SPAWN_LOCATION, firstSpawnWorld);
    }

    @Override
    public String getFirstSpawnWorld() {
        return settings.get(MVConfigNodes.FIRST_SPAWN_LOCATION);
    }

    @Override
    public void setUseDefaultPortalSearch(boolean useDefaultPortalSearch) {
        settings.set(MVConfigNodes.USE_CUSTOM_PORTAL_SEARCH, !useDefaultPortalSearch);
    }

    @Override
    public boolean isUsingDefaultPortalSearch() {
        return !settings.get(MVConfigNodes.USE_CUSTOM_PORTAL_SEARCH);
    }

    @Override
    public void setPortalSearchRadius(int searchRadius) {
        settings.set(MVConfigNodes.CUSTOM_PORTAL_SEARCH_RADIUS, searchRadius);
    }

    @Override
    public int getPortalSearchRadius() {
        return settings.get(MVConfigNodes.CUSTOM_PORTAL_SEARCH_RADIUS);
    }

    @Override
    public void setPrefixChat(boolean prefixChat) {
        settings.set(MVConfigNodes.ENABLE_CHAT_PREFIX, prefixChat);
    }

    @Override
    public boolean getPrefixChat() {
        return settings.get(MVConfigNodes.ENABLE_CHAT_PREFIX);
    }

    @Override
    public void setPrefixChatFormat(String prefixChatFormat) {
        settings.set(MVConfigNodes.CHAT_PREFIX_FORMAT, prefixChatFormat);
    }

    @Override
    public String getPrefixChatFormat() {
        return settings.get(MVConfigNodes.CHAT_PREFIX_FORMAT);
    }

    @Override
    public void setGlobalDebug(int globalDebug) {
        settings.set(MVConfigNodes.GLOBAL_DEBUG, globalDebug);
    }

    @Override
    public int getGlobalDebug() {
        return settings.get(MVConfigNodes.GLOBAL_DEBUG);
    }

    @Override
    public void setSilentStart(boolean silentStart) {
        settings.set(MVConfigNodes.SILENT_START, silentStart);
    }

    @Override
    public boolean getSilentStart() {
        return settings.get(MVConfigNodes.SILENT_START);
    }

    @Override
    public void setShowDonateMessage(boolean idonotwanttodonate) {
        settings.set(MVConfigNodes.I_DONT_WANT_TO_DONATE, idonotwanttodonate);
    }

    @Override
    public boolean isShowingDonateMessage() {
        return settings.get(MVConfigNodes.I_DONT_WANT_TO_DONATE);
    }
}
