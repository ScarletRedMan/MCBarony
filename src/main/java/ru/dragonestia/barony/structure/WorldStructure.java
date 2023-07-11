package ru.dragonestia.barony.structure;

import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.generator.StructureViewGenerator;
import ru.dragonestia.barony.level.provider.InMemoryLevelProvider;
import ru.dragonestia.barony.object.GameObject;

public final class WorldStructure extends Structure {

    public WorldStructure(int xLen, int yLen, int zLen) {
        this(new GameObject[xLen][zLen][yLen]);
    }

    public WorldStructure(@NotNull GameObject[][][] objects) {
        super(objects);
    }

    public static @NotNull WorldStructure of(@NotNull Level level) {
        if (level.getProvider() instanceof InMemoryLevelProvider provider) {
            if (provider.getPrettyGenerator() instanceof StructureViewGenerator generator) {
                return generator.getWorld();
            }
        }

        throw new IllegalArgumentException("It's not a structure level");
    }
}
