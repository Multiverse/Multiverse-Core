package com.onarandombox.MultiverseCore.utils.permissions;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ChildPermission extends Permission {

    protected final Permission wildcard = new Permission(this.getName() + ".*", this.getDescription(), this.getDefault());

    public ChildPermission(@NotNull String name) {
        super(name);
    }

    public ChildPermission(@NotNull String name, @Nullable String description) {
        super(name, description);
    }

    public ChildPermission(@NotNull String name, @Nullable PermissionDefault defaultValue) {
        super(name, defaultValue);
    }

    public ChildPermission(@NotNull String name, @Nullable String description, @Nullable PermissionDefault defaultValue) {
        super(name, description, defaultValue);
    }

    public ChildPermission(@NotNull String name, @Nullable Map<String, Boolean> children) {
        super(name, children);
    }

    public ChildPermission(@NotNull String name, @Nullable String description, @Nullable Map<String, Boolean> children) {
        super(name, description, children);
    }

    public ChildPermission(@NotNull String name, @Nullable PermissionDefault defaultValue, @Nullable Map<String, Boolean> children) {
        super(name, defaultValue, children);
    }

    public ChildPermission(@NotNull String name, @Nullable String description, @Nullable PermissionDefault defaultValue, @Nullable Map<String, Boolean> children) {
        super(name, description, defaultValue, children);
    }

    public ChildPermission child(@NotNull String name) {
        return new ChildPermission(
                this.getName() + "." + name,
                this.getDescription(),
                this.getDefault()
        );
    }
}
