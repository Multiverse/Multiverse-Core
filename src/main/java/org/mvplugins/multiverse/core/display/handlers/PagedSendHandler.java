package org.mvplugins.multiverse.core.display.handlers;

import java.util.List;

import co.aikar.commands.BukkitCommandIssuer;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * Display content as a list with optional pagination.
 */
public class PagedSendHandler extends BaseSendHandler<PagedSendHandler> {

    /**
     * Makes a new {@link PagedSendHandler} instance to use.
     *
     * @return  New {@link PagedSendHandler} instance.
     */
    public static PagedSendHandler create() {
        return new PagedSendHandler();
    }

    private boolean paginate = true;
    private boolean paginateInConsole = false;
    private boolean padEnd = true;
    private int linesPerPage = 8; // SUPPRESS CHECKSTYLE: MagicNumberCheck
    private int targetPage = 1;

    PagedSendHandler() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendContent(@NotNull BukkitCommandIssuer issuer, @NotNull List<String> content) {
        if (!paginate || (issuer.getIssuer() instanceof ConsoleCommandSender && !paginateInConsole)) {
            sendNormal(issuer, content);
            return;
        }
        sendPaged(issuer, content);
    }

    /**
     * Send content list without pagination.
     *
     * @param issuer    The target which the content will be displayed to.
     * @param content   The content to display.
     */
    private void sendNormal(@NotNull BukkitCommandIssuer issuer, @NotNull List<String> content) {
        if (filter.needToFilter()) {
            issuer.sendMessage(String.format("%s[Filter '%s']", ChatColor.GRAY, filter));
        }
        content.forEach(issuer::sendMessage);
    }

    /**
     * Send content list with pagination.
     *
     * @param issuer    The target which the content will be displayed to.
     * @param content   The content to display.
     */
    private void sendPaged(@NotNull BukkitCommandIssuer issuer, @NotNull List<String> content) {
        int totalPages = (content.size() + linesPerPage - 1) / linesPerPage; // Basically just divide round up
        if (targetPage < 1 || targetPage > totalPages) {
            issuer.sendMessage(String.format("%sInvalid page number. Please enter a page number between 1 and %s", ChatColor.RED, totalPages));
            return;
        }

        if (filter.needToFilter()) {
            issuer.sendMessage(String.format("%s[Page %s of %s] [Filter '%s']", ChatColor.GRAY, targetPage, totalPages, filter));
        } else {
            issuer.sendMessage(String.format("%s[Page %s of %s]", ChatColor.GRAY, targetPage, totalPages));
        }

        int startIndex = (targetPage - 1) * linesPerPage;
        int pageEndIndex = startIndex + linesPerPage;
        int endIndex = Math.min(pageEndIndex, content.size());
        List<String> pageContent = content.subList(startIndex, endIndex);
        if (padEnd) {
            for (int i = 0; i < (pageEndIndex - endIndex); i++) {
                pageContent.add("");
            }
        }
        pageContent.forEach(issuer::sendMessage);
    }

    /**
     * Sets whether display output should be paginated.
     *
     * @param paginate  State of doing pagination.
     * @return Same {@link PagedSendHandler} for method chaining.
     */
    public PagedSendHandler doPagination(boolean paginate) {
        this.paginate = paginate;
        return this;
    }

    /**
     * Sets whether display output should be paginated if is for console output.
     * This option will be useless if {@link PagedSendHandler#paginate} is set to false.
     *
     * @param paginateInConsole State of doing pagination in console.
     * @return Same {@link PagedSendHandler} for method chaining.
     */
    public PagedSendHandler doPaginationInConsole(boolean paginateInConsole) {
        this.paginateInConsole = paginateInConsole;
        return this;
    }

    /**
     * Sets whether empty lines should be added if content lines shown is less that {@link PagedSendHandler#linesPerPage}.
     *
     * @param padEnd    State of doing end padding.
     * @return Same {@link PagedSendHandler} for method chaining.
     */
    public PagedSendHandler doEndPadding(boolean padEnd) {
        this.padEnd = padEnd;
        return this;
    }

    /**
     * Sets the max number of lines per page. This excludes header.
     *
     * @param linesPerPage  The number of lines per page.
     * @return Same {@link PagedSendHandler} for method chaining.
     */
    public PagedSendHandler withLinesPerPage(int linesPerPage) {
        this.linesPerPage = linesPerPage;
        return this;
    }

    /**
     * Sets the page number to display.
     *
     * @param targetPage    The target page number to display.
     * @return Same {@link PagedSendHandler} for method chaining.
     */
    public PagedSendHandler withTargetPage(int targetPage) {
        this.targetPage = targetPage;
        return this;
    }

    public boolean isPaginate() {
        return paginate;
    }

    public boolean isPaginateInConsole() {
        return paginateInConsole;
    }

    public boolean isPadEnd() {
        return padEnd;
    }

    public int getLinesPerPage() {
        return linesPerPage;
    }

    public int getTargetPage() {
        return targetPage;
    }
}
