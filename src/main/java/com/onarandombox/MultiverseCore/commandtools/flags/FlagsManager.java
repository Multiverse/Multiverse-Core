package com.onarandombox.MultiverseCore.commandtools.flags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import co.aikar.commands.InvalidCommandArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages all the flag groups and parsing.
 */
public class FlagsManager {
    private final Map<String, FlagGroup> flagGroupMap;

    /**
     * Creates a new FlagsManager.
     */
    public FlagsManager() {
        flagGroupMap = new HashMap<>();
    }

    /**
     * Registers a flag group.
     *
     * @param flagGroup The target flag group to register.
     */
    public void registerFlagGroup(@NotNull FlagGroup flagGroup) {
        flagGroupMap.put(flagGroup.getName(), flagGroup);
    }

    /**
     *  Get a flag group by name.
     *
     * @param groupName The target flag group name.
     * @return The flag group if found, null otherwise.
     */
    public @Nullable FlagGroup getFlagGroup(@Nullable String groupName) {
        return this.flagGroupMap.get(groupName);
    }

    /**
     * Autocompletes suggestions for flags.
     *
     * @param groupName The target flag group name.
     * @param flags The current flags so far.
     * @return The list of suggestions.
     */
    public @NotNull Collection<String> suggest(@Nullable String groupName, @NotNull String[] flags) {
        FlagGroup flagGroup = this.getFlagGroup(groupName);
        if (flagGroup == null) {
            return Collections.emptyList();
        }

        Collection<String> suggestions = new ArrayList<>();
        MVFlag currentFlag = (flags.length <= 1) ? null : flagGroup.getFlagByKey(flags[flags.length - 2]);

        if (currentFlag instanceof MVValueFlag) {
            MVValueFlag valueFlag = (MVValueFlag) currentFlag;
            if (valueFlag.getCompletion() != null) {
                suggestions.addAll((Collection<String>) valueFlag.getCompletion().get());
            }
            if (valueFlag.isOptional()) {
                suggestions.addAll(getRemainingFlagKeys(flagGroup, flags));
            }
        } else {
            suggestions.addAll(getRemainingFlagKeys(flagGroup, flags));
        }

        return suggestions;
    }

    /**
     * Gets the flags keys that has not been used yet.
     *
     * @param flagGroup The target flag group.
     * @param flags The current flags so far.
     * @return The remaining flag keys.
     */
    private @NotNull Set<String> getRemainingFlagKeys(@NotNull FlagGroup flagGroup, @NotNull String[] flags) {
        Set<String> keysRemaining = new HashSet<>(flagGroup.getKeys());
        for (String flag : flags) {
            MVFlag mvFlag = flagGroup.getFlagByKey(flag);
            if (mvFlag != null) {
                keysRemaining.remove(mvFlag.getKey());
            }
        }
        return keysRemaining;
    }

    /**
     * Parses the flags.
     *
     * @param groupName The target flag group name.
     * @param flags The flags to parse.
     * @return The parsed flags.
     *
     * @throws InvalidCommandArgument If the flags are invalid.
     */
    public @NotNull ParsedFlags parse(@Nullable String groupName, @NotNull String[] flags) {
        ParsedFlags parsedFlags = new ParsedFlags();
        FlagGroup flagGroup = this.getFlagGroup(groupName);
        if (flagGroup == null) {
            return parsedFlags;
        }

        boolean isKey = true;
        boolean isValue = false;

        MVFlag currentFlag = null;

        for (String currentArg : flags) {
            if (isKey) {
                MVFlag potentialFlag = flagGroup.getFlagByKey(currentArg);
                if (potentialFlag == null) {
                    if (!isValue) {
                        throw new InvalidCommandArgument(currentArg + " is not a valid key.");
                    }
                } else {
                    currentFlag = potentialFlag;
                    if (currentFlag instanceof MVValueFlag) {
                        MVValueFlag valueFlag = (MVValueFlag) currentFlag;
                        isValue = true;
                        if (valueFlag.isOptional()) {
                            parsedFlags.addFlagResult(valueFlag.getKey(), valueFlag.getDefaultValue());
                            isKey = true;
                        } else {
                            isKey = false;
                        }
                    } else {
                        parsedFlags.addFlagResult(currentFlag.getKey(), null);
                        isKey = true;
                        isValue = false;
                    }
                    continue;
                }
            }

            if (isValue) {
                if (currentFlag == null) {
                    throw new InvalidCommandArgument("Some flag logic error occurred.");
                }
                if (flagGroup.getKeysFlagMap().containsKey(currentArg)) {
                    throw new InvalidCommandArgument(currentFlag.getKey() + " requires a value!");
                }

                Object flagValue;
                MVValueFlag valueFlag = (MVValueFlag)currentFlag;
                if (valueFlag.getContext() != null) {
                    flagValue = valueFlag.getContext().apply(currentArg);
                } else {
                    flagValue = currentArg;
                }
                parsedFlags.addFlagResult(valueFlag.getKey(), flagValue);
                isKey = true;
                isValue = false;
                currentFlag = null;
            }
        }

        if (!isKey && isValue) {
            throw new InvalidCommandArgument(currentFlag.getKey() + " requires a value!!!");
        }

        return parsedFlags;
    }
}
