package org.mvplugins.multiverse.core.world.config;

import io.vavr.control.Option;
import org.bukkit.Material;
import org.mvplugins.multiverse.core.configuration.functions.NodeSerializer;
import org.mvplugins.multiverse.core.economy.MVEconomist;
import org.mvplugins.multiverse.core.utils.MaterialConverter;

/**
 * Converts the material name to/from a {@link Material} enum, with the special case of "vault-economy"
 * for world configuration.
 */
final class CurrencySerializer implements NodeSerializer<Material> {

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
                    return MaterialConverter.stringToMaterial(materialStr);
                })
                .getOrElse(MVEconomist.VAULT_ECONOMY_MATERIAL);
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
