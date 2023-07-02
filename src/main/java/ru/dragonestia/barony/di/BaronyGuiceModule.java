package ru.dragonestia.barony.di;

import cn.nukkit.utils.Logger;
import com.google.inject.AbstractModule;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;

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
    }
}
