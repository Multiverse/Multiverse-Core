package com.onarandombox.MultiverseCore.commands_helper;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commands_acf.CheckCommand;
import com.onarandombox.MultiverseCore.commands_acf.CloneCommand;
import com.onarandombox.MultiverseCore.commands_acf.ConfigCommand;
import com.onarandombox.MultiverseCore.commands_acf.ConfirmCommand;
import com.onarandombox.MultiverseCore.commands_acf.CoordCommand;
import com.onarandombox.MultiverseCore.commands_acf.CreateCommand;
import com.onarandombox.MultiverseCore.commands_acf.DebugCommand;
import com.onarandombox.MultiverseCore.commands_acf.DeleteCommand;
import com.onarandombox.MultiverseCore.commands_acf.EnvironmentCommand;
import com.onarandombox.MultiverseCore.commands_acf.GameRuleCommand;
import com.onarandombox.MultiverseCore.commands_acf.GeneratorCommand;
import com.onarandombox.MultiverseCore.commands_acf.ImportCommand;
import com.onarandombox.MultiverseCore.commands_acf.InfoCommand;
import com.onarandombox.MultiverseCore.commands_acf.ListCommand;
import com.onarandombox.MultiverseCore.commands_acf.LoadCommand;
import com.onarandombox.MultiverseCore.commands_acf.PurgeCommand;
import com.onarandombox.MultiverseCore.commands_acf.RegenCommand;
import com.onarandombox.MultiverseCore.commands_acf.ReloadCommand;
import com.onarandombox.MultiverseCore.commands_acf.RemoveCommand;
import com.onarandombox.MultiverseCore.commands_acf.ScriptCommand;
import com.onarandombox.MultiverseCore.commands_acf.SetSpawnCommand;
import com.onarandombox.MultiverseCore.commands_acf.SilentCommand;
import com.onarandombox.MultiverseCore.commands_acf.SpawnCommand;
import com.onarandombox.MultiverseCore.commands_acf.TeleportCommand;
import com.onarandombox.MultiverseCore.commands_acf.UnloadCommand;
import com.onarandombox.MultiverseCore.commands_acf.UsageCommand;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MVCommandManager extends PaperCommandManager {

    private final MultiverseCore plugin;
    private final MVWorldManager worldManager;
    private final CommandQueueManager commandQueueManager;

    //TODO: Should be in world manager?
    protected static final Set<String> BLACKLIST_WORLD_FOLDER = Collections.unmodifiableSet(new HashSet<String>() {{
        add("plugins");
        add("cache");
        add("logs");
        add("crash-reports");
    }});

    public MVCommandManager(MultiverseCore plugin) {
        super(plugin);
        this.plugin = plugin;
        this.worldManager = plugin.getMVWorldManager();
        this.commandQueueManager = new CommandQueueManager(plugin);

        enableUnstableAPI("help");

        registerCommandConditions();

        registerCommand(new UsageCommand(plugin));
        registerCommand(new CreateCommand(plugin));
        registerCommand(new LoadCommand(plugin));
        registerCommand(new UnloadCommand(plugin));
        registerCommand(new InfoCommand(plugin));
        registerCommand(new DeleteCommand(plugin));
        registerCommand(new ConfirmCommand(plugin));
        registerCommand(new ConfigCommand(plugin));
        registerCommand(new DebugCommand(plugin));
        registerCommand(new CoordCommand(plugin));
        registerCommand(new SpawnCommand(plugin));
        registerCommand(new ReloadCommand(plugin));
        registerCommand(new RemoveCommand(plugin));
        registerCommand(new ListCommand(plugin));
        registerCommand(new ScriptCommand(plugin));
        registerCommand(new GeneratorCommand(plugin));
        registerCommand(new CloneCommand(plugin));
        registerCommand(new ImportCommand(plugin));
        registerCommand(new CheckCommand(plugin));
        registerCommand(new GameRuleCommand(plugin));
        registerCommand(new EnvironmentCommand(plugin));
        registerCommand(new RegenCommand(plugin));
        registerCommand(new TeleportCommand(plugin));
        registerCommand(new SilentCommand(plugin));
        registerCommand(new PurgeCommand(plugin));
        registerCommand(new SetSpawnCommand(plugin));
    }

    @Override
    public synchronized CommandContexts<BukkitCommandExecutionContext> getCommandContexts() {
        if (this.contexts == null) {
            this.contexts = new MVCommandContexts(this, plugin);
        }
        return this.contexts;
    }

    @Override
    public synchronized CommandCompletions<BukkitCommandCompletionContext> getCommandCompletions() {
        if (this.completions == null) {
            this.completions = new MVCommandCompletions(this, plugin);
        }
        return this.completions;
    }

    public CommandQueueManager getQueueManager() {
        return commandQueueManager;
    }

    private void registerCommandConditions() {
        getCommandConditions().addCondition(String.class, "isMVWorld", this::checkIsMVWorld);
        getCommandConditions().addCondition(String.class, "isUnloadedWorld", this::checkIsUnloadedWorld);
        getCommandConditions().addCondition(String.class, "isWorldInConfig", this::checkIsWorldInConfig);
        getCommandConditions().addCondition(String.class, "creatableWorldName", this::checkCreatableWorldName);
        getCommandConditions().addCondition(String.class, "importableWorldName", this::checkImportableWorldName);
        getCommandConditions().addCondition(String.class, "validWorldFolder", this::checkValidWorldFolder);
    }

    private void checkIsMVWorld(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                @NotNull BukkitCommandExecutionContext executionContext,
                                @NotNull String worldName) {

        if (!this.worldManager.isMVWorld(worldName)) {
            throw new ConditionFailedException("World '" + worldName + "' not found.");
        }
    }

    private void checkIsUnloadedWorld(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                      @NotNull BukkitCommandExecutionContext executionContext,
                                      @NotNull String worldName) {

        if (this.worldManager.isMVWorld(worldName)) {
            throw new ConditionFailedException("World '" + worldName + "' is already loaded.");
        }

        if (!this.worldManager.getUnloadedWorlds().contains(worldName)) {
            throw new ConditionFailedException("World '" + worldName + "' not found.");
        }
    }

    private void checkIsWorldInConfig(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                      @NotNull BukkitCommandExecutionContext executionContext,
                                      @NotNull String worldName) {

        //TODO: Should have direct API for it, instead of check both loaded and unloaded.
        if (!this.worldManager.isMVWorld(worldName) && !this.worldManager.getUnloadedWorlds().contains(worldName)) {
            throw new ConditionFailedException("World '" + worldName + "' not found.");
        }
    }

    private void checkCreatableWorldName(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                         @NotNull BukkitCommandExecutionContext executionContext,
                                         @NotNull String worldName) {

        if (this.worldManager.isMVWorld(worldName)) {
            executionContext.getSender().sendMessage(ChatColor.RED + "Multiverse cannot create " + ChatColor.GOLD + ChatColor.UNDERLINE
                    + "another" + ChatColor.RESET + ChatColor.RED + " world named " + worldName);
            throw new ConditionFailedException();
        }

        File worldFolder = new File(this.plugin.getServer().getWorldContainer(), worldName);
        if (worldFolder.exists() && worldFolder.isDirectory()) {
            executionContext.getSender().sendMessage(ChatColor.RED + "A Folder already exists with this name!");
            if (folderHasDat(worldFolder)) {
                executionContext.getSender().sendMessage(ChatColor.RED + "World Folder '" + worldName + "' already look like a world to me!");
                executionContext.getSender().sendMessage(ChatColor.RED + "You can try importing it with /mv import");
            }
            throw new ConditionFailedException();
        }
    }

    private void checkImportableWorldName(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                          @NotNull BukkitCommandExecutionContext executionContext,
                                          @NotNull String worldName) {

        if (this.worldManager.isMVWorld(worldName)) {
            executionContext.getSender().sendMessage(ChatColor.GREEN + "Multiverse" + ChatColor.WHITE
                    + " already knows about '" + ChatColor.AQUA + worldName + ChatColor.WHITE + "'!");
            throw new ConditionFailedException();
        }

        checkValidWorldFolder(context, executionContext, worldName);
    }

    private void checkValidWorldFolder(@NotNull ConditionContext<BukkitCommandIssuer> context,
                                       @NotNull BukkitCommandExecutionContext executionContext,
                                       @NotNull String worldName) {

        File worldFolder = new File(this.plugin.getServer().getWorldContainer(), worldName);
        if (!worldFolder.isDirectory()) {
            //TODO: Possibly show potential worlds. Use flags.
            throw new ConditionFailedException("World folder '"+ worldName +"' does not exist.");
        }
        if (BLACKLIST_WORLD_FOLDER.contains(worldFolder.getName())) {
            throw new ConditionFailedException("World should not be in reserved server folders.");
        }
        if (!folderHasDat(worldFolder)) {
            throw new ConditionFailedException("'" + worldName + "' does not appear to be a world. It is lacking .dat file.");
        }
    }

    private boolean folderHasDat(@NotNull File worldFolder) {
        File[] files = worldFolder.listFiles((file, name) -> name.equalsIgnoreCase(".dat"));
        return files != null && files.length > 0;
    }
}
