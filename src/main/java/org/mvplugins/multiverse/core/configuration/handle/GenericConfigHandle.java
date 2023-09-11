package org.mvplugins.multiverse.core.configuration.handle;

import java.util.logging.Logger;

import io.vavr.control.Try;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.configuration.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.configuration.node.ConfigNodeNotFoundException;
import org.mvplugins.multiverse.core.configuration.node.NodeGroup;
import org.mvplugins.multiverse.core.configuration.node.ValueNode;

/**
 * Generic configuration handle for all ConfigurationSection types.
 */
public abstract class GenericConfigHandle<C extends ConfigurationSection> {
    protected final @Nullable Logger logger;
    protected final @Nullable NodeGroup nodes;
    protected final @Nullable ConfigMigrator migrator;

    protected C config;

    protected GenericConfigHandle(@Nullable Logger logger, @Nullable NodeGroup nodes, @Nullable ConfigMigrator migrator) {
        this.logger = logger;
        this.nodes = nodes;
        this.migrator = migrator;
    }

    /**
     * Loads the configuration.
     *
     * @return Whether the configuration was loaded or its given error.
     */
    public Try<Void> load() {
        return Try.run(() -> {
            if (!config.getKeys(false).isEmpty()) {
                migrateConfig();
            }
            setUpNodes();
        });
    }

    /**
     * Migrates the configuration.
     */
    protected void migrateConfig() {
        if (migrator != null) {
            migrator.migrate(config);
        }
    }

    /**
     * Sets up the nodes.
     */
    protected void setUpNodes() {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        nodes.forEach(node -> {
            if (node instanceof ValueNode valueNode) {
                set(valueNode, get(valueNode));
            }
        });
    }

    /**
     * Gets the value of a node, if the node has a default value, it will be returned if the node is not found.
     * @param name  The name of the node.
     * @return The value of the node.
     */
    public Try<Object> get(@Nullable String name) {
        return nodes.findNode(name, ValueNode.class)
                .toTry(() -> new ConfigNodeNotFoundException(name))
                .map(node -> get((ValueNode<Object>) node));
    }

    /**
     * Gets the value of a node, if the node has a default value, it will be returned if the node is not found.
     *
     * @param node The node to get the value of.
     * @return The value of the node.
     */
    public <T> T get(@NotNull ValueNode<T> node) {
        if (node.getSerializer() == null) {
            return config.getObject(node.getPath(), node.getType(), node.getDefaultValue());
        }
        return node.getSerializer().deserialize(config.get(node.getPath(), node.getDefaultValue()), node.getType());
    }

    /**
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param name  The name of the node.
     * @param value The value to set.
     * @return True if the value was set, false otherwise.
     */
    public Try<Void> set(@Nullable String name, Object value) {
        return nodes.findNode(name, ValueNode.class)
                .toTry(() -> new ConfigNodeNotFoundException(name))
                .flatMap(node -> set(node, value));
    }

    /**
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param node  The node to set the value of.
     * @param value The value to set.
     * @return True if the value was set, false otherwise.
     * @param <T>   The type of the node value.
     */
    public <T> Try<Void> set(@NotNull ValueNode<T> node, T value) {
        return node.validate(value).map(ignore -> {
            T oldValue = get(node);
            if (node.getSerializer() != null) {
                var serialized = node.getSerializer().serialize(value, node.getType());
                config.set(node.getPath(), serialized);
            } else {
                config.set(node.getPath(), value);
            }
            node.onSetValue(oldValue, get(node));
            return null;
        });
    }

    /**
     * Sets the default value of a node.
     *
     * @param node  The node to set the default value of.
     */
    public void setDefault(@NotNull ValueNode node) {
        config.set(node.getPath(), node.getDefaultValue());
    }

    /**
     * Abstract builder for {@link GenericConfigHandle}.
     *
     * @param <C>   The configuration type.
     * @param <B>   The builder type.
     */
    public static abstract class Builder<C extends ConfigurationSection, B extends GenericConfigHandle.Builder<C, B>> {

        protected @Nullable Logger logger;
        protected @Nullable NodeGroup nodes;
        protected @Nullable ConfigMigrator migrator;

        protected Builder() {}

        /**
         * Sets the logger.
         *
         * @param logger The logger.
         * @return The builder.
         */
        public B logger(@Nullable Logger logger) {
            this.logger = logger;
            return self();
        }

        /**
         * Sets the logger.
         *
         * @param plugin    The plugin to get the logger from.
         * @return The builder.
         */
        public B logger(Plugin plugin) {
            this.logger = plugin.getLogger();
            return self();
        }

        /**
         * Sets the nodes.
         *
         * @param nodes The nodes.
         * @return The builder.
         */
        public B nodes(@Nullable NodeGroup nodes) {
            this.nodes = nodes;
            return self();
        }

        /**
         * Sets the migrator.
         *
         * @param migrator The migrator.
         * @return The builder.
         */
        public B migrator(@Nullable ConfigMigrator migrator) {
            this.migrator = migrator;
            return self();
        }

        /**
         * Builds the configuration handle.
         *
         * @return The configuration handle.
         */
        public abstract @NotNull GenericConfigHandle<C> build();

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }
    }
}
