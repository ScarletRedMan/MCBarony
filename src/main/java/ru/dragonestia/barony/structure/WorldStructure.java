package ru.dragonestia.barony.structure;

import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.generator.StructureViewGenerator;
import ru.dragonestia.barony.level.provider.InMemoryLevelProvider;
import ru.dragonestia.barony.object.GameObject;

public final class WorldStructure extends Structure {

    private final String identifier;

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
}
