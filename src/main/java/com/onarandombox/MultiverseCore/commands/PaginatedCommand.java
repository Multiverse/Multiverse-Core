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
 * A generic paginated command.
 * @param <T> The type of items on the page.
 */
public abstract class PaginatedCommand<T> extends Command {
    private static final int DEFAULT_ITEMS_PER_PAGE = 9;
    /**
     * The number of items per page.
     */
    protected int itemsPerPage = DEFAULT_ITEMS_PER_PAGE;

    public PaginatedCommand(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Set the number of items per page.
     *
     * @param items The new number of items per page.
     */
    protected void setItemsPerPage(int items) {
        itemsPerPage = items;
    }

    /**
     * Gets filtered items.
     * @param availableItems All available items.
     * @param filter The filter-{@link String}.
     * @return A list of items that match the filter.
     */
    protected abstract List<T> getFilteredItems(List<T> availableItems, String filter);

    /**
     * Constructs a single string from a list of strings.
     *
     * @param list The {@link List} of strings.
     * @return A single {@link String}.
     */
    protected String stitchThisString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s);
            builder.append(' ');
        }
        return builder.toString();
    }

    /**
     * Shows a page.
     *
     * @param page The number of the page to show.
     * @param sender The {@link CommandSender} that wants to see the page.
     * @param cmds The items that should be displayed on the page.
     */
    protected void showPage(int page, CommandSender sender, List<T> cmds) {
        // Ensure the page is at least 1.
        page = (page <= 0) ? 1 : page;
        int start = (page - 1) * itemsPerPage;
        int end = start + itemsPerPage;

        for (int i = start; i < end; i++) {
            // For consistancy, print some extra lines if it's a player:
            if (i < cmds.size()) {
                sender.sendMessage(this.getItemText(cmds.get(i)));
            } else if (sender instanceof Player) {
                sender.sendMessage(" ");
            }
        }
    }

    /**
     * Converts an item into a string.
     *
     * @param item The item.
     * @return A {@link String}.
     */
    protected abstract String getItemText(T item);

    /**
     * Constructs a {@link FilterObject} from a {@link List} of arguments.
     *
     * @param args The {@link List} of arguments.
     * @return The {@link FilterObject}.
     */
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

    /**
     * "Key-Object" containing information about the page and the filter that were requested.
     */
    protected class FilterObject {
        private Integer page;
        private String filter;

        public FilterObject(Integer page, String filter) {
            this.page = page;
            this.filter = filter;
        }

        /**
         * Gets the page.
         * @return The page.
         */
        public Integer getPage() {
            return this.page;
        }

        /**
         * Sets the page.
         *
         * @param page The new page.
         */
        public void setPage(int page) {
            this.page = page;
        }

        /**
         * Gets the filter.
         * @return The filter.
         */
        public String getFilter() {
            return this.filter;
        }
    }
}
