package org.mvplugins.multiverse.core.commandtools.flags;

import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import org.mvplugins.multiverse.core.commandtools.flag.CommandValueFlag;

/**
 * A command flag for page number.
 * <br/>
 * Parses the value of the --page (or -p) flag as an integer.
 */
public final class PageCommandFlag extends CommandValueFlag<Integer> {

    private static final Function<String, Integer> VALUE_PARSER = value -> {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page number: " + value);
        }
    };

    /**
     * Creates a new instance of this flag.
     *
     * @return The new instance.
     */
    @NotNull
    public static PageCommandFlag create() {
        return new PageCommandFlag();
    }

    private PageCommandFlag() {
        super("--page", List.of("-p"), Integer.class, false, null, VALUE_PARSER, null);
    }
}
