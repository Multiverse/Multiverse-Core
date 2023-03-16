package com.onarandombox.MultiverseCore.utils.settings.migration;

import java.util.ArrayList;
import java.util.List;

import com.onarandombox.MultiverseCore.utils.settings.MVSettings;
import io.github.townyadvanced.commentedconfiguration.setting.TypedValueNode;

public class ConfigMigrator {

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
                versionMigrator.migrate(settings);
            }
        }
    }

    public static class Builder {
        private final TypedValueNode<Double> versionNode;
        private final List<VersionMigrator> versionMigrators;

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
