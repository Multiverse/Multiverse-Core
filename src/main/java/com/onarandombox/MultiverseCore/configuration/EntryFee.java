package com.onarandombox.MultiverseCore.configuration;

import java.util.Map;

import me.main__.util.SerializationConfig.Property;
import me.main__.util.SerializationConfig.SerializationConfig;

import org.bukkit.configuration.serialization.SerializableAs;

/**
 * Entryfee-settings.
 */
@SerializableAs("MVEntryFee")
public class EntryFee extends SerializationConfig {
    @Property
    private double amount;
    @Property
    private int currency;

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
        currency = -1;
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
    public int getCurrency() {
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
    public void setCurrency(int currency) {
        this.currency = currency;
    }
}
