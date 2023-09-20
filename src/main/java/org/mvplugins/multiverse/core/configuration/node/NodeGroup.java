package org.mvplugins.multiverse.core.configuration.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.github.townyadvanced.commentedconfiguration.setting.CommentedNode;
import io.vavr.control.Option;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A collection of {@link CommentedNode}s, with mappings to nodes by name.
 */
public class NodeGroup implements Collection<Node> {
    private final Collection<Node> nodes;
    private final Map<String, Node> nodesMap;

    /**
     * Creates a new empty node group.
     */
    public NodeGroup() {
        this.nodes = new ArrayList<>();
        this.nodesMap = new HashMap<>();
    }

    /**
     * Creates a new node group with the given nodes.
     *
     * @param nodes The nodes to make up the group.
     */
    public NodeGroup(@NotNull Collection<Node> nodes) {
        this.nodes = nodes;
        this.nodesMap = new HashMap<>(nodes.size());
        nodes.forEach(this::addNodeIndex);
    }

    private void addNodeIndex(@NotNull Node node) {
        if (node instanceof ValueNode) {
            ((ValueNode<?>) node).getName().peek(name -> nodesMap.put(name, node));
        }
    }

    private void removeNodeIndex(@NotNull Node node) {
        if (node instanceof ValueNode) {
            ((ValueNode<?>) node).getName().peek(nodesMap::remove);
        }
    }

    /**
     * Gets the names of all nodes in this group.
     *
     * @return The names of all nodes in this group.
     */
    public @NotNull Collection<String> getNames() {
        return nodesMap.keySet();
    }

    /**
     * Gets the node with the given name.
     *
     * @param name The name of the node to get.
     * @return The node with the given name, or {@link Option.None} if no node with the given name exists.
     */
    public @NotNull Option<Node> findNode(@Nullable String name) {
        return Option.of(nodesMap.get(name));
    }

    /**
     * Gets the node with the given name.
     *
     * @param name The name of node to get.
     * @param type The type of node to get.
     * @param <T>  The type of node.
     * @return The node with the given name, or {@link Option.None} if no node with the given name exists.
     */
    public <T extends Node> @NotNull Option<T> findNode(@Nullable String name, @NotNull Class<T> type) {
        return Option.of(nodesMap.get(name)).map(node -> type.isAssignableFrom(node.getClass()) ? (T) node : null);
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return nodes.contains(o);
    }

    @NotNull
    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    @Override
    public Object @NotNull [] toArray() {
        return nodes.toArray();
    }

    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] ts) {
        return nodes.toArray(ts);
    }

    @Override
    public boolean add(Node node) {
        if (nodes.add(node)) {
            addNodeIndex(node);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (nodes.remove(o) && o instanceof CommentedNode) {
            removeNodeIndex((Node) o);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return nodes.containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Node> collection) {
        return nodes.addAll(collection);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> collection) {
        return nodes.removeAll(collection);
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> collection) {
        return nodes.retainAll(collection);
    }

    @Override
    public void clear() {
        nodes.clear();
    }
}
