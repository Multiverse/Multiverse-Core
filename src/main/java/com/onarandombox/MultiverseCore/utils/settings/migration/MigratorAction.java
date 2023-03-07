package com.onarandombox.MultiverseCore.utils.settings.migration;

import com.onarandombox.MultiverseCore.utils.settings.MVSettings;

public interface MigratorAction {
    void migrate(MVSettings settings);
}
