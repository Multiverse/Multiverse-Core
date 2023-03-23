package com.onarandombox.MultiverseCore.utils.settings.node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.dumptruckman.minecraft.util.Logging;
import io.github.townyadvanced.commentedconfiguration.setting.CommentedNode;
import org.jetbrains.annotations.NotNull;

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
        if (node instanceof NamedValueNode namedValueNode && namedValueNode.getName() != null) {
            nodesMap.put(namedValueNode.getName(), node);
        }
    }

    private void removeNodeIndex(CommentedNode node) {
        if (node instanceof NamedValueNode namedValueNode) {
            nodesMap.remove(namedValueNode.getName());
        }
    }

    public Collection<String> getNames() {
        return nodesMap.keySet();
    }

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

    @NotNull
    @Override
    public Object[] toArray() {
        return nodes.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] ts) {
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
