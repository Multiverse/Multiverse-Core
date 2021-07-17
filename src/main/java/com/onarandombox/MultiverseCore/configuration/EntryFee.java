package com.onarandombox.MultiverseCore.configuration;

import com.onarandombox.MultiverseCore.utils.MVMaterials;
import com.onarandombox.MultiverseCore.utils.MaterialConverter;
import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;
import me.main__.util.SerializationConfig.Serializor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Entryfee-settings.
 */
@SerializableAs("MVEntryFee")
public class EntryFee extends SerializationConfig {
    @Property
    private double amount;
    @Property(serializor = EntryFeeCurrencySerializor.class)
    @Nullable
    private Material currency;

    private final Material DISABLED_MATERIAL = MVMaterials.AIR;

    public EntryFee() {
        super();
    }

    public EntryFee(Map<String, Object> values) {
        super(values);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDefaults() {
        amount = 0D;
        currency = null;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @return the currency
     */
    @Nullable
    public Material getCurrency() {
        if (currency == null || currency.equals(DISABLED_MATERIAL)) {
            return null;
        }
        return currency;
    }

    /**
     * Sets the amount.
     * @param amount The new value.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Sets the currency.
     * @param currency The new value.
     */
    public void setCurrency(@Nullable Material currency) {
        this.currency = currency;
    }

    public static final class EntryFeeCurrencySerializor implements Serializor<Material, Object> {
        @Override
        public String serialize(Material material) {
            return material.toString();
        }

        @Override
        public Material deserialize(Object o, Class<Material> aClass) {
            return MaterialConverter.convertTypeString(o.toString());
        }
    }
}
