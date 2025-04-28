package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.LegacyAliasCommand;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.config.handle.PropertyModifyAction;
import org.mvplugins.multiverse.core.config.handle.StringPropertyHandle;
import org.mvplugins.multiverse.core.locale.MVCorei18n;
import org.mvplugins.multiverse.core.locale.message.MessageReplacement;
import org.mvplugins.multiverse.core.world.MultiverseWorld;
import org.mvplugins.multiverse.core.world.WorldManager;

import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.*;
import static org.mvplugins.multiverse.core.locale.message.MessageReplacement.replace;

@Service
class ModifyCommand extends CoreCommand {

    private final WorldManager worldManager;

    @Inject
    ModifyCommand(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    @Subcommand("modify")
    @CommandPermission("multiverse.core.modify")
    @CommandCompletion("@mvworlds:scope=both @propsmodifyaction @mvworldpropsname @mvworldpropsvalue")
    @Syntax("[world] <set|add|remove|reset> <property> <value>")
    @Description("{@@mv-core.modify.description}")
    void onModifyCommand(// SUPPRESS CHECKSTYLE: ParameterNumber
            MVCommandIssuer issuer,

            @Flags("resolve=issuerAware")
            @Syntax("[world]")
            @Description("{@@mv-core.modify.world.description}")
            @NotNull MultiverseWorld world,

            @Syntax("<set|add|remove|reset>")
            @Description("")
            @NotNull PropertyModifyAction action,

            @Syntax("<property>")
            @Description("{@@mv-core.modify.property.description}")
            @NotNull String propertyName,

            @Optional
            @Single
            @Syntax("[value]")
            @Description("{@@mv-core.modify.value.description}")
            @Nullable String propertyValue) {
        if (action.isRequireValue() && propertyValue == null) {
            issuer.sendMessage(MVCorei18n.MODIFY_SPECIFYVALUE,
                    replace("{action}").with(action.name().toLowerCase()),
                    replace("{property}").with(propertyName));
            return;
        } else if (!action.isRequireValue() && propertyValue != null) {
            issuer.sendMessage(MVCorei18n.MODIFY_CANNOTHAVEVALUE,
                    replace("{action}").with(action.name().toLowerCase()),
                    replace("{property}").with(propertyName));
            return;
        }

        StringPropertyHandle worldPropertyHandle = world.getStringPropertyHandle();
        worldPropertyHandle.modifyPropertyString(propertyName, propertyValue, action).onSuccess(ignore -> {
            issuer.sendMessage(MVCorei18n.MODIFY_SUCCESS,
                    replace("{action}").with(action.name().toLowerCase()),
                    replace("{property}").with(propertyName),
                    Replace.VALUE.with(worldPropertyHandle.getProperty(propertyName).getOrNull()),
                    Replace.WORLD.with(world.getName()));
            worldManager.saveWorldsConfig();
        }).onFailure(exception -> {
            if (propertyValue == null) {
                issuer.sendMessage(MVCorei18n.MODIFY_FAILURE_NOVALUE,
                        replace("{action}").with(action.name().toLowerCase()),
                        replace("{property}").with(propertyName),
                        Replace.WORLD.with(world.getName()),
                        Replace.ERROR.with(exception.getMessage()));
            } else {
                issuer.sendMessage(MVCorei18n.MODIFY_FAILURE,
                        replace("{action}").with(action.name().toLowerCase()),
                        replace("{property}").with(propertyName),
                        Replace.VALUE.with(propertyValue),
                        Replace.WORLD.with(world.getName()),
                        Replace.ERROR.with(exception.getMessage()));
            }
        });
    }

    @Service
    private static final class LegacyAlias extends ModifyCommand implements LegacyAliasCommand {
        @Inject
        LegacyAlias(WorldManager worldManager) {
            super(worldManager);
        }

        @Override
        @CommandAlias("mvmodify|mvm")
        void onModifyCommand(MVCommandIssuer issuer, @NotNull MultiverseWorld world, @NotNull PropertyModifyAction action, @NotNull String propertyName, @Nullable String propertyValue) {
            super.onModifyCommand(issuer, world, action, propertyName, propertyValue);
        }
    }
}
