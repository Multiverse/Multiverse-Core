package com.onarandombox.MultiverseCore.configuration.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import io.github.townyadvanced.commentedconfiguration.setting.CommentedNode;
import org.jetbrains.annotations.NotNull;

/**
 * A collection of {@link CommentedNode}s, with mappings to nodes by name.
 */
public class NodeGroup implements Collection<Node> {
    private final Collection<Node> nodes;
    private final Map<String, Node> nodesMap;

    public NodeGroup() {
        this.nodes = new ArrayList<>();
        this.nodesMap = new HashMap<>();
    }

    public NodeGroup(Collection<Node> nodes) {
        this.nodes = nodes;
        this.nodesMap = new HashMap<>(nodes.size());
        nodes.forEach(this::addNodeIndex);
    }

    private void addNodeIndex(Node node) {
        if (node instanceof ValueNode) {
            ((ValueNode<?>) node).getName().ifPresent(name -> nodesMap.put(name, node));
        }
    }

    private void removeNodeIndex(Node node) {
        if (node instanceof ValueNode) {
            ((ValueNode<?>) node).getName().ifPresent(nodesMap::remove);
        }
    }

    /**
     * Gets the names of all nodes in this group.
     *
     * @return The names of all nodes in this group.
     */
    public Collection<String> getNames() {
        return nodesMap.keySet();
    }

    /**
     * Gets the node with the given name.
     *
     * @param name  The name of the node to get.
     * @return The node with the given name, or {@link Optional#empty()} if no node with the given name exists.
     */
    public Optional<Node> findNode(String name) {
        return Optional.ofNullable(nodesMap.get(name));
    }

    /**
     * Gets the node with the given name.
     *
     * @param name The name of the node to get.
     * @param type The type of the node to get.
     * @return The node with the given name, or {@link Optional#empty()} if no node with the given name exists.
     */
    public <T extends Node> Optional<T> findNode(String name, Class<T> type) {
        return Optional.ofNullable(nodesMap.get(name))
                .map(node -> type.isAssignableFrom(node.getClass()) ? (T) node : null);
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
