package com.onarandombox.MultiverseCore.locale;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.bukkit.plugin.Plugin;

/**
 * A class loader that loads resources from the plugin's locales folder.
 */
public class FileResClassLoader extends ClassLoader {
    private static final String DEFAULT_LOCALE_FOLDER_PATH = "locales";
    private final transient File localesFolder;

    public FileResClassLoader(final Plugin plugin) {
        this(plugin, DEFAULT_LOCALE_FOLDER_PATH);
    }

    public FileResClassLoader(final Plugin plugin, final String localesFolderPath) {
        super(plugin.getClass().getClassLoader());
        this.localesFolder = new File(plugin.getDataFolder(), localesFolderPath);
    }

    @Override
    public URL getResource(final String string) {
        final File file = new File(localesFolder, string);
        if (file.exists()) {
            try {
                return file.toURI().toURL();
            } catch (final MalformedURLException ignored) {
            }
        }
        return null;
    }

    @Override
    public InputStream getResourceAsStream(final String string) {
        final File file = new File(localesFolder, string);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (final FileNotFoundException ignored) {
            }
        }
        return null;
    }
}
