package ru.dragonestia.barony.object;

import java.util.Random;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;

public interface GameObject {

    Random RANDOM = new Random();

    @NotNull String id();

    void placeForGame(@NotNull GridPlacer.Placement placement);

    default void placeForEditor(@NotNull GridPlacer.Placement placement) {
        placeForGame(placement);
    }

    default boolean canBreak() {
        return false;
    }
}
