package com.mvplugin.core.minecraft;

public final class PlayerPosition {

    public static final PlayerPosition NULL_LOCATION = new PlayerPosition("", 0, 0, 0, 0, 0);

    private final double x, y, z;
    private final float pitch, yaw;
    private final String world;

    public PlayerPosition(final String world, final double x,
                   final double y, final double z,
                   final float pitch, final float yaw) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public String getWorld() {
        return world;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }
}
