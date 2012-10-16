package com.onarandombox.MultiverseCore.configuration;

import java.util.Locale;

import me.main__.util.SerializationConfig.IllegalPropertyValueException;
import me.main__.util.SerializationConfig.Serializor;

public class LocaleSerializor implements Serializor<Locale, String> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Locale deserialize(String arg0, Class<Locale> arg1) throws IllegalPropertyValueException {
        return new Locale(arg0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String serialize(Locale arg0) {
        return arg0.getLanguage();
    }
}
