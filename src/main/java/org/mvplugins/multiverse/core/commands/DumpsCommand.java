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
import org.mvplugins.multiverse.core.command.flag.ParsedCommandFlags;
import org.mvplugins.multiverse.core.commands.DumpsLogPoster.UploadType;
import org.mvplugins.multiverse.core.command.MVCommandIssuer;
import org.mvplugins.multiverse.core.command.flag.CommandValueFlag;

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
    @Syntax("[--upload <pastesdev|mclogs>]")
    @Description("{@@mv-core.dumps.description}")
    void onDumpsCommand(
            MVCommandIssuer issuer,

            @Optional
            @Syntax("[--upload <pastesdev|mclogs>]")
            String[] flagArray) {
        ParsedCommandFlags parsedFlags = flags.parse(flagArray);
        UploadType servicesType = parsedFlags.flagValue(flags.upload, UploadType.PASTESDEV);
        dumpsService.postLogs(issuer, servicesType);
    }

    @Service
    private static final class Flags extends FlagBuilder {

        private static final String NAME = "mvdumps";

        @Inject
        private Flags(@NotNull CommandFlagsManager flagsManager) {
            super(NAME, flagsManager);
        }

        private final CommandValueFlag<UploadType> upload = flag(CommandValueFlag
                .enumBuilder("--upload", UploadType.class)
                .addAlias("-u")
                .build());
    }
}
