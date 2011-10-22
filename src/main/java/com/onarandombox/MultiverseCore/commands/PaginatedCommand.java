/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.command.CommandSender;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public abstract class PaginatedCommand extends MultiverseCommand {

    private int linesToShow = 9;

    public PaginatedCommand(MultiverseCore plugin) {
        super(plugin);
    }

    protected void displayPage(CommandSender s, int pageNum, String filter) {

    }
}
