package ru.dragonestia.barony.object.registry;

import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.GameObject;
import ru.dragonestia.barony.object.SchematicGameObject;
import ru.dragonestia.barony.object.editor.EditorBorderObj;
import ru.dragonestia.barony.object.editor.EditorFloorObj;
import ru.dragonestia.barony.object.procedural.*;

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

        registry.register(new GrassObj());
        registry.register(new WoodObj());
        registry.register(new LeavesObj());
        registry.register(new DirtMineWallObj());
        registry.register(new StoneWallObj());
        registry.register(new DirtFloorObj());
        registry.register(new StoneBricksWallObj());
        registry.register(new StoneBricksSlabFloorObj());
        registry.register(new StoneBricksDecoratedObj());
        registry.register(new PlanksObj());
    }

    int getRuntimeIdFor(@NotNull GameObject object);
}
