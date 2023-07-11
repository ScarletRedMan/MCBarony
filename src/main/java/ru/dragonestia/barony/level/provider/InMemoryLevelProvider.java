package ru.dragonestia.barony.level.provider;

import cn.nukkit.api.UsedByReflection;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.level.DimensionData;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.ChunkSection3DBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.format.anvil.ChunkSection;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.util.PalettedBlockStorage;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.ThreadCache;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.generator.PrettyGenerator;

public class InMemoryLevelProvider implements LevelProvider {

    @Getter
    @Setter
    private Level level;

    @Getter
    private final String path = "";

    private final PrettyGenerator generator;
    protected final ConcurrentMap<Long, BaseFullChunk> chunks = new ConcurrentHashMap<>();
    private final ThreadLocal<WeakReference<BaseFullChunk>> lastChunk = new ThreadLocal<>();

    @UsedByReflection
    public InMemoryLevelProvider(Level level, String path) {
        throw new IllegalStateException("Do not use this level provider using by nukkit config!");
    }

    public InMemoryLevelProvider(@NotNull PrettyGenerator generator) {
        this.generator = generator;
    }

    public @NotNull PrettyGenerator getPrettyGenerator() {
        return generator;
    }

    // Я в рот шатал того, кто придумал использовать рефлексию
    @UsedByReflection
    public static String getProviderName() {
        return "unsaved";
    }

    @UsedByReflection
    public static byte getProviderOrder() {
        return ORDER_YZX;
    }

    @UsedByReflection
    public static boolean usesChunkSection() {
        return true;
    }

    @UsedByReflection
    public static boolean isValid(String path) {
        return true;
    }

    @UsedByReflection
    public static void generate(String path, String name, long seed, Class<? extends Generator> generator)
            throws IOException {
        generate(path, name, seed, generator, new HashMap<>());
    }

    @UsedByReflection
    public static void generate(
            String path, String name, long seed, Class<? extends Generator> generator, Map<String, String> options) {}

    @UsedByReflection
    public static ChunkSection createChunkSection(int y) {
        return new ChunkSection(y);
    }

    @Override
    public AsyncTask requestChunkTask(int x, int z) {
        Chunk chunk = (Chunk) getChunk(x, z, false);
        if (chunk == null) throw new ChunkException("Invalid Chunk Set");

        long timestamp = chunk.getChanges();
        BiConsumer<BinaryStream, Integer> callback =
                (stream, subchunks) -> getLevel().chunkRequestCallback(timestamp, x, z, subchunks, stream.getBuffer());
        return new AsyncTask() {

            @Override
            public void onRun() {
                serialize(chunk, callback, getLevel().getDimensionData());
            }
        };
    }

    @Override
    public String getGenerator() {
        return "void";
    }

    @Override
    public Map<String, Object> getGeneratorOptions() {
        return new HashMap<>();
    }

    @Override
    public BaseFullChunk getLoadedChunk(int X, int Z) {
        var tmp = getThreadLastChunk();
        if (tmp != null && tmp.getX() == X && tmp.getZ() == Z) {
            return tmp;
        }
        long index = Level.chunkHash(X, Z);
        lastChunk.set(new WeakReference<>(tmp = chunks.get(index)));
        return tmp;
    }

    @Override
    public BaseFullChunk getLoadedChunk(long hash) {
        var tmp = getThreadLastChunk();
        if (tmp != null && tmp.getIndex() == hash) {
            return tmp;
        }
        lastChunk.set(new WeakReference<>(tmp = chunks.get(hash)));
        return tmp;
    }

    @Override
    public BaseFullChunk getChunk(int X, int Z) {
        return getChunk(X, Z, false);
    }

    @Override
    public BaseFullChunk getChunk(int chunkX, int chunkZ, boolean create) {
        var tmp = getThreadLastChunk();
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        long index = Level.chunkHash(chunkX, chunkZ);
        lastChunk.set(new WeakReference<>(tmp = chunks.get(index)));

        if (tmp == null) {
            tmp = loadChunk(index, chunkX, chunkZ, create);
            lastChunk.set(new WeakReference<>(tmp));
        }

        return tmp;
    }

    @Override
    public BaseFullChunk getEmptyChunk(int x, int z) {
        return Chunk.getEmptyChunk(x, z, this);
    }

    @Override
    public void saveChunks() {}

    @Override
    public void saveChunk(int X, int Z) {}

    @Override
    public void saveChunk(int X, int Z, FullChunk chunk) {}

    @Override
    public void unloadChunks() {
        var iter = chunks.values().iterator();
        while (iter.hasNext()) {
            iter.next().unload(true, false);
            iter.remove();
        }
    }

    @Override
    public boolean loadChunk(int X, int Z) {
        return loadChunk(X, Z, false);
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ, boolean create) {
        long index = Level.chunkHash(chunkX, chunkZ);
        if (chunks.containsKey(index)) return true;
        return loadChunk(index, chunkX, chunkZ, create) != null;
    }

    @Override
    public boolean unloadChunk(int X, int Z) {
        return unloadChunk(X, Z, true);
    }

    @Override
    public boolean unloadChunk(int X, int Z, boolean safe) {
        long index = Level.chunkHash(X, Z);
        BaseFullChunk chunk = chunks.get(index);
        if (chunk != null && chunk.unload(false, safe)) {
            lastChunk.set(null);
            chunks.remove(index, chunk);
            return true;
        }
        return false;
    }

    @Override
    public boolean isChunkGenerated(int X, int Z) {
        return true;
    }

    @Override
    public boolean isChunkPopulated(int X, int Z) {
        return true;
    }

    @Override
    public boolean isChunkLoaded(int X, int Z) {
        BaseFullChunk chunk = this.getChunk(X, Z);
        return chunk != null && chunk.isPopulated();
    }

    @Override
    public boolean isChunkLoaded(long hash) {
        return chunks.containsKey(hash);
    }

    @Override
    public void setChunk(int chunkX, int chunkZ, FullChunk chunk) {
        if (!(chunk instanceof BaseFullChunk)) {
            throw new ChunkException("Invalid Chunk class");
        }

        chunk.setProvider(this);
        chunk.setPosition(chunkX, chunkZ);

        long index = Level.chunkHash(chunkX, chunkZ);
        if (chunks.containsKey(index) && !chunks.get(index).equals(chunk)) {
            unloadChunk(chunkX, chunkZ, false);
        }

        chunks.put(index, (BaseFullChunk) chunk);
    }

    @Override
    public String getName() {
        return "InMemory";
    }

    @Override
    public boolean isRaining() {
        return false;
    }

    @Override
    public void setRaining(boolean raining) {}

    @Override
    public int getRainTime() {
        return 0;
    }

    @Override
    public void setRainTime(int rainTime) {}

    @Override
    public boolean isThundering() {
        return false;
    }

    @Override
    public void setThundering(boolean thundering) {}

    @Override
    public int getThunderTime() {
        return 0;
    }

    @Override
    public void setThunderTime(int thunderTime) {}

    @Override
    public long getCurrentTick() {
        return 0;
    }

    @Override
    public void setCurrentTick(long currentTick) {}

    @Override
    public long getTime() {
        return generator.defaultTime();
    }

    @Override
    public void setTime(long value) {}

    @Override
    public long getSeed() {
        return 0;
    }

    @Override
    public void setSeed(long value) {}

    @Override
    public Vector3 getSpawn() {
        return generator.spawn();
    }

    @Override
    public void setSpawn(Vector3 pos) {}

    @Override
    public Map<Long, ? extends FullChunk> getLoadedChunks() {
        return ImmutableMap.copyOf(chunks);
    }

    @Override
    public void doGarbageCollection() {}

    @Override
    public void close() {
        unloadChunks();
        setLevel(null);
    }

    @Override
    public void saveLevelData() {}

    @Override
    public void updateLevelName(String name) {}

    @Override
    public GameRules getGamerules() {
        return generator.gameRules();
    }

    @Override
    public void setGameRules(GameRules rules) {}

    protected final BaseFullChunk getThreadLastChunk() {
        var ref = lastChunk.get();
        return ref == null ? null : ref.get();
    }

    public BaseFullChunk loadChunk(long index, int chunkX, int chunkZ, boolean create) {
        var chunk = getEmptyChunk(chunkX, chunkZ);
        putChunk(index, chunk);
        generator.generateChunk(level, chunkX, chunkZ);
        return chunk;
    }

    public void putChunk(long index, BaseFullChunk chunk) {
        chunks.put(index, chunk);
    }

    private static byte[] serializeEntities(BaseChunk chunk) {
        List<CompoundTag> tagList = new ObjectArrayList<>();
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
            if (blockEntity instanceof BlockEntitySpawnable) {
                tagList.add(((BlockEntitySpawnable) blockEntity).getSpawnCompound());
            }
        }
        try {
            return NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] serializeBiomes(BaseFullChunk chunk, int sectionCount) {
        var stream = ThreadCache.binaryStream.get().reset();
        if (chunk instanceof cn.nukkit.level.format.Chunk sectionChunk
                && sectionChunk.isChunkSection3DBiomeSupported()) {
            var sections = sectionChunk.getSections();
            var len = Math.min(sections.length, sectionCount);
            final var tmpSectionBiomeStream = new BinaryStream[len];
            for (int i = 0; i < len; i++) { // 确保全部在主线程上分配
                tmpSectionBiomeStream[i] = new BinaryStream(new byte[4096 + 1024]).reset(); // 5KB
            }
            IntStream.range(0, len).parallel().forEach(i -> {
                if (sections[i] instanceof ChunkSection3DBiome each) {
                    var palette = PalettedBlockStorage.createWithDefaultState(
                            Biome.getBiomeIdOrCorrect(chunk.getBiomeId(0, 0) & 0xFF));
                    var biomeData = each.get3DBiomeDataArray();
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = 0; y < 16; y++) {
                                var tmpBiome = Biome.getBiomeIdOrCorrect(biomeData[getAnvilIndex(x, y, z)] & 0xFF);
                                palette.setBlock(x, y, z, tmpBiome);
                            }
                        }
                    }
                    palette.writeTo(tmpSectionBiomeStream[i]);
                } else {
                    var palette = PalettedBlockStorage.createWithDefaultState(
                            Biome.getBiomeIdOrCorrect(chunk.getBiomeId(0, 0) & 0xFF));
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            int biomeId = Biome.getBiomeIdOrCorrect(chunk.getBiomeId(x, z) & 0xFF);
                            for (int y = 0; y < 16; y++) {
                                palette.setBlock(x, y, z, biomeId);
                            }
                        }
                    }
                    palette.writeTo(tmpSectionBiomeStream[i]);
                }
            });
            for (int i = 0; i < len; i++) {
                stream.put(tmpSectionBiomeStream[i].getBuffer());
            }
        } else {
            PalettedBlockStorage palette = PalettedBlockStorage.createWithDefaultState(
                    Biome.getBiomeIdOrCorrect(chunk.getBiomeId(0, 0) & 0xFF));
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int biomeId = Biome.getBiomeIdOrCorrect(chunk.getBiomeId(x, z));
                    for (int y = 0; y < 16; y++) {
                        palette.setBlock(x, y, z, biomeId);
                    }
                }
            }

            palette.writeTo(stream);
            byte[] bytes = stream.getBuffer();
            stream.reset();

            for (int i = 0; i < sectionCount; i++) {
                stream.put(bytes);
            }
        }
        return stream.getBuffer();
    }

    private void serialize(BaseChunk chunk, BiConsumer<BinaryStream, Integer> callback, DimensionData dimensionData) {
        byte[] blockEntities;
        if (chunk.getBlockEntities().isEmpty()) {
            blockEntities = new byte[0];
        } else {
            blockEntities = serializeEntities(chunk);
        }

        int subChunkCount = 0;
        cn.nukkit.level.format.ChunkSection[] sections = chunk.getSections();
        for (int i = sections.length - 1; i >= 0; i--) {
            if (!sections[i].isEmpty()) {
                subChunkCount = i + 1;
                break;
            }
        }

        int maxDimensionSections = dimensionData.getHeight() >> 4;
        subChunkCount = Math.min(maxDimensionSections, subChunkCount);

        byte[] biomePalettes = serializeBiomes(chunk, maxDimensionSections);
        BinaryStream stream = ThreadCache.binaryStream.get().reset();

        int writtenSections = subChunkCount;

        final var tmpSubChunkStreams = new BinaryStream[subChunkCount];
        for (int i = 0; i < subChunkCount; i++) {
            tmpSubChunkStreams[i] = new BinaryStream(new byte[8192]).reset(); // 8KB
        }
        if (getLevel() != null && getLevel().isAntiXrayEnabled()) {
            IntStream.range(0, subChunkCount)
                    .parallel()
                    .forEach(i -> sections[i].writeObfuscatedTo(tmpSubChunkStreams[i], getLevel()));
        } else {
            IntStream.range(0, subChunkCount).parallel().forEach(i -> sections[i].writeTo(tmpSubChunkStreams[i]));
        }
        for (int i = 0; i < subChunkCount; i++) {
            stream.put(tmpSubChunkStreams[i].getBuffer());
        }

        stream.put(biomePalettes);
        stream.putByte((byte) 0); // Border blocks
        stream.put(blockEntities);
        callback.accept(stream, writtenSections);
    }

    private static int getAnvilIndex(int x, int y, int z) {
        return (y << 8) + (z << 4) + x; // YZX
    }
}
