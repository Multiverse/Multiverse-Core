package com.onarandombox.MultiverseCore.utils;

import de.themoep.idconverter.IdMappings;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A tool for converting values which may be an old type ID to a Material.
 */
public class MaterialConverter {

    /**
     * Converts the value in the given config at the given path from a numeric id or flattened material name to a
     * Material.
     *
     * @param config The config with the value to convert.
     * @param path The path of the value in the config.
     * @return The converted Material type or null if no matching type.
     */
    @Nullable
    public static Material convertConfigType(@NotNull ConfigurationSection config, @NotNull String path) {
        return convertTypeString(config.getString(path));
    }

    /**
     * Converts a string representing a numeric id or flattened material name to a Material.
     *
     * @param value The value to convert.
     * @return The converted Material type or null if no matching type.
     */
    @Nullable
    public static Material convertTypeString(@Nullable String value) {
        String targetValue = value != null ? value : "";

        IdMappings.Mapping idMapping = IdMappings.getById(targetValue);
        if (idMapping != null) {
            return getMappedType(idMapping);
        }
        IdMappings.Mapping flattenMapping = IdMappings.getByFlatteningType(targetValue);
        if (flattenMapping != null) {
            return getMappedType(flattenMapping);
        }
        IdMappings.Mapping legacyMapping = IdMappings.getByLegacyType(targetValue);
        if (legacyMapping != null) {
            return getMappedType(legacyMapping);
        }

        return Material.matchMaterial(targetValue);
    }

    /**
     * Gets flattened or legacy material type based on what the server supports.
     *
     * @param mapping Dynamic material mapping provided by {@link IdMappings}.
     * @return Material of the mapping.
     */
    public static Material getMappedType(IdMappings.Mapping mapping) {
        return CompatibilityLayer.isUsingLegacyMaterials()
                ? Material.matchMaterial(mapping.getLegacyType())
                : Material.matchMaterial(mapping.getFlatteningType());
    }
}
