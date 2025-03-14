package com.onarandombox.MultiverseCore.generators;

import com.onarandombox.MultiverseCore.generators.impl.MultiverseVoidGenerator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Map;

//CustomGenerators start
public class InternalChunkGeneratorManager {

    private final Map<String, ChunkGenerator> generators;

    private static InternalChunkGeneratorManager instance;

    public static InternalChunkGeneratorManager get() {
        if (instance == null) {
            throw new IllegalStateException("InternalChunkGeneratorManager has not been initialized yet.");
        }
        return instance;
    }

    public static void init(){
        new InternalChunkGeneratorManager();
    }

    public InternalChunkGeneratorManager() {
        instance = this;
        //register internal generators instances (singleton)
        generators = Map.of(
                "VOID", new MultiverseVoidGenerator()
        );
    }

    public boolean exists(String generator){
        return generators.containsKey(generator);
    }

    public ChunkGenerator getGenerator(String generator){
        return generators.getOrDefault(generator.toUpperCase(), null);
    }

}
//CustomGenerators end
