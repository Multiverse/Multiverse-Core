package org.mvplugins.multiverse.core.command;

import co.aikar.commands.CommandCompletionContext;
import co.aikar.commands.CommandCompletionFilter;
import co.aikar.commands.apachecommonslang.ApacheCommonsLangUtil;
import org.jetbrains.annotations.ApiStatus;

import java.util.Locale;

/**
 * Utility filters for command completion matching.
 *
 * @since 5.7
 */
@ApiStatus.AvailableSince("5.7")
public final class MVCommandCompletionFilters {

    /**
     * Matches namespaced keys (for example, {@code minecraft:stone}) using namespace-aware checks.
     * <p>
     * This filter accepts completions that:
     * <ul>
     *     <li>start with the current input,</li>
     *     <li>have a namespace starting with the input, or</li>
     *     <li>have a key value containing the input.</li>
     * </ul>
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static final CommandCompletionFilter NAMESPACED_KEY = (context, completion) -> {
        String[] split = completion.split(":", 2);
        if (split.length < 2) {
            return ApacheCommonsLangUtil.startsWithIgnoreCase(completion, context.getInput());
        }
        String lowerCase = context.getInput().toLowerCase(Locale.ROOT);
        return ApacheCommonsLangUtil.startsWithIgnoreCase(completion, context.getInput())
                || split[0].toLowerCase(Locale.ROOT).startsWith(lowerCase)
                || split[1].toLowerCase(Locale.ROOT).contains(lowerCase);
    };

    /**
     * Gets the namespaced-key completion filter with a generic type-safe signature.
     * Use this method instead of {@link MVCommandCompletionFilters#NAMESPACED_KEY}
     * to avoid raw type unchecked warnings.
     *
     * @param <C> completion context type
     * @return the namespaced key filter
     *
     * @since 5.7
     */
    @ApiStatus.AvailableSince("5.7")
    public static <C extends CommandCompletionContext> CommandCompletionFilter<C> namespacedKey() {
        return NAMESPACED_KEY;
    }

    private MVCommandCompletionFilters() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
