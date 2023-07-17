package ru.dragonestia.barony.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.DimensionEnum;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

public interface PrettyGenerator {

    @NotNull String name();

    default DimensionEnum dimension() {
        return DimensionEnum.OVERWORLD;
    }

    default Vector3 spawn() {
        return new Vector3(0, 60, 0);
    }

    default GameRules gameRules() {
        return GameRules.getDefault();
    }

    default int defaultTime() {
        return Level.TIME_DAY;
    }

    void generateChunk(@NotNull ChunkManager level, int chunkX, int chunkZ);

    default void provideLevel(@NotNull Level level) {}
}
