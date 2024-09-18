package org.mvplugins.multiverse.core.world.config;

import de.themoep.idconverter.IdMappings;
import io.vavr.control.Option;
import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;
import org.mvplugins.multiverse.core.configuration.functions.NodeSerializer;
import org.mvplugins.multiverse.core.economy.MVEconomist;

/**
 * Converts the material name to/from a {@link Material} enum, with the special case of "vault-economy"
 * for world configuration.
 */
public class CurrencySerializer implements NodeSerializer<Material> {

    static final String VAULT_ECONOMY_CODE = "@vault-economy";

    /**
     * {@inheritDoc}
     */
    @Override
    public Material deserialize(Object object, Class<Material> type) {
        return Option.of(object)
                .map(String::valueOf)
                .map(materialStr -> {
                    if (materialStr.equalsIgnoreCase(VAULT_ECONOMY_CODE)) {
                        return MVEconomist.VAULT_ECONOMY_MATERIAL;
                    }
                    return stringToMaterial(materialStr);
                })
                .getOrElse(MVEconomist.VAULT_ECONOMY_MATERIAL);
    }

    /**
     * Converts a string representing a numeric id or flattened material name to a Material.
     *
     * @param value The value to convert.
     * @return The converted Material type or null if no matching type.
     */
    @Nullable
    private Material stringToMaterial(@Nullable String value) {
        IdMappings.Mapping mapping = IdMappings.getById(value != null ? value : "");
        if (mapping != null) {
            return Material.matchMaterial(mapping.getFlatteningType());
        } else {
            return Material.matchMaterial(value != null ? value : "");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object serialize(Material object, Class<Material> type) {
        return Option.of(object)
                .map(material -> material == MVEconomist.VAULT_ECONOMY_MATERIAL ? VAULT_ECONOMY_CODE : material.name())
                .getOrElse(VAULT_ECONOMY_CODE);
    }
}
