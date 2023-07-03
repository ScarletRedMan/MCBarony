package ru.dragonestia.barony.level.grid;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.Vector3;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.GameObject;

import java.util.function.Consumer;

public final class GridPlacer {

    private final ChunkManager level;
    private final int chunkX;
    private final int chunkZ;
    private final int floor;
    private final Mode mode;

    public GridPlacer(@NotNull ChunkManager level, int chunkX, int chunkZ, int floor, @NotNull Mode mode) {
        this.level = level;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.floor = floor;
        this.mode = mode;
    }

    public void place(@NotNull GridPos pos, @NotNull GameObject object) {
        var placement = new Placement(level, chunkX, chunkZ, pos, pos.getStart(floor));

        if (mode == Mode.GAME) object.placeForGame(placement);
        else if (mode == Mode.EDITOR) object.placeForEditor(placement);
    }

    public enum Mode {
        EDITOR,
        GAME
    }

    @RequiredArgsConstructor
    public static final class Placement {

        private final ChunkManager level;
        private final int chunkX;
        private final int chunkZ;
        private final GridPos gridPos;
        private final Vector3 pos;

        public @NotNull GridPos position() {
            return gridPos;
        }

        public void set(int x, int y, int z, @NotNull BlockState block) {
            int gx = pos.getFloorX() + x;
            int gz = pos.getFloorZ() + z;

            if (chunkX != (gx >> 4) || chunkZ != (gz >> 4)) return;
            level.setBlockStateAt(gx, pos.getFloorY() + y, gz, block);
        }

        public void set(int x, int y, int z, @NotNull Block block) {
            set(x, y, z, block.getCurrentState());
        }

        public void fill(@NotNull BlockState block) {
            for (int x = 0; x < 3; x++) {
                int gx = pos.getFloorX() + x;

                if (chunkX != (gx >> 4)) continue;

                for (int z = 0; z < 3; z++) {
                    int gz = pos.getFloorZ() + z;

                    if (chunkZ != (gz >> 4)) continue;

                    for (int y = 0, sy = pos.getFloorY(); y < 3; y++) {
                        level.setBlockStateAt(gx, sy + y, gz, block);
                    }
                }
            }
        }

        public void fill(@NotNull Block block) {
            fill(block.getCurrentState());
        }
    }
}
