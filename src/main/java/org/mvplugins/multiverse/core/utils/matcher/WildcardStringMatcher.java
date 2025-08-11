package org.mvplugins.multiverse.core.utils.matcher;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * WildcardStringMatcher is a StringMatcher that matches strings against a wildcard pattern.
 * It supports '*' as a wildcard character, which can match any sequence of characters.
 * <br />
 * For example, the wildcard "foo*bar" will match any string that starts with "foo" and ends with "bar",
 *
 * @since 5.2
 */
@ApiStatus.AvailableSince("5.2")
public class WildcardStringMatcher implements StringMatcher {

    private final String wildcard;
    private final Pattern pattern;

    /**
     * Creates a new WildcardStringMatcher with a wildcard string.
     * The wildcard string can contain '*' characters, which will be replaced with a regex equivalent.
     *
     * @param wildcard the wildcard string to match against.
     */
    @ApiStatus.AvailableSince("5.2")
    public WildcardStringMatcher(@NotNull String wildcard) {
        this.wildcard = wildcard;
        this.pattern = Try.of(() -> Pattern.compile(("\\Q" + wildcard + "\\E").replace("*", "\\E.*\\Q")))
                .onFailure(ex -> Logging.warning("Failed to compile wildcard '%s': %s",
                        wildcard, ex.getMessage()))
                .getOrNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@Nullable String value) {
        if (pattern == null || value == null) {
            return false;
        }
        return pattern.matcher(value).matches();
    }
}
