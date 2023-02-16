package com.onarandombox.MultiverseCore.api;

import java.util.List;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Make things easier for Multiverse Plugins.
 */
public abstract class AbstractMVPlugin extends JavaPlugin implements MVPlugin {
    private MVCore core;

    /**
     * {@inheritDoc}
     *
     * Note: You should not override this, use {@link #onMVPluginEnable()} instead!
     * @see #onMVPluginEnable()
     */
    @Override
    public final void onEnable() {
        MVCore mvCore = (MVCore) this.getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (mvCore == null) {
            Logging.severe("Core not found! You must have Multiverse-Core installed to use this plugin!");
            Logging.severe("Grab a copy at: ");
            Logging.severe("https://dev.bukkit.org/projects/multiverse-core");
            Logging.severe("Disabling!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (mvCore.getProtocolVersion() < this.getProtocolVersion()) {
            Logging.severe("Your Multiverse-Core is OUT OF DATE");
            Logging.severe("This version of " + this.getDescription().getName() + " requires Protocol Level: " + this.getProtocolVersion());
            Logging.severe("Your of Core Protocol Level is: " + this.core.getProtocolVersion());
            Logging.severe("Grab an updated copy at: ");
            Logging.severe("https://dev.bukkit.org/projects/multiverse-core");
            Logging.severe("Disabling!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.core = mvCore;
        this.core.incrementPluginCount();
        this.onMVPluginEnable();
        Logging.config("Version %s (API v%s) Enabled - By %s", this.getDescription().getVersion(), getProtocolVersion(), getAuthors());
    }

    /**
     * {@inheritDoc}
     *
     * Note: You should not override this, use {@link #onMVPluginDisable()} instead!
     * @see #onMVPluginDisable()
     */
    @Override
    public void onDisable() {
        this.core.decrementPluginCount();
        this.onMVPluginDisable();
    }

    /**
     * Called when the plugin is enabled.
     * @see #onEnable()
     */
    protected abstract void onMVPluginEnable();

    /**
     * Called when the plugin is disabled.
     * @see #onDisable()
     */
    protected abstract void onMVPluginDisable();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAuthors() {
        List<String> authorsList = this.getDescription().getAuthors();
        if (authorsList.size() == 0) {
            return "";
        }

        StringBuilder authors = new StringBuilder();
        authors.append(authorsList.get(0));

        for (int i = 1; i < authorsList.size(); i++) {
            if (i == authorsList.size() - 1) {
                authors.append(" and ").append(authorsList.get(i));
            } else {
                authors.append(", ").append(authorsList.get(i));
            }
        }

        return authors.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final MVCore getCore() {
        if (this.core == null) {
            throw new IllegalStateException("MVCore is null!");
        }
        return this.core;
    }
}
