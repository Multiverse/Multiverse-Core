package com.onarandombox.MultiverseCore.permissions;

import org.bukkit.permissions.Permissible;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a permission node with dynamically created children.
 */
public class PermissionCollection {
    private final PermissionNode root;
    private final Map<String, PermissionNode> elements;
    private final String prefix;
    private final String description;

    public PermissionCollection(PermissionNode parent, String prefix, String description) {
        if (!prefix.endsWith("."))
            throw new IllegalArgumentException("The prefix must end with a dot!");
        this.root = new HierarchyPermission(parent, prefix + "*", description);
        this.elements = new HashMap<String, PermissionNode>();
        this.prefix = prefix;
        this.description = description;
    }

    /**
     * Checks whether a given {@link Permissible} has a specified element permission.
     *
     * @param p The {@link Permissible}.
     * @param element The element.
     * @return Whether the {@link Permissible} has the permission.
     */
    public boolean has(Permissible p, String element) {
        if (!this.elements.containsKey(element))
            this.elements.put(element, new HierarchyPermission(root, prefix + element, description));
        return this.elements.get(element).has(p);
    }
}
