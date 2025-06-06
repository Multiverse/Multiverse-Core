package org.mvplugins.multiverse.core.command.context.issueraware;

import org.jetbrains.annotations.ApiStatus;
import org.mvplugins.multiverse.core.world.MultiverseWorld;

/**
 * Issuer aware value wrapper for {@link MultiverseWorld}
 *
 * @since 5.1
 */
@ApiStatus.AvailableSince("5.1")
public class MultiverseWorldValue extends IssuerAwareValue {

    private final MultiverseWorld world;

    /**
     * Constructor for issuer aware value
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public MultiverseWorldValue(boolean byIssuer, MultiverseWorld world) {
        super(byIssuer);
        this.world = world;
    }

    /**
     * The containing world wrapped
     *
     * @return wrapped world
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public MultiverseWorld value() {
        return world;
    }
}
