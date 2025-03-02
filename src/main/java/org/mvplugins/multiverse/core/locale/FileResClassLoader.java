package org.mvplugins.multiverse.core.locale;

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
final class FileResClassLoader extends ClassLoader {

    private final transient File targetFolder;

    /**
     * Creates a new FileResClassLoader.
     *
     * @param plugin    The plugin to load resources from.
     * @param subFolder The subfolder to load resources from.
     */
    FileResClassLoader(final Plugin plugin, final String subFolder) {
        super(plugin.getClass().getClassLoader());
        this.targetFolder = new File(plugin.getDataFolder(), subFolder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getResource(final String string) {
        final File file = new File(targetFolder, string);
        if (file.exists()) {
            try {
                return file.toURI().toURL();
            } catch (final MalformedURLException ignored) {
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResourceAsStream(final String string) {
        final File file = new File(targetFolder, string);
        if (file.exists()) {
            try {
                return new FileInputStream(file);
            } catch (final FileNotFoundException ignored) {
            }
        }
        return null;
    }
}
