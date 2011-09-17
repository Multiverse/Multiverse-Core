/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.event;

import java.util.List;

import org.bukkit.event.Event;

public class MVConfigMigrateEvent extends Event {
    private static final long serialVersionUID = 3647950355746345397L;
    private List<String> configsLoaded;

    public MVConfigMigrateEvent(List<String> configsLoaded) {
        super("MVConfigMigrate");
        this.configsLoaded = configsLoaded;
    }

    public void addConfig(String config) {
        this.configsLoaded.add(config);
    }

    public List<String> getAllConfigsLoaded() {
        return this.configsLoaded;
    }
}
