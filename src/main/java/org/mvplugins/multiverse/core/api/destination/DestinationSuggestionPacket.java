package org.mvplugins.multiverse.core.api.destination;

/**
 * Data of a possible destination for tab completion and permission checking
 *
 * @param destinationString     The destination string
 * @param finerPermissionSuffix The finer permission suffix
 * @since 5.0
 */
public record DestinationSuggestionPacket(String destinationString, String finerPermissionSuffix) {
}
