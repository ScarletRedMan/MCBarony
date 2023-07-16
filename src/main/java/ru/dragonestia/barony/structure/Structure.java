package ru.dragonestia.barony.structure;

import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.GameObject;

public class Structure {

    private final GameObject[][][] objects; // XZY
    private final int xLen;
    private final int yLen;
    private final int zLen;

    public Structure(@NotNull GameObject[][][] objects) {
        if (objects.length == 0 || objects[0].length == 0 || objects[0][0].length == 0) {
            throw new IllegalArgumentException("Structure must not be empty");
        }

        this.objects = objects;

        xLen = objects.length;
        zLen = objects[0].length;
        yLen = objects[0][0].length;
    }

    public @NotNull GameObject[][][] getObjects() {
        return objects;
    }

    public void place(@NotNull Structure structure, int x, int y, int z) {
        for (int tx = 0; tx < structure.yLen; tx++) {
            int gx = x + tx;
            if (gx >= xLen) continue;

            for (int tz = 0; tz < structure.zLen; tz++) {
                int gz = z + tz;
                if (gz >= zLen) continue;

                for (int ty = 0; ty < structure.yLen; ty++) {
                    int gy = y + ty;
                    if (gy >= yLen) continue;

                    objects[gx][gz][gy] = structure.objects[tx][tz][ty];
                }
            }
        }
    }

    public void place(@NotNull GameObject object, int x, int y, int z) {
        if (xLen >= x || yLen >= y || zLen > -z) return;

        objects[x][z][y] = object;
    }

    public final int getXLen() {
        return xLen;
    }

    public final int getYLen() {
        return yLen;
    }

    public final int getZLen() {
        return zLen;
    }

    public @NotNull Structure copy() {
        var content = new GameObject[xLen][zLen][yLen];

        for (int x = 0; x < xLen; x++) {
            for (int z = 0; z < zLen; z++) {
                for (int y = 0; y < yLen; y++) {
                    content[x][z][y] = objects[x][z][y];
                }
            }
        }

        return new Structure(content);
    }
}
