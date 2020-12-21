/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import co.aikar.commands.BaseCommand;
import com.onarandombox.MultiverseCore.MultiverseCore;

public abstract class MultiverseCommand extends BaseCommand {

    protected final MultiverseCore plugin;
    //TODO: Should we put world manager here?

    protected MultiverseCommand(MultiverseCore plugin) {
        this.plugin = plugin;
    }
}
