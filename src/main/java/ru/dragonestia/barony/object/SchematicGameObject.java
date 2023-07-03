package ru.dragonestia.barony.object;

import cn.nukkit.blockstate.BlockState;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;

public class SchematicGameObject implements GameObject {

    private final String id;
    private final BlockState[][][] blocks; //XZY
    private final boolean canBreak;

    public SchematicGameObject(@NotNull String id, @NotNull BlockState[][][] blocks, boolean canBreak) {
        this.id = id;
        this.blocks = blocks;
        this.canBreak = canBreak;
    }

    @Override
    public @NotNull String id() {
        return id;
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                for (int y = 0; y < 3; y++) {
                    placement.set(x, y, z, blocks[x][z][y]);
                }
            }
        }
    }

    @Override
    public boolean canBreak() {
        return canBreak;
    }

    public @NotNull BlockState[][][] getBlocks() {
        return blocks;
    }
}
