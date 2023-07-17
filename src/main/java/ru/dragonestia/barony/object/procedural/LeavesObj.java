package ru.dragonestia.barony.object.procedural;

import cn.nukkit.block.BlockMoss;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class LeavesObj implements GameObject {

    @Override
    public @NotNull String id() {
        return "leaves";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        placement.fill(new BlockMoss());
    }
}
