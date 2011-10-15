/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.test;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public class TestCommandSender implements CommandSender {

    private Server server;
    private boolean isOp;

    public TestCommandSender(Server server) {
        this.server = server;
    }

    /**
     * Sends this sender a message
     *
     * @param message Message to be displayed
     */
    @Override
    public void sendMessage(String message) {
        System.out.println(ChatColor.stripColor(message));
    }

    /**
     * Returns the server instance that this command is running on
     *
     * @return Server instance
     */
    @Override
    public Server getServer() {
        return this.server;
    }

    /**
     * Gets the name of this command sender
     *
     * @return Name of the sender
     */
    @Override
    public String getName() {
        return "CONSOLE";
    }

    /**
     * Checks if this object contains an override for the specified permission, by fully qualified name
     *
     * @param name Name of the permission
     *
     * @return true if the permission is set, otherwise false
     */
    @Override
    public boolean isPermissionSet(String name) {
        return true;
    }

    /**
     * Checks if this object contains an override for the specified {@link org.bukkit.permissions.Permission}
     *
     * @param perm Permission to check
     *
     * @return true if the permission is set, otherwise false
     */
    @Override
    public boolean isPermissionSet(Permission perm) {
        return true;
    }

    /**
     * Gets the value of the specified permission, if set.
     * <p/>
     * If a permission override is not set on this object, the default value of the permission will be returned.
     *
     * @param name Name of the permission
     *
     * @return Value of the permission
     */
    @Override
    public boolean hasPermission(String name) {
        return true;
    }

    /**
     * Gets the value of the specified permission, if set.
     * <p/>
     * If a permission override is not set on this object, the default value of the permission will be returned
     *
     * @param perm Permission to get
     *
     * @return Value of the permission
     */
    @Override
    public boolean hasPermission(Permission perm) {
        return true;
    }

    /**
     * Adds a new {@link org.bukkit.permissions.PermissionAttachment} with a single permission by name and value
     *
     * @param plugin Plugin responsible for this attachment, may not be null or disabled
     * @param name   Name of the permission to attach
     * @param value  Value of the permission
     *
     * @return The PermissionAttachment that was just created
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return null;
    }

    /**
     * Adds a new empty {@link org.bukkit.permissions.PermissionAttachment} to this object
     *
     * @param plugin Plugin responsible for this attachment, may not be null or disabled
     *
     * @return The PermissionAttachment that was just created
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    /**
     * Temporarily adds a new {@link org.bukkit.permissions.PermissionAttachment} with a single permission by name and
     * value
     *
     * @param plugin Plugin responsible for this attachment, may not be null or disabled
     * @param name   Name of the permission to attach
     * @param value  Value of the permission
     * @param ticks  Amount of ticks to automatically remove this attachment after
     *
     * @return The PermissionAttachment that was just created
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return null;
    }

    /**
     * Temporarily adds a new empty {@link org.bukkit.permissions.PermissionAttachment} to this object
     *
     * @param plugin Plugin responsible for this attachment, may not be null or disabled
     * @param ticks  Amount of ticks to automatically remove this attachment after
     *
     * @return The PermissionAttachment that was just created
     */
    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return null;
    }

    /**
     * Removes the given {@link org.bukkit.permissions.PermissionAttachment} from this object
     *
     * @param attachment Attachment to remove
     *
     * @throws IllegalArgumentException Thrown when the specified attachment isn't part of this object
     */
    @Override
    public void removeAttachment(PermissionAttachment attachment) {
    }

    /**
     * Recalculates the permissions for this object, if the attachments have changed values.
     * <p/>
     * This should very rarely need to be called from a plugin.
     */
    @Override
    public void recalculatePermissions() {
    }

    /**
     * Gets a set containing all of the permissions currently in effect by this object
     *
     * @return Set of currently effective permissions
     */
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    /**
     * Checks if this object is a server operator
     *
     * @return true if this is an operator, otherwise false
     */
    @Override
    public boolean isOp() {
        return this.isOp;
    }

    /**
     * Sets the operator status of this object
     *
     * @param value New operator value
     */
    @Override
    public void setOp(boolean value) {
        this.isOp = value;
    }
}
