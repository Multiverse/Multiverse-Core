package org.mvplugins.multiverse.core.teleportation;

import org.jetbrains.annotations.ApiStatus;

/**
 * Defines how passengers and vehicles on an entity should be handled when the entity is teleported.
 *
 * @since 5.1
 */
@ApiStatus.AvailableSince("5.1")
public interface PassengerMode {
    /**
     * Defines whether the passengers should be removed from the parent entity when the parent entity is teleported.
     * <br/>
     * Teleports between worlds may fail if passengers are not removed.
     *
     * @return Whether the passengers should be removed
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    boolean isDismountPassengers();

    /**
     * Defines whether the passengers should follow the parent entity when the parent entity is teleported.
     * <br/>
     * This only applies of passengers are removed from the parent entity, i.e. {@link #isDismountPassengers()} is true.
     *
     * @return Whether the passengers should follow
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    boolean isPassengersFollow();

    /**
     * Defines whether the entity should dismount from the vehicle when the entity is teleported.
     * <br/>
     * Teleport between worlds may fail if entity is not dismounted.
     *
     * @return Whether the entity should dismount
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    boolean isDismountVehicle();

    /**
     * Defines whether the vehicle and other passengers on it should follow the target entity on the vehicle when
     * the target entity is teleported.
     * <br/>
     * This only applies if the entity is dismounted from the vehicle, i.e. {@link #isDismountVehicle()} is true.
     *
     * @return Whether the vehicle should follow
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    boolean isVehicleFollow();
}
