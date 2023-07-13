package ru.dragonestia.barony.structure.registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.di.DataFolder;
import ru.dragonestia.barony.structure.Structure;
import ru.dragonestia.barony.structure.serializer.StructureSerializer;

@Singleton
public class StorageStructureRegistry implements StructureRegistry {

    private final Path dataFolder;
    private final StructureSerializer serializer;
    private final ConcurrentHashMap<String, Structure> structures = new ConcurrentHashMap();

    @Inject
    public StorageStructureRegistry(@DataFolder Path dataFolder, @NotNull StructureSerializer serializer) {
        this.dataFolder = dataFolder.resolve("structures/");
        this.serializer = serializer;
    }

    @Override
    public void save(@NotNull String identifier, @NotNull Structure structure) {
        var filePath = dataFolder.resolve(identifier);

        try {
            Files.write(filePath, serializer.serialize(structure));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void load(@NotNull String identifier) {
        var filePath = dataFolder.resolve(identifier);

        try {
            structures.put(identifier, serializer.deserialize(Files.readAllBytes(filePath)));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void loadAll() {
        var structuresFolder = dataFolder.toFile();
        if (!structuresFolder.exists()) structuresFolder.mkdir();

        for (var identifier : Objects.requireNonNull(structuresFolder.list((dir, name) -> dir.isDirectory()))) {
            load(identifier);
        }
    }

    @Override
    public @NotNull Structure findById(@NotNull String identifier) throws IllegalArgumentException {
        var result = structures.getOrDefault(identifier, null);
        if (result == null) {
            throw new IllegalArgumentException("Structure with identifier '" + identifier + "' is not found.");
        }

        return result;
    }
}
