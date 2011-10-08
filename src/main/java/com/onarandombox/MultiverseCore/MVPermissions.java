/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Dummy class to make old MV Plugins not explode.
 * If this loads, the user WILL get a severe telling them to update said plugin!
 * WILL BE DELETED ON 11/1/11
 */
@Deprecated
public class MVPermissions {
    public Permission addPermission(String string, PermissionDefault def) {
        return new Permission("update.all.multiverse.plugins");
    }
}
