package com.onarandombox.MultiverseCore.enums;

import com.onarandombox.MultiverseCore.utils.EnumMapping;
import org.bukkit.World;
import org.bukkit.WorldType;

public class MVEnums {

    public static final EnumMapping<World.Environment> ENVIRONMENT = EnumMapping.builder(World.Environment.class)
            .addAlias("overworld", World.Environment.NORMAL)
            .addAlias("hell", World.Environment.NETHER)
            .addAlias("end", World.Environment.THE_END)
            .addAlias("starwars", World.Environment.THE_END)
            .mapWithoutUnderscore()
            .build();

    public static final EnumMapping<WorldType> WORLD_TYPE = EnumMapping.builder(WorldType.class)
            .mapWithoutUnderscore()
            .build();
}
