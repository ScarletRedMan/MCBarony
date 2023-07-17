package ru.dragonestia.barony.level.grid;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class GlobalGridPlacer extends GridPlacer {

    private final Vector3 offset;

    public GlobalGridPlacer(@NotNull ChunkManager level, @NotNull Vector3 offset, @NotNull Mode mode) {
        super(level, 0, 0, 0, mode);

        this.offset = offset;
    }

    @Override
    protected @NotNull Placement createPlacement(@NotNull GridPos pos) {
        return new GlobalPlacement(level, pos, pos.getStart(0).add(offset));
    }

    public static class GlobalPlacement extends Placement {

        public GlobalPlacement(@NotNull ChunkManager level, @NotNull GridPos gridPos, @NotNull Vector3 pos) {
            super(level, 0, 0, gridPos, pos);
        }

        @Override
        public void set(int x, int y, int z, @NotNull BlockState block) {
            int gx = pos.getFloorX() + x;
            int gz = pos.getFloorZ() + z;

            level.setBlockStateAt(gx, pos.getFloorY() + y, gz, block);
        }

        @Override
        public void fill(@NotNull BlockState block) {
            for (int x = 0; x < 3; x++) {
                int gx = pos.getFloorX() + x;

                for (int z = 0; z < 3; z++) {
                    int gz = pos.getFloorZ() + z;

                    for (int y = 0, sy = pos.getFloorY(); y < 3; y++) {
                        level.setBlockStateAt(gx, sy + y, gz, block);
                    }
                }
            }
        }
    }
}
