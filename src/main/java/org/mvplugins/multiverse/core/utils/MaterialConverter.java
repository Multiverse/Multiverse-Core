package org.mvplugins.multiverse.core.utils;

import de.themoep.idconverter.IdMappings;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

/**
 * A tool for converting values which may be an old type ID to a Material.
 */
public final class MaterialConverter {

    /**
     * Converts a string representing a numeric id or flattened material name to a Material.
     *
     * @param value The value to convert.
     * @return The converted Material type or null if no matching type.
     */
    @Nullable
    public static Material stringToMaterial(@Nullable String value) {
        IdMappings.Mapping mapping = IdMappings.getById(value != null ? value : "");
        if (mapping != null) {
            return Material.matchMaterial(mapping.getFlatteningType());
        } else {
            return Material.matchMaterial(value != null ? value : "");
        }
    }
}
