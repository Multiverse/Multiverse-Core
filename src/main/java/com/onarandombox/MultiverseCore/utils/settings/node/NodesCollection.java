package com.onarandombox.MultiverseCore.utils.settings.node;

import java.util.Collection;
import java.util.Iterator;

import io.github.townyadvanced.commentedconfiguration.setting.CommentedNode;
import org.jetbrains.annotations.NotNull;

public class NodesCollection implements Collection<CommentedNode> {
    private final Collection<CommentedNode> nodes;

    public NodesCollection(Collection<CommentedNode> nodes) {
        this.nodes = nodes;
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
        return nodes.add(commentedNode);
    }

    @Override
    public boolean remove(Object o) {
        return nodes.remove(o);
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
