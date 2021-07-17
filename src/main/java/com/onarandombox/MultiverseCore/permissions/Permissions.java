package com.onarandombox.MultiverseCore.permissions;

/**
 * Permission nodes.
 */
public class Permissions {
    protected Permissions() {
        throw new UnsupportedOperationException();
    }

    /**
     * The {@code multiverse.*} permission.
     */
    public static final HierarchyPermission MV_ROOT = new HierarchyPermission(PermissionNode.OP_DEFAULT,
            "multiverse.*", "Provides access to all Multiverse features.");

    /**
     * The {@code multiverse.access.*} family of permissions.
     */
    public static final PermissionCollection ACCESS = new PermissionCollection(MV_ROOT, "multiverse.access.", "World access");
}
