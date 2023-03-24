package com.onarandombox.MultiverseCore.utils.settings.migration;

import java.util.ArrayList;
import java.util.List;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.utils.settings.MVSettings;
import io.github.townyadvanced.commentedconfiguration.setting.TypedValueNode;

public class ConfigMigrator {
    public static Builder builder(TypedValueNode<Double> versionNode) {
        return new Builder(versionNode);
    }

    private final TypedValueNode<Double> versionNode;
    private final List<VersionMigrator> versionMigrators;

    public ConfigMigrator(TypedValueNode<Double> versionNode, List<VersionMigrator> versionMigrators) {
        this.versionNode = versionNode;
        this.versionMigrators = versionMigrators;
    }

    public void migrate(MVSettings settings) {
        double versionNumber = settings.get(versionNode);
        for (VersionMigrator versionMigrator : versionMigrators) {
            if (versionNumber < versionMigrator.getVersion()) {
                Logging.config("Migrating config from version %s to %s...", versionNumber, versionMigrator.getVersion());
                versionMigrator.migrate(settings);
            }
        }

        settings.setDefault(versionNode);
    }

    public static class Builder {
        private final TypedValueNode<Double> versionNode;
        private final List<VersionMigrator> versionMigrators;

        /**
         * Creates a new builder for a ConfigMigrator.
         *
         * @param versionNode   The node that stores the version number of the config.
         *                      Default value should be the current latest version number.
         */
        public Builder(TypedValueNode<Double> versionNode) {
            this.versionNode = versionNode;
            this.versionMigrators = new ArrayList<>();
        }

        public Builder addVersionMigrator(VersionMigrator versionMigrator) {
            versionMigrators.add(versionMigrator);
            return this;
        }

        public ConfigMigrator build() {
            return new ConfigMigrator(versionNode, versionMigrators);
        }
    }
}
