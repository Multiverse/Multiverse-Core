package com.onarandombox.MultiverseCore.localization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class SimpleMessageProvider implements LazyLocaleMessageProvider {

    public final static String LOCALIZATION_FOLDER_NAME = "localization";

    private final HashMap<Locale, HashMap<MultiverseMessage, String>> messages;
    private final MultiverseCore core;

    private Locale locale = DEFAULT_LOCALE;

    public SimpleMessageProvider(MultiverseCore core) {
        this.core = core;
        messages = new HashMap<Locale, HashMap<MultiverseMessage, String>>();

        try {
            loadLocale(locale);
        } catch (NoSuchLocalizationException e) {
            //let's take the defaults from the enum!
        }
    }

    public void maybeLoadLocale(Locale locale) throws LocalizationLoadingException {
        if (!isLocaleLoaded(locale)) {
            try {
                loadLocale(locale);
            } catch (NoSuchLocalizationException e) {
                core.log(Level.WARNING, "An error occured while trying to get the localization for: "
                                + e.getLocale().getDisplayCountry(DEFAULT_LOCALE));
                throw e;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadLocale(Locale l) throws NoSuchLocalizationException {
        InputStream stream = null;

        try {
            stream = new FileInputStream(new File(core.getDataFolder(), l.getLanguage() + ".yml"));
        } catch (FileNotFoundException e) {
        }

        // only if that file didn't exist, we try to get the localization from the JAR.
        if (stream == null) // this way, users can easily overwrite localizations from the JAR.
            stream = core.getResource(new StringBuilder(LOCALIZATION_FOLDER_NAME).append("/")
                    .append(l.getLanguage()).append(".yml").toString());

        if (stream == null)
            throw new NoSuchLocalizationException(l);

        messages.put(l, new HashMap<MultiverseMessage, String>(MultiverseMessage.values().length));

        FileConfiguration config = YamlConfiguration.loadConfiguration(stream);
        for (MultiverseMessage m : MultiverseMessage.values()) {
            messages.get(l).put(m, config.getString(m.toString(), m.getDefault()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Locale> getLoadedLocales() {
        return messages.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLocaleLoaded(Locale l) {
        return messages.containsKey(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(MultiverseMessage key) {
        if (!isLocaleLoaded(DEFAULT_LOCALE))
            return key.getDefault();
        else
            return messages.get(locale).get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(MultiverseMessage key, Locale locale) {
        try {
            maybeLoadLocale(locale);
            return messages.get(locale).get(key);
        } catch (LocalizationLoadingException e) {
            e.printStackTrace();
            return getMessage(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocale(Locale locale) {
        if (locale == null)
            throw new IllegalArgumentException("Can't set locale to null!");

        try {
            maybeLoadLocale(locale);
        } catch (LocalizationLoadingException e) {
            if (!locale.equals(DEFAULT_LOCALE))
                throw new IllegalArgumentException("Error while trying to load localization for the given Locale!", e);
        }

        this.locale = locale;
    }
}
