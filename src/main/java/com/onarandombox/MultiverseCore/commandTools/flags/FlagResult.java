package com.onarandombox.MultiverseCore.commandTools.flags;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static com.onarandombox.MultiverseCore.commandTools.flags.CommandFlag.ValueRequirement;

public class FlagResult {

    public static FlagResult parse(@Nullable String[] args,
                                   @NotNull FlagGroup flagGroup) throws FlagParseFailedException {

        FlagResult flagResult = new FlagResult();

        // No args to parse.
        if (args == null || args.length <= 0) {
            return flagResult;
        }

        // First arg must be a flag.
        CommandFlag<?> currentFlag = flagGroup.getByKey(args[0]);
        boolean completed = false;

        // Parse the arguments.
        for (int i = 1, argsLength = args.length; i <= argsLength; i++) {
            // Don't allow null flag obviously.
            if (currentFlag == null) {
                throw new FlagParseFailedException("%s is not a valid flag.", args[i-1]);
            }
            // THis ensures that flag is not null during final parse.
            if (i >= argsLength) {
                break;
            }

            CommandFlag<?> nextFlag = flagGroup.getByKey(args[i]);

            switch (currentFlag.getValueRequirement()) {
                case NONE:
                    // Arg must be a flag key.
                    flagResult.add(currentFlag, currentFlag.getValue(), false);
                    currentFlag = nextFlag;
                    break;
                case OPTIONAL:
                    // Arg can be a flag key or value.
                    if (nextFlag != null) {
                        // It's a key.
                        flagResult.add(currentFlag, currentFlag.getValue(), false);
                        currentFlag = nextFlag;
                        break;
                    }
                    // It's a value.
                    flagResult.add(currentFlag, currentFlag.getValue(args[i]), true);
                    if (i == argsLength - 1) {
                        completed = true;
                        break;
                    }
                    currentFlag = flagGroup.getByKey(args[++i]);
                    break;
                case REQUIRED:
                    // Arg must be a flag value.
                    if (nextFlag != null) {
                        // It's a key.
                        throw new FlagParseFailedException("%s flag '%s' requires a value input.",
                                currentFlag.getName(), currentFlag.getIdentifier());
                    }
                    // It's a value.
                    flagResult.add(currentFlag, currentFlag.getValue(args[i]), true);
                    if (i == argsLength - 1) {
                        completed = true;
                        break;
                    }
                    currentFlag = flagGroup.getByKey(args[++i]);
                    break;
            }
        }

        // Parse final flag.
        if (!completed) {
            if (currentFlag.getValueRequirement() == ValueRequirement.REQUIRED) {
                throw new FlagParseFailedException("%s flag '%s' requires a value input.",
                        currentFlag.getName(), currentFlag.getIdentifier());
            }
            flagResult.add(currentFlag, currentFlag.getValue(), false);
        }

        return flagResult;
    }

    private final Map<CommandFlag<?>, SingleFlagResult<?>> resultMap;

    public FlagResult() {
        resultMap = new HashMap<>();
    }

    private void add(CommandFlag<?> flag, Object value, boolean fromInput) {
        resultMap.put(flag, new SingleFlagResult<>(value, fromInput));
    }

    public <T> T getValue(CommandFlag<T> flag) {
        SingleFlagResult<?> result = resultMap.get(flag);
        if (result == null) {
            return flag.getDefaultValue();
        }
        return (T) result.value;
    }

    public boolean isByUserInput(CommandFlag<?> flag) {
        SingleFlagResult<?> result = resultMap.get(flag);
        if (result == null) {
            return false;
        }
        return result.fromInput;
    }

    public boolean isDefaulted(CommandFlag<?> flag) {
        return resultMap.get(flag) != null;
    }

    @Override
    public String toString() {
        return "FlagResult{" +
                "resultMap=" + resultMap +
                '}';
    }

    private class SingleFlagResult<T> {
        private final T value;
        private final boolean fromInput;

        private SingleFlagResult(T value, boolean fromInput) {
            this.fromInput = fromInput;
            this.value = value;
        }

        @Override
        public String toString() {
            return "SingleFlagResult{" +
                    "value=" + value +
                    ", fromInput=" + fromInput +
                    '}';
        }
    }
}
