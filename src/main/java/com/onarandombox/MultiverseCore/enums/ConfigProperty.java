/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.enums;

public enum ConfigProperty {
    messagecooldown, teleportcooldown, worldnameprefix, enforcegamemodes, enforceaccess, displaypermerrors, debug;


    public static String getAllValues() {
        String buffer = "";
        for (ConfigProperty c : ConfigProperty.values()) {
            // All values will NOT Contain spaces.
            buffer += c.toString() + " ";
        }
        return buffer;
    }
}
