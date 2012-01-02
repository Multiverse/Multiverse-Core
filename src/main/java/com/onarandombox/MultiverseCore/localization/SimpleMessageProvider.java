package com.onarandombox.MultiverseCore.localization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class SimpleMessageProvider implements LazyLocaleMessageProvider {

    public final static String LOCALIZATION_FOLDER_NAME = "localization";

    private final HashMap<Locale, HashMap<MultiverseMessage, List<String>>> messages;
    private final JavaPlugin plugin;

    private Locale locale = DEFAULT_LOCALE;

    public SimpleMessageProvider(JavaPlugin plugin) {
        this.plugin = plugin;
        messages = new HashMap<Locale, HashMap<MultiverseMessage, List<String>>>();

        try {
            loadLocale(locale);
        } catch (NoSuchLocalizationException e) {
            // let's take the defaults from the enum!
        }
    }

    public void maybeLoadLocale(Locale locale) throws LocalizationLoadingException {
        if (!isLocaleLoaded(locale)) {
            try {
                loadLocale(locale);
            } catch (NoSuchLocalizationException e) {
                throw e;
            }
        }
        if (!isLocaleLoaded(locale))
            throw new LocalizationLoadingException("Couldn't load the localization: "
                    + locale.toString(), locale);
    }
    
    public List<String> format(List<String> strings, Object... args) {
        for (String string : strings) {
            format(string, args);
        }
        return strings;
    }

    public String format(String string, Object... args) {
        // Replaces & with the Section character
        string = string.replaceAll("&", Character.toString((char) 167));

        return String.format(string, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadLocale(Locale l) throws NoSuchLocalizationException {
        messages.remove(l);

        InputStream resstream = null;
        InputStream filestream = null;

        try {
            filestream = new FileInputStream(new File(plugin.getDataFolder(), l.getLanguage() + ".yml"));
        } catch (FileNotFoundException e) {
        }

        try {
            resstream = plugin.getResource(new StringBuilder(LOCALIZATION_FOLDER_NAME).append("/")
                    .append(l.getLanguage()).append(".yml").toString());
        } catch (Exception e) {
        }

        if ((resstream == null) && (filestream == null))
            throw new NoSuchLocalizationException(l);

        messages.put(l, new HashMap<MultiverseMessage, List<String>>(MultiverseMessage.values().length));

        FileConfiguration resconfig = (resstream == null) ? null : YamlConfiguration.loadConfiguration(resstream);
        FileConfiguration fileconfig = (filestream == null) ? null : YamlConfiguration.loadConfiguration(filestream);
        for (MultiverseMessage m : MultiverseMessage.values()) {
            List<String> values = m.getDefault();

            if (resconfig != null) {
                if (resconfig.isList(m.toString())) {
                    values = resconfig.getStringList(m.toString());
                } else {
                    values.add(resconfig.getString(m.toString(), values.get(0)));
                }
            }
            if (fileconfig != null) {
                if (fileconfig.isList(m.toString())) {
                    values = fileconfig.getStringList(m.toString());
                } else {
                    values.add(fileconfig.getString(m.toString(), values.get(0)));
                }
            }

            messages.get(l).put(m, values);
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
    public String getMessage(MultiverseMessage key, Object... args) {
        if (!isLocaleLoaded(locale)) {
            return format(key.getDefault().get(0), args);
        }
        else
            return format(messages.get(locale).get(key).get(0), args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage(MultiverseMessage key, Locale locale, Object... args) {
        try {
            maybeLoadLocale(locale);
        } catch (LocalizationLoadingException e) {
            e.printStackTrace();
            return getMessage(key, args);
        }
        return format(messages.get(locale).get(key).get(0), args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMessages(MultiverseMessage key, Object... args) {
        if (!isLocaleLoaded(locale)) {
            return format(key.getDefault(), args);
        }
        else
            return format(messages.get(locale).get(key), args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getMessages(MultiverseMessage key, Locale locale, Object... args) {
        try {
            maybeLoadLocale(locale);
        } catch (LocalizationLoadingException e) {
            e.printStackTrace();
            return format(getMessages(key), args);
        }
        return format(messages.get(locale).get(key), args);
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
