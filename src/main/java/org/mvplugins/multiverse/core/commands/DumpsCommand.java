package org.mvplugins.multiverse.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jvnet.hk2.annotations.Service;

import org.mvplugins.multiverse.core.commands.DumpsLogPoster.LogsType;
import org.mvplugins.multiverse.core.commands.DumpsLogPoster.UploadType;
import org.mvplugins.multiverse.core.commandtools.MVCommandIssuer;
import org.mvplugins.multiverse.core.commandtools.MVCommandManager;
import org.mvplugins.multiverse.core.commandtools.flag.CommandFlag;
import org.mvplugins.multiverse.core.commandtools.flag.CommandValueFlag;
import org.mvplugins.multiverse.core.commandtools.flag.ParsedCommandFlags;

@Service
@CommandAlias("mv")
final class DumpsCommand extends CoreCommand {

    private final DumpsService dumpsService;

    private final CommandValueFlag<LogsType> logsFlag = flag(CommandValueFlag
            .enumBuilder("--logs", LogsType.class)
            .addAlias("-l")
            .build());

    private final CommandValueFlag<UploadType> uploadFlag = flag(CommandValueFlag
            .enumBuilder("--upload", UploadType.class)
            .addAlias("-u")
            .build());

    // Does not upload logs or plugin list (except if --logs mclogs is there)
    private final CommandFlag paranoidFlag = flag(CommandFlag.builder("--paranoid")
            .addAlias("-p")
            .build());

    @Inject
    DumpsCommand(@NotNull MVCommandManager commandManager, @NotNull DumpsService dumpsService) {
        super(commandManager);
        this.dumpsService = dumpsService;
    }

    @Subcommand("dumps")
    @CommandPermission("multiverse.core.dumps")
    @CommandCompletion("@flags:groupName=mvdumpscommand")
    @Syntax("[--logs <mclogs | append>] [--upload <pastesdev | pastegg>] [--paranoid]")
    @Description("{@@mv-core.dumps.description}")
    void onDumpsCommand(
            MVCommandIssuer issuer,

            @Optional
            @Syntax("[--logs <mclogs | append>] [--upload <pastesdev | pastegg>] [--paranoid]")
            String[] flags) {
        ParsedCommandFlags parsedFlags = parseFlags(flags);

        // Grab all our flags
        boolean paranoid = parsedFlags.hasFlag(paranoidFlag);
        LogsType logsType = parsedFlags.flagValue(logsFlag, LogsType.MCLOGS);
        UploadType servicesType = parsedFlags.flagValue(uploadFlag, UploadType.PASTESDEV);

        dumpsService.postLogs(issuer, logsType, servicesType, paranoid);
    }
}
