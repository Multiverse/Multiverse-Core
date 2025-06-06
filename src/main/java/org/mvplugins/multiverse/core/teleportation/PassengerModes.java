package org.mvplugins.multiverse.core.teleportation;

import org.jetbrains.annotations.ApiStatus;

/**
 * Enum shorthands for {@link PassengerMode} to define common passenger modes configurations.
 *
 * @since 5.1
 */
@ApiStatus.AvailableSince("5.1")
public enum PassengerModes implements PassengerMode {
    /**
     * All passengers and vehicles are handled by the server. This usually means that entities with passengers
     * or in vehicles will not be able to teleport to a different world until they manually dismount.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    DEFAULT(false, false, false, false),

    /**
     * All passengers will be removed from the parent entity before the teleport. Passengers will be left behind.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    DISMOUNT_PASSENGERS(true, false, false, false),

    /**
     * Parent entity will be dismounted from the vehicle before the teleport. Vehicle will be left behind.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    DISMOUNT_VEHICLE(true, false, true, false),

    /**
     * All passengers and vehicles will be removed from the parent entity before the teleport. Passengers and vehicles
     * will be left behind.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    DISMOUNT_ALL(true, false, true, false),

    /**
     * All passengers will teleport together with the parent entity.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    RETAIN_PASSENGERS(true, true, false, false),

    /**
     * All vehicles will teleport together with the parent entity.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    RETAIN_VEHICLE(false, false, true, true),

    /**
     * All passengers and vehicles will teleport together with the parent entity.
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    RETAIN_ALL(true, true, true, true),
    ;

    private final boolean dismountPassengers;
    private final boolean passengersFollow;
    private final boolean dismountVehicle;
    private final boolean vehicleFollow;

    PassengerModes(boolean dismountPassengers, boolean mountPassengers, boolean dismountVehicle, boolean mountVehicle) {
        this.dismountPassengers = dismountPassengers;
        this.passengersFollow = mountPassengers;
        this.dismountVehicle = dismountVehicle;
        this.vehicleFollow = mountVehicle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDismountPassengers() {
        return dismountPassengers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPassengersFollow() {
        return passengersFollow;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDismountVehicle() {
        return dismountVehicle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVehicleFollow() {
        return vehicleFollow;
    }
}
