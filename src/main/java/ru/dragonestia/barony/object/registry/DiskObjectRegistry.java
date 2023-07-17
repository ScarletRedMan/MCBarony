package ru.dragonestia.barony.object.registry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.di.DataFolder;
import ru.dragonestia.barony.object.GameObject;
import ru.dragonestia.barony.object.SchematicGameObject;
import ru.dragonestia.barony.object.serializer.ObjectSerializer;

@Singleton
public class DiskObjectRegistry implements ObjectRegistry {

    @Inject
    private ObjectSerializer serializer;

    @Inject
    @DataFolder
    private Path dataFolder;

    private final ConcurrentHashMap<String, GameObject> objects = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> object2runtimeId = new ConcurrentHashMap<>();
    private final AtomicInteger freeRuntimeId = new AtomicInteger(1);

    @Override
    public @NotNull GameObject findById(@NotNull String id) throws IllegalArgumentException {
        var result = objects.getOrDefault(id, null);

        if (result == null) throw new IllegalArgumentException("Objects with id '" + id + "' not found");
        return result;
    }

    @Override
    public void register(@NotNull GameObject obj) {
        objects.put(obj.id(), obj);
        object2runtimeId.put(obj.id(), freeRuntimeId.getAndIncrement());
    }

    @Override
    public void save(@NotNull SchematicGameObject obj) {
        var filePath = dataFolder.resolve("objects/" + obj.id());

        try {
            Files.write(filePath, serializer.serialize(obj));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public @NotNull SchematicGameObject load(@NotNull String id) {
        var filePath = dataFolder.resolve("objects/" + id);

        try {
            return serializer.deserialize(id, Files.readAllBytes(filePath));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void loadAll() {
        var objectsFolder = dataFolder.resolve("objects/").toFile();

        if (!objectsFolder.exists()) objectsFolder.mkdir();

        Arrays.stream(Objects.requireNonNull(objectsFolder.list((file, name) -> file.isDirectory())))
                .map(this::load)
                .forEach(this::register);
    }

    @Override
    public int getRuntimeIdFor(@NotNull GameObject object) {
        return object2runtimeId.getOrDefault(object.id(), AIR);
    }
}
