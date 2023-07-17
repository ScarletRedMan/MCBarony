package ru.dragonestia.barony.object.procedural;

import cn.nukkit.block.BlockPackedMud;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class DirtFloorObj implements GameObject {

    @Override
    public @NotNull String id() {
        return "dirt_floor";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        placement.fill(new BlockPackedMud());
    }
}
