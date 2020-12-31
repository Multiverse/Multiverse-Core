package com.onarandombox.MultiverseCore.commandTools.queue;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

/**
 * Used by {@link CommandQueueManager}, so different commands block can be recognised as one.
 */
class DummyCommandBlockSender implements CommandSender {

    @Override
    public void sendMessage(@NotNull String message) {
        throw new NotImplementedException();
    }

    @Override
    public void sendMessage(@NotNull String[] messages) {
        throw new NotImplementedException();
    }

    @Override
    public @NotNull Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public @NotNull String getName() {
        return "DummyCommandBlockSender";
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        throw new NotImplementedException();
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        throw new NotImplementedException();
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        throw new NotImplementedException();
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        throw new NotImplementedException();
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        throw new NotImplementedException();
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        throw new NotImplementedException();
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        throw new NotImplementedException();
    }

    @Override
    public @Nullable PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        throw new NotImplementedException();
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        throw new NotImplementedException();
    }

    @Override
    public void recalculatePermissions() {
        throw new NotImplementedException();
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        throw new NotImplementedException();
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {
        throw new NotImplementedException();
    }
}
