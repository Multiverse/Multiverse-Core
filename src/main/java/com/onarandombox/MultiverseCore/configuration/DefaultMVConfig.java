package com.onarandombox.MultiverseCore.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.NewMVConfig;
import com.onarandombox.MultiverseCore.utils.settings.MVSettings;
import com.onarandombox.MultiverseCore.utils.settings.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.utils.settings.migration.InvertBoolMigratorAction;
import com.onarandombox.MultiverseCore.utils.settings.migration.MoveMigratorAction;
import com.onarandombox.MultiverseCore.utils.settings.migration.VersionMigrator;

public class DefaultMVConfig implements NewMVConfig {
    public static final String CONFIG_FILENAME = "config2.yml";

    private final Path configPath;
    private final MVSettings settings;

    public DefaultMVConfig(MultiverseCore core) {
        configPath = Path.of(core.getDataFolder().getPath(), CONFIG_FILENAME);

        migrateFromOldConfig();

        settings = MVSettings.builder(configPath)
                .logger(core.getLogger())
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

    private void migrateFromOldConfig() {
        String content;
        try {
            content = Files.readString(configPath);
        } catch (IOException e) {
            return;
        }
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
}
