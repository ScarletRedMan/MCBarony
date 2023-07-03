package ru.dragonestia.barony.level.grid;

import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

public final class GridPos {

    private final int x, y, z;

    private GridPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static @NotNull GridPos of(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0) {
            throw new IllegalArgumentException("Grid pos must not have any negative number");
        }

        return new GridPos(x, y, z);
    }

    public static @NotNull GridPos of(@NotNull Vector3 vec, int floor) {
        return of(vec.getFloorX() / 3, (vec.getFloorY() - floor - 1) / 3, vec.getFloorZ() / 3);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int z() {
        return z;
    }

    public @NotNull Vector3 getStart(int floor) {
        return new Vector3(x * 3, y * 3 + floor + 1, z * 3);
    }

    public @NotNull Vector3 getCenter(int floor) {
        return getStart(floor).add(1, 1, 1);
    }

    public @NotNull GridPos withY(int y) {
        return GridPos.of(x, y, z);
    }
}
