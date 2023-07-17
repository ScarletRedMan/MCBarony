package ru.dragonestia.barony.object.procedural;

import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockGrass;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class GrassObj implements GameObject {

    @Override
    public @NotNull String id() {
        return "grass";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                for (int y = 0; y < 3; y++) {
                    if (y == 2) {
                        placement.set(x, y, z, new BlockGrass());
                        continue;
                    }

                    placement.set(x, y, z, new BlockDirt());
                }
            }
        }
    }
}
