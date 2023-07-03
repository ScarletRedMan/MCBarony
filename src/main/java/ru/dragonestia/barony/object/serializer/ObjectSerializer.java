package ru.dragonestia.barony.object.serializer;

import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.SchematicGameObject;

public interface ObjectSerializer {

    @NotNull byte[] serialize(@NotNull SchematicGameObject obj);

    @NotNull SchematicGameObject deserialize(@NotNull String id, @NotNull byte[] bytes);
}
