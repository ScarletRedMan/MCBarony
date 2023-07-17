package ru.dragonestia.barony.object.procedural;

import cn.nukkit.block.BlockBlackstone;
import cn.nukkit.block.BlockBricksStone;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class StoneBricksDecoratedObj implements GameObject {

    @Override
    public @NotNull String id() {
        return "stone_bricks_decorated";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        var bricks = new BlockBricksStone().getCurrentState();
        var decoration = new BlockBlackstone().getCurrentState();

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                for (int y = 0; y < 3; y++) {
                    placement.set(x, y, z, y == 1 ? decoration : bricks);
                }
            }
        }
    }
}
