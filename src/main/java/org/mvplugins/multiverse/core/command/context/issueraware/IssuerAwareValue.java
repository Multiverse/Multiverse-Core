package org.mvplugins.multiverse.core.command.context.issueraware;

import org.jetbrains.annotations.ApiStatus;

/**
 * Base class for issuer aware values
 *
 * @since 5.1
 */
@ApiStatus.AvailableSince("5.1")
public abstract class IssuerAwareValue {
    protected final boolean byIssuer;

    /**
     * Constructor to create an issuer aware value
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    protected IssuerAwareValue(boolean byIssuer) {
        this.byIssuer = byIssuer;
    }

    /**
     * Gets whether the value is by issuer or input
     *
     * @return true if by issuer, false if by input
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public boolean isByIssuer() {
        return byIssuer;
    }
}
