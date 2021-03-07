package com.onarandombox.MultiverseCore.commandtools;

import co.aikar.commands.BaseCommand;
import com.onarandombox.MultiverseCore.commandtools.flag.FlagGroup;

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
