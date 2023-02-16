package com.onarandombox.MultiverseCore.locale;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A class loader that loads resources from the plugin's locales folder.
 */
public class FileResClassLoader extends ClassLoader {
    private final transient File localesFolder;

    public FileResClassLoader(final ClassLoader classLoader, final Plugin plugin) {
        super(classLoader);
        this.localesFolder = new File(plugin.getDataFolder(), "locales");
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
