package ru.dragonestia.barony.object;

import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;

public interface GameObject {

    @NotNull String id();

    void placeForGame(@NotNull GridPlacer.Placement placement);

    default void placeForEditor(@NotNull GridPlacer.Placement placement) {
        placeForGame(placement);
    }
}
