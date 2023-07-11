package ru.dragonestia.barony.object.registry;

import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.GameObject;
import ru.dragonestia.barony.object.SchematicGameObject;
import ru.dragonestia.barony.object.editor.EditorBorderObj;
import ru.dragonestia.barony.object.editor.EditorFloorObj;

public interface ObjectRegistry {

    int AIR = 0;

    @NotNull GameObject findById(@NotNull String id) throws IllegalArgumentException;

    void register(@NotNull GameObject obj);

    void save(@NotNull SchematicGameObject obj);

    @NotNull SchematicGameObject load(@NotNull String id);

    void loadAll();

    static void registerDefault(@NotNull ObjectRegistry registry) {
        registry.register(new EditorBorderObj());
        registry.register(new EditorFloorObj());
    }

    int getRuntimeIdFor(@NotNull GameObject object);
}
