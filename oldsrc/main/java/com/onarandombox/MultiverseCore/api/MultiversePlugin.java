package com.onarandombox.MultiverseCore.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.utils.DebugLog;
import com.pneumaticraft.commandhandler.CommandHandler;

/**
 * Make things easier for MV-Plugins!
 */
public abstract class MultiversePlugin extends JavaPlugin implements MVPlugin {
    private MultiverseCore core;
    /**
     * Prefix for standard log entrys.
     */
    protected String logTag;
    private DebugLog debugLog;

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
        debugLog = new DebugLog(this.getDescription().getName(), getDataFolder() + File.separator + "debug.log");
        debugLog.setTag(String.format("[%s-Debug]", this.getDescription().getName()));

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

    /**
     * You can register commands here.
     * @param handler The CommandHandler.
     */
    protected abstract void registerCommands(CommandHandler handler);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!this.isEnabled()) {
            sender.sendMessage("This plugin is Disabled!");
            return true;
        }

        ArrayList<String> allArgs = new ArrayList<String>(args.length + 1);
        allArgs.add(command.getName());
        allArgs.addAll(Arrays.asList(args));
        return this.getCore().getCommandHandler().locateAndRunCommand(sender, allArgs);
    }

    @Override
    public void log(Level level, String msg) {
        int debugLevel = this.getCore().getMVConfig().getGlobalDebug();
        if ((level == Level.FINE && debugLevel >= 1) || (level == Level.FINER && debugLevel >= 2)
                || (level == Level.FINEST && debugLevel >= 3)) {
            debugLog.log(level, msg);
        } else if (level != Level.FINE && level != Level.FINER && level != Level.FINEST) {
            String message = new StringBuilder(getLogTag()).append(msg).toString();
            this.getServer().getLogger().log(level, message);
            debugLog.log(level, message);
        }
    }

    private String getLogTag() {
        if (logTag == null)
            logTag = String.format("[%s]", this.getDescription().getName());
        return logTag;
    }

    /**
     * Sets the debug log-tag.
     * @param tag The new tag.
     */
    protected final void setDebugLogTag(String tag) {
        this.debugLog.setTag(tag);
    }

    @Override
    public final String dumpVersionInfo(String buffer) {
        throw new UnsupportedOperationException("This is gone.");
    }

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
