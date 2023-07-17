package ru.dragonestia.barony.object.procedural;

import cn.nukkit.block.BlockCobblestone;
import cn.nukkit.block.BlockTuff;
import cn.nukkit.block.BlockWood;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class StoneWallObj implements GameObject {

    @Override
    public @NotNull String id() {
        return "stone_wall";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        var pos = placement.position();
        var stone1 = new BlockCobblestone().getCurrentState();
        var stone2 = new BlockTuff().getCurrentState();
        var column = new BlockWood().getCurrentState();

        if (((pos.x() + pos.z()) & 0b1) == 0) {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    if (((x + z) & 0b1) == 0) {
                        for (int y = 0; y < 3; y++) {
                            placement.set(x, y, z, column);
                        }
                        continue;
                    }

                    for (int y = 0; y < 3; y++) {
                        placement.set(x, y, z, RANDOM.nextBoolean() ? stone1 : stone2);
                    }
                }
            }
            return;
        }

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                for (int y = 0; y < 3; y++) {
                    placement.set(x, y, z, RANDOM.nextBoolean() ? stone1 : stone2);
                }
            }
        }
    }
}
