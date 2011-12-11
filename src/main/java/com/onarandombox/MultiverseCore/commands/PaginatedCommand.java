/******************************************************************************
 * Multiverse 2 Copyright (c) the Multiverse Team 2011.                       *
 * Multiverse 2 is licensed under the BSD License.                            *
 * For more information please check the README.md file included              *
 * with this project.                                                         *
 ******************************************************************************/

package com.onarandombox.MultiverseCore.commands;

import com.pneumaticraft.commandhandler.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Multiverse 2
 *
 * @author fernferret
 */
public abstract class PaginatedCommand<T> extends Command {
    protected int ITEMS_PER_PAGE = 9;


    public PaginatedCommand(JavaPlugin plugin) {
        super(plugin);
    }

    protected void setItemsPerPage(int items) {
        ITEMS_PER_PAGE = items;
    }

    protected abstract List<T> getFilteredItems(List<T> availableItems, String filter);

    protected String stitchThisString(List<String> list) {
        String returnstr = "";
        for (String s : list) {
            returnstr += s + " ";
        }
        return returnstr;
    }

    protected void showPage(int page, CommandSender sender, List<T> cmds) {
        // Ensure the page is at least 1.
        page = (page <= 0) ? 1 : page;
        int start = (page - 1) * ITEMS_PER_PAGE;
        int end = start + ITEMS_PER_PAGE;

        for (int i = start; i < end; i++) {
            // For consistancy, print some extra lines if it's a player:
            if (i < cmds.size()) {
                sender.sendMessage(this.getItemText(cmds.get(i)));
            } else if (sender instanceof Player) {
                sender.sendMessage(" ");
            }
        }
    }

    protected abstract String getItemText(T item);

    protected FilterObject getPageAndFilter(List<String> args) {
        int page = 1;

        String filter = "";

        if (args.size() == 0) {
            filter = "";
            page = 1;
        } else if (args.size() == 1) {
            try {
                page = Integer.parseInt(args.get(0));
            } catch (NumberFormatException ex) {
                filter = args.get(0);
                page = 1;
            }
        } else if (args.size() == 2) {
            filter = args.get(0);
            try {
                page = Integer.parseInt(args.get(1));
            } catch (NumberFormatException ex) {
                page = 1;
            }
        }
        return new FilterObject(page, filter);
    }

    protected class FilterObject {
        private Integer page;
        private String filter;

        public FilterObject(Integer page, String filter) {
            this.page = page;
            this.filter = filter;
        }

        public Integer getPage() {
            return this.page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public String getFilter() {
            return this.filter;
        }
    }
}
