package ru.dragonestia.barony.object.procedural;

import cn.nukkit.block.BlockDirt;
import cn.nukkit.block.BlockDirtWithRoots;
import cn.nukkit.block.BlockFence;
import cn.nukkit.block.BlockPlanks;
import cn.nukkit.blockproperty.value.DirtType;
import cn.nukkit.blockproperty.value.WoodType;
import cn.nukkit.blockstate.BlockState;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class DirtMineWallObj implements GameObject {

    @Override
    public @NotNull String id() {
        return "mine_wall_dirt";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        var dirt1 = createDirtState();
        var dirt2 = new BlockDirtWithRoots().getCurrentState();
        var planks = createPlanksState();
        var column = new BlockFence().getCurrentState();

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                for (int y = 0; y < 2; y++) {
                    if (((x + z) & 0b1) == 0) {
                        placement.set(x, y, z, RANDOM.nextBoolean() ? dirt1 : dirt2);
                    } else {
                        placement.set(x, y, z, column);
                    }
                }

                placement.set(x, 2, z, planks);
            }
        }
    }

    private @NotNull BlockState createDirtState() {
        var block = new BlockDirt();
        block.setDirtType(DirtType.COARSE);
        return block.getCurrentState();
    }

    private @NotNull BlockState createPlanksState() {
        var block = new BlockPlanks();
        block.setWoodType(WoodType.SPRUCE);
        return block.getCurrentState();
    }
}
