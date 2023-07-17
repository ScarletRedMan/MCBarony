package ru.dragonestia.barony.structure;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.dragonestia.barony.level.generator.StructureViewGenerator;
import ru.dragonestia.barony.level.grid.GlobalGridPlacer;
import ru.dragonestia.barony.level.grid.GridPos;
import ru.dragonestia.barony.level.provider.InMemoryLevelProvider;
import ru.dragonestia.barony.object.GameObject;

public final class WorldStructure extends Structure {

    private final String identifier;
    private int xOffset = 0;
    private int yOffset = 0;
    private int zOffset = 0;
    private GlobalGridPlacer placer = null;

    public WorldStructure(@NotNull String identifier, @NotNull Structure structure) {
        this(identifier, structure.getObjects());
    }

    public WorldStructure(@NotNull String identifier, int xLen, int yLen, int zLen) {
        this(identifier, new GameObject[xLen][zLen][yLen]);
    }

    public WorldStructure(@NotNull String identifier, @NotNull GameObject[][][] objects) {
        super(objects);
        this.identifier = identifier;
    }

    public static @NotNull WorldStructure of(@NotNull Level level) {
        if (level.getProvider() instanceof InMemoryLevelProvider provider) {
            if (provider.getPrettyGenerator() instanceof StructureViewGenerator generator) {
                return generator.getWorld();
            }
        }

        throw new IllegalArgumentException("It's not a structure level");
    }

    public @NotNull String getIdentifier() {
        return identifier;
    }

    public void setOffset(int x, int y, int z) {
        xOffset = x;
        yOffset = y;
        zOffset = z;
    }

    public @NotNull Vector3 getOffset() {
        return new Vector3(xOffset, yOffset, zOffset);
    }

    public void setPlacer(@NotNull GlobalGridPlacer placer) {
        this.placer = placer;
    }

    public @NotNull GridPos gridPosOf(@NotNull Vector3 vec) {
        return GridPos.of(vec.subtract(xOffset, yOffset, zOffset), 0);
    }

    public @NotNull Vector3 startVectorOf(@NotNull GridPos gridPos) {
        return gridPos.getStart(0).add(xOffset, yOffset, zOffset);
    }

    public @NotNull Vector3 centerVectorOf(@NotNull GridPos gridPos) {
        return gridPos.getCenter(0).add(xOffset, yOffset, zOffset);
    }

    public void update(@NotNull GridPos pos, @Nullable GameObject gameObject) {
        place(gameObject, pos.x(), pos.y(), pos.z());

        if (placer == null) return;

        if (gameObject == null) placer.air(pos);
        else placer.place(pos, gameObject);
    }

    public void update(int x, int y, int z, @Nullable GameObject gameObject) {
        update(GridPos.of(x, y, z), gameObject);
    }

    public @Nullable GameObject get(@NotNull GridPos pos) {
        return get(pos.x(), pos.y(), pos.z());
    }

    public @Nullable GameObject get(int x, int y, int z) {
        return getObjects()[x][z][y];
    }
}
