package org.mvplugins.multiverse.core.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ExceptionHandler;
import co.aikar.commands.RegisteredCommand;
import com.dumptruckman.minecraft.util.Logging;

import java.util.List;

/**
 * Default handler that will print the stack trace when the command throws an exception.
 */
public class MVDefaultExceptionHandler implements ExceptionHandler {
    @Override
    public boolean execute(BaseCommand command, RegisteredCommand registeredCommand, CommandIssuer sender, List<String> args, Throwable t) {
        Logging.severe("Failed to execute command /%s %s %s",
                registeredCommand.getCommand(), registeredCommand.getPrefSubCommand(), String.join(" ", args));
        t.printStackTrace();
        return false;
    }
}
