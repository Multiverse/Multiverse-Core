package org.mvplugins.multiverse.core.configuration.node;

import org.jetbrains.annotations.NotNull;

public interface Node {

    /**
     * Gets the YAML path of the node.
     *
     * @return The YAML path of the node.
     */
    @NotNull String getPath();
}
