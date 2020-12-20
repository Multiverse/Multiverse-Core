package com.onarandombox.MultiverseCore.commands_helper;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.PaperCommandManager;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.commands_acf.AnchorCommand;
import com.onarandombox.MultiverseCore.commands_acf.BedCommand;
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
import com.onarandombox.MultiverseCore.commands_acf.ModifyCommand;
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
import com.onarandombox.MultiverseCore.commands_acf.VersionCommand;

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
