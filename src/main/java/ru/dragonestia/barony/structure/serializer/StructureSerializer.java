package ru.dragonestia.barony.structure.serializer;

import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.structure.Structure;

public interface StructureSerializer {

    @NotNull byte[] serialize(@NotNull Structure structure);

    @NotNull Structure deserialize(@NotNull byte[] bytes);
}
