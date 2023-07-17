package ru.dragonestia.barony.object.procedural;

import cn.nukkit.block.BlockSmoothStone;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class StoneBricksSlabFloorObj implements GameObject {

    @Override
    public @NotNull String id() {
        return "stone_bricks_slab_floor";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        placement.fill(new BlockSmoothStone());
    }
}
