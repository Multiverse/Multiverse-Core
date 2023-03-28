package com.onarandombox.MultiverseCore.configuration.handle;

import java.nio.file.Path;
import java.util.logging.Logger;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import com.onarandombox.MultiverseCore.configuration.node.CommentedNode;
import com.onarandombox.MultiverseCore.configuration.node.ValueNode;
import io.github.townyadvanced.commentedconfiguration.CommentedConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommentedYamlConfigHandle extends FileConfigHandle<CommentedConfiguration> {

    public static @NotNull Builder builder(@NotNull Path configPath, @NotNull NodeGroup nodes) {
        return new Builder(configPath, nodes);
    }

    protected CommentedYamlConfigHandle(@NotNull Path configPath, @Nullable Logger logger, @NotNull NodeGroup nodes, @Nullable ConfigMigrator migrator) {
        super(configPath, logger, nodes, migrator);
    }

    @Override
    protected boolean loadConfigObject() {
        config = new CommentedConfiguration(configPath, logger);
        return config.load();
    }

    @Override
    protected void setUpNodes() {
        CommentedConfiguration oldConfig = config;
        this.config = new CommentedConfiguration(configPath, logger);

        nodes.forEach(node -> {
            if (node instanceof CommentedNode typedNode) {
                if (typedNode.getComments().length > 0) {
                    config.addComment(typedNode.getPath(), typedNode.getComments());
                }
            }
            if (node instanceof ValueNode valueNode) {
                set(valueNode, oldConfig.getObject(valueNode.getPath(), valueNode.getType(), valueNode.getDefaultValue())).onFailure(e -> {
                    Logging.warning("Failed to set node " + valueNode.getPath() + " to " + valueNode.getDefaultValue());
                    setDefault(valueNode);
                });
            }
        });
    }

    @Override
    public boolean save() {
        config.save();
        return true;
    }

    public static class Builder extends FileConfigHandle.Builder<CommentedConfiguration, Builder> {

        protected Builder(@NotNull Path configPath, @NotNull NodeGroup nodes) {
            super(configPath, nodes);
        }

        @Override
        public @NotNull CommentedYamlConfigHandle build() {
            return new CommentedYamlConfigHandle(configPath, logger, nodes, migrator);
        }
    }
}
