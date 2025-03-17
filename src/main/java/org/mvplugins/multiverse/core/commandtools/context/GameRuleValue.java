package org.mvplugins.multiverse.core.commandtools.context;

import org.jetbrains.annotations.ApiStatus;

/**
 * Simple wrapper for game rule value, as they may be different types.
 */
@ApiStatus.Internal
public record GameRuleValue(Object value) {
}
