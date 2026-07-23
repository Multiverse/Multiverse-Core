package org.mvplugins.multiverse.core.world.helpers;

import com.google.common.base.Strings;
import jakarta.inject.Inject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement.Replace;
import org.mvplugins.multiverse.core.utils.text.ChatTextFormatter;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import java.util.ArrayList;
import java.util.List;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

/**
 * Checks world aliases for conflicts with other world aliases and names.
 *
 * @since 5.8
 */
@ApiStatus.AvailableSince("5.8")
@Service
public class AliasNameConflictChecker {

    private final WorldManager worldManager;

    @Inject
    private AliasNameConflictChecker(@NotNull WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    /**
     * Checks whether the target world's alias conflicts with another world's alias or name.
     * Color formatting is ignored when aliases are compared.
     *
     * @param targetWorld The world whose alias should be checked.
     * @return The detected alias conflicts.
     *
     * @since 5.8
     */
    @ApiStatus.AvailableSince("5.8")
    public @NotNull AliasNameConflictResult checkDuplicateFor(MultiverseWorld targetWorld) {
        AliasNameConflictResult aliasNameConflictResult = new AliasNameConflictResult(targetWorld);
        String targetWorldAlias = ChatTextFormatter.removeColor(targetWorld.getAlias());
        if (Strings.isNullOrEmpty(targetWorldAlias)) {
            return aliasNameConflictResult;
        }

        for (MultiverseWorld otherWorld : worldManager.getWorlds()) {
            if (otherWorld.getKey().equals(targetWorld.getKey())) {
                continue;
            }
            if (targetWorldAlias.equals(ChatTextFormatter.removeColor(otherWorld.getAlias()))) {
                aliasNameConflictResult.getDuplicateAliases().add(otherWorld);
            }
            if (targetWorldAlias.equals(otherWorld.getName())) {
                aliasNameConflictResult.getDuplicateWorldNames().add(otherWorld);
            }
        }
        return aliasNameConflictResult;
    }

    /**
     * Contains the alias conflicts detected for a world.
     *
     * @since 5.8
     */
    @ApiStatus.AvailableSince("5.8")
    public static class AliasNameConflictResult {
        private final MultiverseWorld targetWorld;
        private final List<MultiverseWorld> duplicateAliases;
        private final List<MultiverseWorld> duplicateWorldNames;

        private AliasNameConflictResult(@NotNull MultiverseWorld targetWorld) {
            this.targetWorld = targetWorld;
            duplicateAliases  = new ArrayList<>();
            duplicateWorldNames = new ArrayList<>();
        }

        /**
         * Sends localized messages describing the detected conflicts to the command issuer.
         * No message is sent when there are no conflicts.
         *
         * @param issuer The command issuer to notify.
         *
         * @since 5.8
         */
        @ApiStatus.AvailableSince("5.8")
        public void sendConflictMessage(MVCommandIssuer issuer) {
            if (!hasConflict()) {
                return;
            }
            issuer.sendError(MVCorei18n.ALIASNAMECONFLICT_DETECTED,
                    Replace.WORLD.with(targetWorld.getName()),
                    replace("{alias}").with(targetWorld.getColourlessAlias()));
            duplicateAliases.forEach(conflictingWorld ->
                    issuer.sendError(MVCorei18n.ALIASNAMECONFLICT_DUPLICATEALIAS,
                            Replace.WORLD.with(conflictingWorld.getName()),
                            replace("{alias}").with(conflictingWorld.getColourlessAlias())));
            duplicateWorldNames.forEach(conflictingWorld ->
                    issuer.sendError(MVCorei18n.ALIASNAMECONFLICT_DUPLICATEWORLDNAME,
                            Replace.WORLD.with(conflictingWorld.getName())));
        }

        /**
         * Gets whether any alias or world name conflicts were detected.
         *
         * @return {@code true} if at least one conflict was detected, otherwise {@code false}.
         *
         * @since 5.8
         */
        @ApiStatus.AvailableSince("5.8")
        public boolean hasConflict() {
            return !duplicateAliases.isEmpty() || !duplicateWorldNames.isEmpty();
        }

        /**
         * Gets the world whose alias was checked.
         *
         * @return The checked world.
         *
         * @since 5.8
         */
        @ApiStatus.AvailableSince("5.8")
        public @NotNull MultiverseWorld getTargetWorld() {
            return targetWorld;
        }

        /**
         * Gets the worlds whose aliases match the target world's alias.
         *
         * @return The worlds with conflicting aliases.
         *
         * @since 5.8
         */
        @ApiStatus.AvailableSince("5.8")
        public @NotNull List<MultiverseWorld> getDuplicateAliases() {
            return duplicateAliases;
        }

        /**
         * Gets the worlds whose names match the target world's alias.
         *
         * @return The worlds with names that conflict with the target world's alias.
         *
         * @since 5.8
         */
        @ApiStatus.AvailableSince("5.8")
        public @NotNull List<MultiverseWorld> getDuplicateWorldNames() {
            return duplicateWorldNames;
        }
    }
}
