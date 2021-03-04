package com.onarandombox.MultiverseCore.commandTools;

import co.aikar.commands.BaseCommand;
import com.onarandombox.MultiverseCore.commandTools.flags.FlagGroup;

public class MultiverseCommand extends BaseCommand {

    private FlagGroup flagGroup;

    public MultiverseCommand() {
        super();
    }

    public FlagGroup getFlagGroup() {
        return flagGroup;
    }

    public void setFlagGroup(FlagGroup flagGroup) {
        this.flagGroup = flagGroup;
    }
}
