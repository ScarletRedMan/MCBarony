package ru.dragonestia.barony.object.procedural;

import cn.nukkit.block.BlockBricksStone;
import cn.nukkit.block.BlockDoubleSlabStone;
import cn.nukkit.blockproperty.value.StoneSlab1Type;
import cn.nukkit.blockstate.BlockState;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class StoneBricksWallObj implements GameObject {

    @Override
    public @NotNull String id() {
        return "stone_bricks_wall";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        var pos = placement.position();
        var bricks = new BlockBricksStone();
        var column = createColumnState();

        if (((pos.x() + pos.z()) & 0b1) == 0) {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    if (((x + z) & 0b1) != 0) {
                        for (int y = 0; y < 3; y++) {
                            placement.set(x, y, z, column);
                        }
                        continue;
                    }

                    for (int y = 0; y < 3; y++) {
                        placement.set(x, y, z, bricks);
                    }
                }
            }
            return;
        }

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                for (int y = 0; y < 3; y++) {
                    placement.set(x, y, z, bricks);
                }
            }
        }
    }

    private @NotNull BlockState createColumnState() {
        var block = new BlockDoubleSlabStone();
        block.setSlabType(StoneSlab1Type.SMOOTH_STONE);
        return block.getCurrentState();
    }
}
