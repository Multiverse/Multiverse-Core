package org.mvplugins.multiverse.core.command;

import co.aikar.commands.BaseCommand;
import com.dumptruckman.minecraft.util.Logging;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Contract;

import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.CommandFlagGroup;
import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;

/**
 * Base class for all Multiverse commands.
 */
@Contract
public abstract class MultiverseCommand extends BaseCommand {
}
