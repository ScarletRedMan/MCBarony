package ru.dragonestia.barony.level.generator;

import cn.nukkit.block.BlockStone;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;
import org.jetbrains.annotations.NotNull;

public class FlatGenerator implements PrettyGenerator {

    @Override
    public @NotNull String name() {
        return "flat";
    }

    @Override
    public void generateChunk(@NotNull ChunkManager level, int chunkX, int chunkZ) {
        for (int x = 0; x < 16; x++) {
            int gx = (chunkX << 4) + x;
            for (int z = 0; z < 16; z++) {
                int gz = (chunkZ << 4) + z;

                level.setBlockStateAt(gx , 58, gz, new BlockStone().getCurrentState());
            }
        }
    }

    @Override
    public GameRules gameRules() {
        var rules = PrettyGenerator.super.gameRules();
        rules.setGameRule(GameRule.SHOW_COORDINATES, true);
        return rules;
    }
}
