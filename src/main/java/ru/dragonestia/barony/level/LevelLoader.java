package ru.dragonestia.barony.level;

import cn.nukkit.Server;
import cn.nukkit.event.level.LevelInitEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.math.Vector3;
import java.io.File;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.generator.PrettyGenerator;
import ru.dragonestia.barony.level.provider.InMemoryLevelProvider;

public final class LevelLoader {

    private LevelLoader() {}

    public static Level createInMemoryLevel(@NotNull PrettyGenerator generator) {
        var server = Server.getInstance();
        var provider = new InMemoryLevelProvider(generator);
        Level level;
        try {
            var constructor = Level.class.getDeclaredConstructor(
                    Server.class, String.class, File.class, boolean.class, LevelProvider.class);

            constructor.setAccessible(true);
            level = constructor.newInstance(server, "InMemory_" + UUID.randomUUID(), new File("./"), true, provider);
            provider.setLevel(level);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        server.getLevels().put(level.getId(), level);

        level.initLevel();
        level.setTickRate(1);

        server.getPluginManager().callEvent(new LevelInitEvent(level));
        server.getPluginManager().callEvent(new LevelLoadEvent(level));

        preloadChunks(level, generator.spawn());
        return level;
    }

    private static void preloadChunks(@NotNull Level level, @NotNull Vector3 spawnPos) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                level.loadChunk(spawnPos.getChunkX() + x, spawnPos.getChunkZ() + z);
            }
        }
    }
}
