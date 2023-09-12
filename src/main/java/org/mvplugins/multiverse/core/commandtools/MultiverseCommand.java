package org.mvplugins.multiverse.core.commandtools;

import co.aikar.commands.BaseCommand;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;

import org.mvplugins.multiverse.core.commandtools.flags.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlagGroup;
import org.mvplugins.multiverse.core.commandtools.flags.CommandFlagsManager;
import org.mvplugins.multiverse.core.commandtools.flags.ParsedCommandFlags;

@Contract
public abstract class  MultiverseCommand extends BaseCommand {

    protected final MVCommandManager commandManager;
    private String flagGroupName;
    private CommandFlagGroup.Builder flagGroupBuilder;

    protected MultiverseCommand(@NotNull MVCommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @PostConstruct
    private void postConstruct() {
        if (flagGroupBuilder != null) {
            registerFlagGroup(flagGroupBuilder.build());
            flagGroupBuilder = null;
        }
    }

    protected CommandFlagsManager getFlagsManager() {
        return commandManager.getFlagsManager();
    }

    protected <T extends CommandFlag> T flag(T flag) {
        if (flagGroupBuilder == null) {
            flagGroupBuilder = CommandFlagGroup.builder("mv" + getClass().getSimpleName().toLowerCase());
        }
        flagGroupBuilder.add(flag);
        Logging.finest("Registered flag: " + flag);
        return flag;
    }

    protected void registerFlagGroup(@NotNull CommandFlagGroup flagGroup) {
        if (flagGroupName != null) {
            throw new IllegalStateException("Flag group already registered! (name: " + flagGroupName + ")");
        }
        getFlagsManager().registerFlagGroup(flagGroup);
        flagGroupName = flagGroup.getName();
        Logging.fine("Registered flag group: " + flagGroupName);
    }

    protected @NotNull ParsedCommandFlags parseFlags(@NotNull String[] flags) {
        return getFlagsManager().parse(flagGroupName, flags);
    }
}
