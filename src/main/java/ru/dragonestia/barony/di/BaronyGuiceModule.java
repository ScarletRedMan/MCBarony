package ru.dragonestia.barony.di;

import cn.nukkit.utils.Logger;
import com.google.inject.AbstractModule;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.registry.DiskObjectRegistry;
import ru.dragonestia.barony.object.registry.ObjectRegistry;
import ru.dragonestia.barony.object.serializer.NbtObjectSerializer;
import ru.dragonestia.barony.object.serializer.ObjectSerializer;
import ru.dragonestia.barony.structure.registry.StorageStructureRegistry;
import ru.dragonestia.barony.structure.registry.StructureRegistry;
import ru.dragonestia.barony.structure.serializer.BinaryStreamStructureSerializer;
import ru.dragonestia.barony.structure.serializer.StructureSerializer;

public class BaronyGuiceModule extends AbstractModule {

    private final Logger logger;
    private final Path dataFolder;

    public BaronyGuiceModule(@NotNull Logger logger, @NotNull Path dataFolder) {
        this.logger = logger;
        this.dataFolder = dataFolder;
    }

    @Override
    protected void configure() {
        bind(Logger.class).toInstance(logger);
        bind(Path.class).annotatedWith(DataFolder.class).toInstance(dataFolder);
        bind(ObjectSerializer.class).to(NbtObjectSerializer.class).asEagerSingleton();
        bind(ObjectRegistry.class).to(DiskObjectRegistry.class).asEagerSingleton();
        bind(StructureRegistry.class).to(StorageStructureRegistry.class).asEagerSingleton();
        bind(StructureSerializer.class)
                .to(BinaryStreamStructureSerializer.class)
                .asEagerSingleton();
    }
}
