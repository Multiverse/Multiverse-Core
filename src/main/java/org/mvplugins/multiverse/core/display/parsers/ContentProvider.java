package org.mvplugins.multiverse.core.display.parsers;

import java.util.Collection;

import co.aikar.commands.BukkitCommandIssuer;
import org.jetbrains.annotations.NotNull;

/**
 * Parse objects into string or list of strings.
 */
@FunctionalInterface
public interface ContentProvider {
    /**
     * Parse the object to string(s) and add it to the content.
     *
     * @param issuer    The target which the content will be displayed to.
     * @return          The parsed content list.
     */
    Collection<String> parse(@NotNull BukkitCommandIssuer issuer);
}
