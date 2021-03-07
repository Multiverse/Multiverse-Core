package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.CommandCompletionContext;
import co.aikar.commands.RegisteredCommand;
import com.onarandombox.MultiverseCore.utils.ReflectHelper;

import java.lang.reflect.Field;

public class MVCommandReflect {

    private static final Field commandScope;
    private static final Field commandField;

    static {
        commandField = ReflectHelper.getField(CommandCompletionContext.class, "command");
        commandScope = ReflectHelper.getField(RegisteredCommand.class, "scope");
    }

    public static MultiverseCommand getCommandCompletionContextCommand(CommandCompletionContext context) {
        RegisteredCommand registeredCommand = ReflectHelper.getFieldValue(context, commandField);
        return ReflectHelper.getFieldValue(registeredCommand, commandScope);
    }
}
