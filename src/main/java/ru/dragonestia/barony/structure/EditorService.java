package ru.dragonestia.barony.structure;

import cn.nukkit.level.Level;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.LevelLoader;
import ru.dragonestia.barony.level.generator.StructureViewGenerator;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.structure.registry.StructureRegistry;

@Singleton
public final class EditorService {

    private final StructureRegistry registry;
    private final ConcurrentHashMap<String, Level> editors = new ConcurrentHashMap<>();

    @Inject
    public EditorService(@NotNull StructureRegistry registry) {
        this.registry = registry;
    }

    public boolean checkStructure(@NotNull String identifier) {
        try {
            registry.findById(identifier);
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isEditingRightNow(@NotNull String identifier) {
        return editors.containsKey(identifier);
    }

    public @NotNull Level createEditorLevel(@NotNull String identifier, @NotNull Structure structure) {
        if (isEditingRightNow(identifier)) return editors.get(identifier);

        var world = new WorldStructure(identifier, structure);

        var level = LevelLoader.createInMemoryLevel(StructureViewGenerator.createFrom(world, GridPlacer.Mode.EDITOR));

        editors.put(identifier, level);
        return level;
    }

    public void close(@NotNull Level level) {
        WorldStructure world;
        try {
            world = WorldStructure.of(level);
        } catch (IllegalArgumentException ex) {
            return;
        }

        editors.remove(world.getIdentifier());
        level.getPlayers().values().forEach(player -> {
            player.teleport(player.getServer().getDefaultLevel().getSafeSpawn());
        });
        level.unload(true);
    }
}
