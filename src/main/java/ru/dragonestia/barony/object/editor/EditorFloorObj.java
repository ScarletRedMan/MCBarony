package ru.dragonestia.barony.object.editor;

import cn.nukkit.block.BlockConcrete;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.utils.DyeColor;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.object.HiddenGameObject;

public class EditorFloorObj implements HiddenGameObject {

    @Override
    public @NotNull String id() {
        return "dev_floor";
    }

    @Override
    public void placeForGame(@NotNull GridPlacer.Placement placement) {
        placement.fill(new BlockConcrete(DyeColor.BLACK.getWoolData()));
    }

    @Override
    public void placeForEditor(@NotNull GridPlacer.Placement placement) {
        var pos = placement.position();
        var color = ((pos.x() + pos.z()) & 0b1) == 0 ? DyeColor.WHITE : DyeColor.LIGHT_GRAY;

        placement.fill(BlockState.of(BlockID.WOOL, color.getWoolData()));
    }
}
