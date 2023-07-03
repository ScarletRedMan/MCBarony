package ru.dragonestia.barony.object.editor;

import cn.nukkit.block.BlockBedrock;
import cn.nukkit.block.BlockConcrete;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.GameObject;

public class EditorBorderObj implements GameObject {

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        placement.fill(new BlockBedrock());
    }

    @Override
    public void placeForEditor(@NotNull GridPlacer.Placement placement) {
        placement.fill(new BlockConcrete(DyeColor.YELLOW.getWoolData()));
    }
}
