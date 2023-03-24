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
public class NodeGroup implements Collection<CommentedNode> {
    private final Collection<CommentedNode> nodes;
    private final Map<String, CommentedNode> nodesMap;

    public NodeGroup() {
        this.nodes = new ArrayList<>();
        this.nodesMap = new HashMap<>();
    }

    public NodeGroup(Collection<CommentedNode> nodes) {
        this.nodes = nodes;
        this.nodesMap = new HashMap<>(nodes.size());
        nodes.forEach(this::addNodeIndex);
    }

    private void addNodeIndex(CommentedNode node) {
        if (node instanceof NamedValueNode) {
            ((NamedValueNode<?>) node).getName().ifPresent(name -> nodesMap.put(name, node));
        }
    }

    private void removeNodeIndex(CommentedNode node) {
        if (node instanceof NamedValueNode) {
            ((NamedValueNode<?>) node).getName().ifPresent(nodesMap::remove);
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
     * @param name The name of the node to get.
     * @return The node with the given name, or {@link Optional#empty()} if no node with the given name exists.
     */
    public Optional<CommentedNode> findNode(String name) {
        return Optional.ofNullable(nodesMap.get(name));
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
    public Iterator<CommentedNode> iterator() {
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
    public boolean add(CommentedNode commentedNode) {
        if (nodes.add(commentedNode)) {
            addNodeIndex(commentedNode);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (nodes.remove(o) && o instanceof CommentedNode) {
            removeNodeIndex((CommentedNode) o);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> collection) {
        return nodes.containsAll(collection);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends CommentedNode> collection) {
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
