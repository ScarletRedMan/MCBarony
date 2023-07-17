package ru.dragonestia.barony.object.procedural;

import cn.nukkit.block.BlockPlanks;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class PlanksObj implements GameObject {

    @Override
    public @NotNull String id() {
        return "planks";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        placement.fill(new BlockPlanks());
    }
}
