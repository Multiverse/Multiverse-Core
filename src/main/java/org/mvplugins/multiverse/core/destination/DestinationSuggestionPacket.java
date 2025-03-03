package org.mvplugins.multiverse.core.destination;

/**
 * Data of a possible destination for tab completion and permission checking
 *
 * @param destinationString     The destination string
 * @param finerPermissionSuffix The finer permission suffix
 */
public record DestinationSuggestionPacket(String destinationString, String finerPermissionSuffix) {
}
