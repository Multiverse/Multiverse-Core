package com.onarandombox.MultiverseCore.permissions;

import org.bukkit.permissions.Permissible;

/**
 * A node in a permissions hierarchy.
 *
 * @see HierarchyPermission
 */
public interface PermissionNode {
    static final class OperatorDefault implements PermissionNode {
        @Override
        public boolean has(final Permissible permissible) {
            return permissible.isOp();
        }
    }

    /**
     * This {@link HierarchyPermission} depends on the operator state. Ops have it, non-ops don't.
     */
    PermissionNode OP_DEFAULT = new OperatorDefault();

    /**
     * Checks whether a given {@link Permissible} has this node.
     *
     * @param permissible The {@link Permissible} that might have the permission.
     * @return Whether it has the permission.
     */
    boolean has(Permissible permissible);
}
