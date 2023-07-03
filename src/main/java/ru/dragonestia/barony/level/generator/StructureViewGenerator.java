package ru.dragonestia.barony.level.generator;

import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.level.grid.GridPos;
import ru.dragonestia.barony.object.GameObject;
import ru.dragonestia.barony.object.editor.EditorBorderObj;
import ru.dragonestia.barony.object.editor.EditorFloorObj;

public class StructureViewGenerator implements PrettyGenerator {

    public final static int Y_FLOOR = 0;
    public final static int OBJECTS_PER_CHUNK = 16 / 3 + 1;

    private final int xSize, ySize, zSize;
    private final GameObject floorObj = new EditorFloorObj();
    private final GameObject borderObj = new EditorBorderObj();
    private final GridPlacer.Mode mode;
    private final GameObject[][][] objects; //XZY

    public StructureViewGenerator(int xSize, int ySize, int zSize, GridPlacer.Mode mode, GameObject[][][] objects) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.mode = mode;
        this.objects = objects;
    }

    @NotNull
    @Override
    public String name() {
        return "structure_view";
    }

    @Override
    public void generateChunk(@NotNull ChunkManager level, int chunkX, int chunkZ) {
        if (chunkX < 0 || chunkZ < 0) return;

        int sx = chunkX << 4;
        int sz = chunkZ << 4;
        var start = GridPos.of(new Vector3(sx, Y_FLOOR + 1, sz), Y_FLOOR);
        var gridPlacer = new GridPlacer(level, chunkX, chunkZ, Y_FLOOR, mode);

        for (int dx = 0; dx < OBJECTS_PER_CHUNK; dx++) {
            int objX = start.x() + dx;

            if (objX > xSize + 1) continue;

            for (int dz = 0; dz < OBJECTS_PER_CHUNK; dz++) {
                int objZ = start.z() + dz;

                if (objZ > xSize + 1) continue;

                var floorPos = GridPos.of(objX, 0, objZ);
                if (objX == 0 || objZ == 0 || objX == xSize + 1 || objZ == zSize + 1) {
                    gridPlacer.place(floorPos, borderObj);

                    if (mode == GridPlacer.Mode.GAME) {
                        for (int i = 1; i <= ySize; i++) {
                            gridPlacer.place(floorPos.withY(floorPos.y() + i), borderObj);
                        }
                    }
                } else {
                    gridPlacer.place(floorPos, floorObj);

                    //placing objects
                    for (int i = 0; i < ySize; i++) {
                        var obj = objects[floorPos.x() - 1][floorPos.z() - 1][i];

                        if (obj == null) continue;

                        gridPlacer.place(floorPos.withY(floorPos.y() + 1 + i), obj);
                    }
                }
            }
        }
    }

    @Override
    public Vector3 spawn() {
        return new Vector3(2.5, Y_FLOOR + 3, 2.5);
    }
}