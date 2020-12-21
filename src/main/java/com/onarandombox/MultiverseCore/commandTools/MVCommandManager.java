package com.onarandombox.MultiverseCore.commandTools;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commands.AnchorCommand;
import com.onarandombox.MultiverseCore.commands.BedCommand;
import com.onarandombox.MultiverseCore.commands.CheckCommand;
import com.onarandombox.MultiverseCore.commands.CloneCommand;
import com.onarandombox.MultiverseCore.commands.ConfigCommand;
import com.onarandombox.MultiverseCore.commands.ConfirmCommand;
import com.onarandombox.MultiverseCore.commands.CoordCommand;
import com.onarandombox.MultiverseCore.commands.CreateCommand;
import com.onarandombox.MultiverseCore.commands.DebugCommand;
import com.onarandombox.MultiverseCore.commands.DeleteCommand;
import com.onarandombox.MultiverseCore.commands.EnvironmentCommand;
import com.onarandombox.MultiverseCore.commands.GameRuleCommand;
import com.onarandombox.MultiverseCore.commands.GeneratorCommand;
import com.onarandombox.MultiverseCore.commands.ImportCommand;
import com.onarandombox.MultiverseCore.commands.InfoCommand;
import com.onarandombox.MultiverseCore.commands.ListCommand;
import com.onarandombox.MultiverseCore.commands.LoadCommand;
import com.onarandombox.MultiverseCore.commands.ModifyCommand;
import com.onarandombox.MultiverseCore.commands.PurgeCommand;
import com.onarandombox.MultiverseCore.commands.RegenCommand;
import com.onarandombox.MultiverseCore.commands.ReloadCommand;
import com.onarandombox.MultiverseCore.commands.RemoveCommand;
import com.onarandombox.MultiverseCore.commands.ScriptCommand;
import com.onarandombox.MultiverseCore.commands.SetSpawnCommand;
import com.onarandombox.MultiverseCore.commands.SilentCommand;
import com.onarandombox.MultiverseCore.commands.SpawnCommand;
import com.onarandombox.MultiverseCore.commands.TeleportCommand;
import com.onarandombox.MultiverseCore.commands.UnloadCommand;
import com.onarandombox.MultiverseCore.commands.UsageCommand;
import com.onarandombox.MultiverseCore.commands.VersionCommand;
import com.onarandombox.MultiverseCore.commands.WhoCommand;

import java.util.Arrays;
import java.util.Set;

public class MVCommandManager extends PaperCommandManager {

    private final MultiverseCore plugin;
    private final CommandQueueManager commandQueueManager;

    public MVCommandManager(MultiverseCore plugin) {
        super(plugin);
        this.plugin = plugin;
        this.commandQueueManager = new CommandQueueManager(plugin);
        new MVCommandConditions(plugin, getCommandConditions());

        enableUnstableAPI("help");

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
        registerCommand(new ModifyCommand(plugin));
        registerCommand(new VersionCommand(plugin));
        registerCommand(new BedCommand(plugin));
        registerCommand(new AnchorCommand(plugin));
        registerCommand(new WhoCommand(plugin));
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

    /**
     * Change default implementation to OR instead of AND
     */
    @Override
    public boolean hasPermission(CommandIssuer issuer, Set<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return true;
        }

        return permissions.stream()
                .anyMatch(permission -> hasPermission(issuer, permission));
    }

    /**
     * Change default implementation to OR instead of AND
     */
    @Override
    public boolean hasPermission(CommandIssuer issuer, String permission) {
        if (permission == null || permission.isEmpty()) {
            return true;
        }

        return Arrays.stream(permission.split(","))
                .anyMatch(perm -> !perm.isEmpty() && issuer.hasPermission(perm));
    }

    public CommandQueueManager getQueueManager() {
        return commandQueueManager;
    }
}
