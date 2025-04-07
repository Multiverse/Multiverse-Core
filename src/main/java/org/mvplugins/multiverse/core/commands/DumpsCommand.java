package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.command.flag.CommandFlagsManager;
import org.mvplugins.multiverse.core.command.flag.FlagBuilder;
import org.mvplugins.multiverse.core.commands.DumpsLogPoster.LogsType;
import org.mvplugins.multiverse.core.commands.DumpsLogPoster.UploadType;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.MVCommandManager;
import org.mvplugins.multiverse.core.command.flag.CommandFlag;
import org.mvplugins.multiverse.core.command.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;

@Service
final class DumpsCommand extends CoreCommand {

    private final DumpsService dumpsService;
    private final DumpsCommand.Flags flags;

    @Inject
    DumpsCommand(@NotNull DumpsService dumpsService, @NotNull Flags flags) {
        this.dumpsService = dumpsService;
        this.flags = flags;
    }

    @Subcommand("dumps")
    @CommandPermission("multiverse.core.dumps")
    @CommandCompletion("@flags:groupName=" + Flags.NAME)
    @Syntax("[--logs <mclogs | append>] [--upload <pastesdev | pastegg>] [--paranoid]")
    @Description("{@@mv-core.dumps.description}")
    void onDumpsCommand(
            MVCommandIssuer issuer,

            @Optional
            @Syntax("[--logs <mclogs | append>] [--upload <pastesdev | pastegg>] [--paranoid]")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);

        // Grab all our flags
        boolean paranoid = parsedFlags.hasFlag(flags.paranoid);
        LogsType logsType = parsedFlags.flagValue(flags.logs, LogsType.MCLOGS);
        UploadType servicesType = parsedFlags.flagValue(flags.upload, UploadType.PASTESDEV);

        dumpsService.postLogs(issuer, logsType, servicesType, paranoid);
    }

    @Service
    private static final class Flags extends FlagBuilder {

        private static final String NAME = "mvdumps";

        @Inject
        private Flags(@NotNull CommandFlagsManager flagsManager) {
            super(NAME, flagsManager);
        }

        private final CommandValueFlag<LogsType> logs = flag(CommandValueFlag
                .enumBuilder("--logs", LogsType.class)
                .addAlias("-l")
                .build());

        private final CommandValueFlag<UploadType> upload = flag(CommandValueFlag
                .enumBuilder("--upload", UploadType.class)
                .addAlias("-u")
                .build());

        // Does not upload logs or plugin list (except if --logs mclogs is there)
        private final CommandFlag paranoid = flag(CommandFlag.builder("--paranoid")
                .addAlias("-p")
                .build());
    }
}
