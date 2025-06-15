package org.mvplugins.multiverse.core.command.context.issueraware;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * Issuer aware value wrapper for {@link Player} array
 *
 * @since 5.1
 */
@ApiStatus.AvailableSince("5.1")
public final class PlayerArrayValue extends IssuerAwareValue {

    private final Player[] players;

    /**
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public PlayerArrayValue(boolean byIssuer, Player[] players) {
        super(byIssuer);
        this.players = players;
    }

    /**
     * The containing player array wrapped
     *
     * @return wrapped player array
     *
     * @since 5.1
     */
    @ApiStatus.AvailableSince("5.1")
    public Player[] value() {
        return players;
    }
}
