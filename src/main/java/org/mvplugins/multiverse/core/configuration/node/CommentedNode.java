package org.mvplugins.multiverse.core.configuration.node;

import org.jetbrains.annotations.NotNull;

public interface CommentedNode extends Node {

    /**
     * Gets the comment of the node.
     *
     * @return The comment of the node.
     */
    @NotNull String[] getComments();
}
