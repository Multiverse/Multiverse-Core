package org.mvplugins.multiverse.core.configuration.handle;

import io.vavr.control.Option;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.configuration.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.configuration.node.NodeGroup;
import org.mvplugins.multiverse.core.configuration.node.ValueNode;

import java.util.logging.Logger;

public class MemoryConfigurationHandle extends ConfigurationSectionHandle<ConfigurationSection> {

    /**
     * Creates a new builder for a {@link ConfigurationSectionHandle}.
     *
     * @param configurationSection  The configuration section.
     * @param nodes                 The nodes.
     * @return The builder.
     */
    public static MemoryConfigurationHandle.Builder<? extends ConfigurationSectionHandle.Builder<ConfigurationSection, ?>> builder(
            @NotNull ConfigurationSection configurationSection, @NotNull NodeGroup nodes) {
        return new MemoryConfigurationHandle.Builder<>(configurationSection, nodes);
    }

    protected MemoryConfigurationHandle(
            @NotNull ConfigurationSection configurationSection,
            @Nullable Logger logger,
            @NotNull NodeGroup nodes,
            @Nullable ConfigMigrator migrator) {
        super(configurationSection, logger, nodes, migrator);
    }

    @Override
    protected void setUpNodes() {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        ConfigurationSection oldConfig = config;
        config = new MemoryConfiguration();

        nodes.forEach(node -> {
            if (!(node instanceof ValueNode valueNode)) {
                return;
            }
            //todo: this is a copied from CommentedConfigurationHandle
            Option.of(oldConfig.get(valueNode.getPath()))
                    .peek(oldValue -> {
                        this.config.set(valueNode.getPath(), oldValue);
                        set(valueNode, get(valueNode));
                    })
                    .onEmpty(() -> reset(valueNode));
        });
    }

    /**
     * Builder for {@link MemoryConfigurationHandle}.
     *
     * @param <B>   The builder type.
     */
    public static class Builder<B extends Builder<B>> extends ConfigurationSectionHandle.Builder<ConfigurationSection, B> {

        protected Builder(@NotNull ConfigurationSection configurationSection, @NotNull NodeGroup nodes) {
            super(configurationSection, nodes);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public @NotNull MemoryConfigurationHandle build() {
            return new MemoryConfigurationHandle(configurationSection, logger, nodes, migrator);
        }
    }
}
