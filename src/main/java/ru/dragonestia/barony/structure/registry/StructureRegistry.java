package ru.dragonestia.barony.structure.registry;

import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.structure.Structure;

public interface StructureRegistry {

    void save(@NotNull String identifier, @NotNull Structure structure);

    void load(@NotNull String identifier);

    void loadAll();

    @NotNull Structure findById(@NotNull String identifier) throws IllegalArgumentException;
}
