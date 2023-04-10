package com.onarandombox.MultiverseCore.utils.permissions;

import org.jvnet.hk2.annotations.Service;

@Service
public class MVCorePermissions {
    public final ChildPermission WORLD_ACCESS = new ChildPermission("multiverse.access");
    public final ChildPermission WORLD_EXEMPT = new ChildPermission("multiverse.exempt");
    public final ChildPermission GAMEMODE_BYPASS = new ChildPermission("mv.bypass.gamemode");
    public final ChildPermission PLAYERLIMIT_BYPASS = new ChildPermission("mv.bypass.playerlimit");
    public final ChildPermission TELEPORT_SELF = new ChildPermission("multiverse.teleport.self");
    public final ChildPermission TELEPORT_OTHER = new ChildPermission("multiverse.teleport.other");
}
