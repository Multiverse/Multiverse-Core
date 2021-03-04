/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2020.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commandTools.MultiverseCommand;

public abstract class MultiverseCoreCommand extends MultiverseCommand {

    protected final MultiverseCore plugin;

    protected MultiverseCoreCommand(MultiverseCore plugin) {
        this.plugin = plugin;
    }
}
