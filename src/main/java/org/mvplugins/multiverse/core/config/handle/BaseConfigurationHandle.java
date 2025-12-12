package org.mvplugins.multiverse.core.config.handle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mvplugins.multiverse.core.config.migration.ConfigMigrator;
import org.mvplugins.multiverse.core.config.node.ListValueNode;
import org.mvplugins.multiverse.core.config.node.NodeGroup;
import org.mvplugins.multiverse.core.config.node.ValueNode;

/**
 * Generic configuration handle for all ConfigurationSection types.
 *
 * @param <C>   The configuration type.
 */
@SuppressWarnings("rawtypes,unchecked")
public abstract class BaseConfigurationHandle<C extends ConfigurationSection> {

    protected final @Nullable Logger logger;
    protected final @NotNull NodeGroup nodes;
    protected final @Nullable ConfigMigrator migrator;
    protected final @NotNull Map<ValueNode, Object> nodeValueMap;

    protected C config;

    protected BaseConfigurationHandle(
            @Nullable Logger logger,
            @NotNull NodeGroup nodes,
            @Nullable ConfigMigrator migrator) {
        this.logger = logger;
        this.nodes = nodes;
        this.migrator = migrator;
        this.nodeValueMap = new HashMap<>(nodes.size());
    }

    /**
     * Loads the configuration.
     *
     * @return Whether the configuration was loaded or its given error.
     */
    public Try<Void> load() {
        return Try.run(() -> {
            migrateConfig();
            setUpNodes();
        }).onFailure(e -> {
            Logging.severe("Failed to load configuration: %s", e.getMessage());
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
        nodeValueMap.clear();
        if (nodes.isEmpty()) {
            return;
        }

        nodes.forEach(node -> {
            if (node instanceof ValueNode valueNode) {
                var value = deserializeNodeFromConfig(valueNode);
                nodeValueMap.put(valueNode, value);
            }
        });

        nodeValueMap.forEach((valueNode, value) -> {
            valueNode.onLoad(value);
            valueNode.onLoadAndChange(Bukkit.getConsoleSender(), null, value);
        });
    }

    protected <T> T deserializeNodeFromConfig(ValueNode<T> node) {
        if (node.getSerializer() == null) {
            return Option.of(config.getObject(node.getPath(), node.getType())).getOrElse(node::getDefaultValue);
        }
        return Try.of(() -> {
                    var value = config.get(node.getPath());
                    if (value == null) {
                        return node.getDefaultValue();
                    }
                    return node.getSerializer().deserialize(value, node.getType());
                }).flatMap(value -> node.validate(value).map(ignore -> value))
                .onFailure(e -> Logging.warning("Failed to deserialize node %s: %s", node.getPath(), e.getMessage()))
                .getOrElse(node::getDefaultValue);
    }

    /**
     * Saves the configuration.
     */
    public Try<Void> save() {
        return Try.run(() -> nodes.forEach(node -> {
            if (!(node instanceof ValueNode valueNode)) {
                return;
            }
            serializeNodeToConfig(valueNode);
        }));
    }

    protected void serializeNodeToConfig(ValueNode node) {
        var value = nodeValueMap.get(node);
        if (value == null) {
            value = node.getDefaultValue();
        }
        if (node.getSerializer() != null) {
            var serialized = node.getSerializer().serialize(value, node.getType());
            config.set(node.getPath(), serialized);
        } else {
            config.set(node.getPath(), value);
        }
    }

    /**
     * Gets whether the configuration is loaded. i.e. {@link #load()} is called.
     *
     * @return Whether the configuration is loaded.
     */
    public boolean isLoaded() {
        return !nodeValueMap.isEmpty();
    }

    /**
     * Gets the value of a node, if the node has a default value, it will be returned if the node is not found.
     *
     * @param node The node to get the value of.
     * @return The value of the node.
     */
    public <T> T get(@NotNull ValueNode<T> node) {
        return (T) nodeValueMap.get(node);
    }

    /**
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param node  The node to set the value of.
     * @param value The value to set.
     * @param <T>   The type of the node value.
     * @return Empty try if the value was set, try containing an error otherwise.
     */
    public <T> Try<Void> set(@NotNull ValueNode<T> node, T value) {
        return set(Bukkit.getConsoleSender(), node, value);
    }

    /**
     * Sets the value of a node, if the validator is not null, it will be tested first.
     *
     * @param sender The sender who triggered the change.
     * @param node   The node to set the value of.
     * @param value  The value to set.
     * @param <T>    The type of the node value.
     * @return Empty try if the value was set, try containing an error otherwise.
     *
     * @since 5.4
     */
    @ApiStatus.AvailableSince("5.4")
    public <T> Try<Void> set(@NotNull CommandSender sender, @NotNull ValueNode<T> node, T value) {
        return node.validate(value).map(ignore -> {
            T oldValue = get(node);
            nodeValueMap.put(node, value);
            node.onLoadAndChange(sender, oldValue, value);
            node.onChange(sender, oldValue, value);
            return null;
        });
    }

    /**
     * Adds an item to a list node.
     *
     * @param node      The list node to add the item to.
     * @param itemValue The value of the item to add.
     * @param <I>       The type of the list item.
     * @return Empty try if the item was added, try containing an error otherwise.
     */
    public <I> Try<Void> add(@NotNull ListValueNode<I> node, I itemValue) {
        return node.validateItem(itemValue).map(ignore -> {
            List<I> list = get(node);
            list.add(itemValue);
            node.onSetItemValue(null, itemValue);
            return null;
        });
    }

    /**
     * Removes an item from a list node.
     *
     * @param node      The list node to remove the item from.
     * @param itemValue The value of the item to remove.
     * @param <I>       The type of the list item.
     * @return Empty try if the item was removed, try containing an error otherwise.
     */
    public <I> Try<Void> remove(@NotNull ListValueNode<I> node, I itemValue) {
        return node.validateItem(itemValue).map(ignore -> {
            List<I> list = get(node);
            if (!list.remove(itemValue)) {
                throw new IllegalArgumentException("Cannot remove item as it is already not in the list!");
            }
            node.onSetItemValue(itemValue, null);
            return null;
        });
    }

    /**
     * Sets the default value of a node.
     *
     * @param node  The node to set the default value of.
     * @param <T>   The type of the node value.
     * @return Empty try if the value was set, try containing an error otherwise.
     */
    public <T> Try<Void> reset(@NotNull ValueNode<T> node) {
        return set(node, node.getDefaultValue());
    }

    /**
     * Gets the configuration.
     *
     * @return The configuration.
     */
    public C getConfig() {
        return config;
    }

    /**
     * Gets the configuration. Mainly used for {@link StringPropertyHandle}.
     *
     * @return The configuration.
     */
    @NotNull NodeGroup getNodes() {
        return nodes;
    }

    /**
     * Abstract builder for {@link BaseConfigurationHandle}.
     *
     * @param <C>   The configuration type.
     * @param <B>   The builder type.
     */
    public abstract static class Builder<C extends ConfigurationSection, B extends BaseConfigurationHandle.Builder<C, B>> {

        protected final @NotNull NodeGroup nodes;
        protected @Nullable Logger logger;
        protected @Nullable ConfigMigrator migrator;

        protected Builder(@NotNull NodeGroup nodes) {
            this.nodes = nodes;
        }

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
        public abstract @NotNull BaseConfigurationHandle<C> build();

        @SuppressWarnings("unchecked")
        protected B self() {
            return (B) this;
        }
    }
}
