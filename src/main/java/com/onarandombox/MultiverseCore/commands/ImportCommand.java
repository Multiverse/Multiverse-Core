package com.onarandombox.MultiverseCore.commands;

import java.util.Arrays;
import java.util.stream.Collectors;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.commandtools.MVCommandManager;
import com.onarandombox.MultiverseCore.commandtools.MultiverseCommand;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandFlagGroup;
import com.onarandombox.MultiverseCore.commandtools.flags.CommandValueFlag;
import com.onarandombox.MultiverseCore.commandtools.flags.ParsedCommandFlags;
import com.onarandombox.MultiverseCore.utils.UnsafeCallWrapper;
import jakarta.inject.Inject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

@Service
@CommandAlias("mv")
public class ImportCommand extends MultiverseCommand {

    private final MVWorldManager worldManager;

    @Inject
    public ImportCommand(
            @NotNull MVCommandManager commandManager,
            @NotNull MVWorldManager worldManager,
            @NotNull UnsafeCallWrapper unsafeCallWrapper
    ) {
        super(commandManager);
        this.worldManager = worldManager;

        registerFlagGroup(CommandFlagGroup.builder("mvimport")
                .add(CommandValueFlag.builder("--generator", String.class)
                        .addAlias("-g")
                        .completion(() -> Arrays.stream(Bukkit.getServer().getPluginManager().getPlugins())
                                .filter(Plugin::isEnabled)
                                .filter(genplugin -> unsafeCallWrapper.wrap(
                                        () -> genplugin.getDefaultWorldGenerator("world", ""),
                                        genplugin.getName(),
                                        "Get generator"
                                ) != null)
                                .map(genplugin -> genplugin.getDescription().getName())
                                .collect(Collectors.toList()))
                        .build())
                .add(CommandFlag.builder("--adjust-spawn")
                        .addAlias("-a")
                        .build())
                .build());
    }

    @Subcommand("import")
    @CommandPermission("multiverse.core.import")
    @CommandCompletion("@mvworlds:scope=potential  @flags:groupName=mvimport")
    @Syntax("<name> <env> --generator [generator[:id]] --adjust-spawn")
    @Description("Imports a existing world folder.")
    public void onImportCommand(BukkitCommandIssuer issuer,

                                @Conditions("validWorldName:scope=new")
                                @Syntax("<name>")
                                @Description("Name of the world folder.")
                                String worldName,

                                @Syntax("<env>")
                                @Description("The world's environment. See: /mv env")
                                World.Environment environment,

                                @Optional
                                @Syntax("--generator [generator[:id]] --adjust-spawn")
                                @Description("Other world settings. See: https://gg.gg/nn8c2")
                                String[] flags) {

        ParsedCommandFlags parsedFlags = parseFlags(flags);

        issuer.sendMessage(String.format("Starting import of world '%s'...", worldName));

        if (!this.worldManager.addWorld(
                worldName, environment,
                null,
                null,
                null,
                parsedFlags.flagValue("--generator", String.class),
                parsedFlags.hasFlag("--adjust-spawn"))
        ) {
            issuer.sendMessage(String.format("%sFailed! See console for more details.", ChatColor.RED));
            return;
        }
        issuer.sendMessage(String.format("%sComplete!", ChatColor.GREEN));
    }
}
