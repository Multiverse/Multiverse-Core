/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.enums;

/**
 * An enum containing all config-properties that can be set.
 */
public enum ConfigProperty {
    /**
     * How long to leave in between sending a message to the player. (NOT YET IMPLEMENTED)
     */
    messagecooldown,
    /**
     * How fast are people allowed to use /MVTP (NOT YET IMPLEMENTED).
     */
    teleportcooldown,
    /**
     * Prefix chat-messages with world-names.
     */
    worldnameprefix,
    /**
     * If value is set to false, Multiverse will NOT enforce world access permissions.
     */
    enforceaccess,
    /**
     * Whether users should get detailed information about the permissions they would need.
     */
    displaypermerrors,
    /**
     * Debug-information.
     */
    debug,
    /**
     * The world new users will spawn in.
     */
    firstspawnworld,
    /**
     * Whether Multiverse should intercept teleports.
     */
    teleportintercept,
    /**
     * Whether Multiverse should override the first spawn.
     */
    firstspawnoverride;

    /**
     * Constructs a string containing all values in this enum.
     *
     * @return That {@link String}.
     */
    public static String getAllValues() {
        String buffer = "";
        for (ConfigProperty c : ConfigProperty.values()) {
            // All values will NOT Contain spaces.
            buffer += c.toString() + " ";
        }
        return buffer;
    }
}
