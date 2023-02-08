package com.onarandombox.MultiverseCore.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.onarandombox.MultiverseCore.MultiverseCore;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Make things easier for MV-Plugins!
 */
public abstract class MultiversePlugin extends JavaPlugin implements MVPlugin {
    private MultiverseCore core;

    /**
     * {@inheritDoc}
     *
     * Note: You can't override this, use {@link #onPluginEnable()} instead!
     * @see #onPluginEnable()
     */
    @Override
    public final void onEnable() {
        MultiverseCore theCore = (MultiverseCore) this.getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (theCore == null) {
            this.getLogger().severe("Core not found! The plugin dev needs to add a dependency!");
            this.getLogger().severe("Disabling!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if (theCore.getProtocolVersion() < this.getProtocolVersion()) {
            this.getLogger().severe("You need a newer version of Multiverse-Core!");
            this.getLogger().severe("Disabling!");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.setCore(theCore);

        this.getServer().getLogger().info(String.format("%s - Version %s enabled - By %s",
                this.getDescription().getName(), this.getDescription().getVersion(), getAuthors()));
        getDataFolder().mkdirs();
        File debugLogFile = new File(getDataFolder(), "debug.log");
        try {
            debugLogFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.onPluginEnable();
    }

    /**
     * Parse the Authors Array into a readable String with ',' and 'and'.
     *
     * @return The readable authors-{@link String}
     */
    protected String getAuthors() {
        String authors = "";
        List<String> auths = this.getDescription().getAuthors();
        if (auths.size() == 0) {
            return "";
        }

        if (auths.size() == 1) {
            return auths.get(0);
        }

        for (int i = 0; i < auths.size(); i++) {
            if (i == this.getDescription().getAuthors().size() - 1) {
                authors += " and " + this.getDescription().getAuthors().get(i);
            } else {
                authors += ", " + this.getDescription().getAuthors().get(i);
            }
        }
        return authors.substring(2);
    }

    /**
     * Called when the plugin is enabled.
     * @see #onEnable()
     */
    protected abstract void onPluginEnable();

    @Override
    public final MultiverseCore getCore() {
        if (this.core == null)
            throw new IllegalStateException("Core is null!");
        return this.core;
    }

    @Override
    public final void setCore(MultiverseCore core) {
        this.core = core;
    }
}
