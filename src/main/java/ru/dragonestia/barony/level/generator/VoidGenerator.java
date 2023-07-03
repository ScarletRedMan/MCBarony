package ru.dragonestia.barony.level.generator;

import cn.nukkit.api.UsedByReflection;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class VoidGenerator extends Generator {

    private ChunkManager chunkManager;
    private NukkitRandom random;

    public VoidGenerator() {}

    @UsedByReflection
    public VoidGenerator(Map<String, Object> settings) {}

    @Override
    public int getId() {
        return Generator.TYPE_INFINITE;
    }

    @Override
    public void init(ChunkManager chunkManager, NukkitRandom random) {
        this.chunkManager = chunkManager;
        this.random = random;
    }

    @Override
    public void generateChunk(int chunkX, int chunkZ) {}

    @Override
    public void populateChunk(int chunkX, int chunkZ) {}

    @Override
    public Map<String, Object> getSettings() {
        return new HashMap<>();
    }

    @Override
    public String getName() {
        return "void";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0, 60, 0);
    }
}
