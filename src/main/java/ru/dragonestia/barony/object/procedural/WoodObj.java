package ru.dragonestia.barony.object.procedural;

import cn.nukkit.block.BlockBarrier;
import cn.nukkit.block.BlockWood;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class WoodObj implements GameObject {

    @Override
    public @NotNull String id() {
        return "wood";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        var pos = placement.position();
        var even = ((pos.x() + pos.z()) & 0b1) == 0;

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                var place = (((x + z) & 0b1) == 0) == even;

                for (int y = 0; y < 3; y++) {
                    placement.set(x, y, z, place ? new BlockWood() : new BlockBarrier());
                }
            }
        }
    }
}
