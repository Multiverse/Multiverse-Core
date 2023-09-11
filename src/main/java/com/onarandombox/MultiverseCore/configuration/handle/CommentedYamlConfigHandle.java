package com.onarandombox.MultiverseCore.configuration.handle;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import com.dumptruckman.minecraft.util.Logging;
import com.onarandombox.MultiverseCore.configuration.migration.ConfigMigrator;
import com.onarandombox.MultiverseCore.configuration.node.NodeGroup;
import com.onarandombox.MultiverseCore.configuration.node.CommentedNode;
import com.onarandombox.MultiverseCore.configuration.node.ValueNode;
import io.github.townyadvanced.commentedconfiguration.CommentedConfiguration;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Configuration handle for commented YAML files.
 */
public class CommentedYamlConfigHandle extends FileConfigHandle<CommentedConfiguration> {

    /**
     * Creates a new builder for a {@link CommentedYamlConfigHandle}.
     *
     * @param configPath    The path to the config file.
     * @return The builder.
     */
    public static @NotNull Builder builder(@NotNull Path configPath) {
        return new Builder(configPath);
    }

    protected CommentedYamlConfigHandle(@NotNull Path configPath, @Nullable Logger logger, @Nullable NodeGroup nodes, @Nullable ConfigMigrator migrator) {
        super(configPath, logger, nodes, migrator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadConfigObject() throws IOException {
        config = new CommentedConfiguration(configPath, logger);
        if (!config.load()) {
            throw new IOException("Failed to load commented config file " + configPath
                    + ". See console for details.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUpNodes() {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public Try<Void> save() {
        // TODO: There is no way to check if the save was successful.
        return Try.run(() -> config.save());
    }

    public static class Builder extends FileConfigHandle.Builder<CommentedConfiguration, Builder> {

        protected Builder(@NotNull Path configPath) {
            super(configPath);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull CommentedYamlConfigHandle build() {
            return new CommentedYamlConfigHandle(configPath, logger, nodes, migrator);
        }
    }
}
