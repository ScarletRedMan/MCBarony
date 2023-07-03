package ru.dragonestia.barony.object.serializer;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import java.io.IOException;
import java.nio.ByteOrder;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.SchematicGameObject;

@Singleton
public class NbtObjectSerializer implements ObjectSerializer {

    @Override
    public @NotNull byte[] serialize(@NotNull SchematicGameObject obj) {
        var root = new CompoundTag();
        var blocksTag = new CompoundTag("Blocks");
        var blocks = obj.getBlocks();

        for (int x = 0, i = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                for (int y = 0; y < 3; y++) {
                    blocksTag.putString(Integer.toString(i++), blocks[x][z][y].getStateId());
                }
            }
        }

        root.putCompound(blocksTag);
        root.putBoolean("CanBreak", obj.canBreak());

        byte[] bytes;
        try {
            bytes = NBTIO.write(root);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return bytes;
    }

    @Override
    public @NotNull SchematicGameObject deserialize(@NotNull String id, @NotNull byte[] bytes) {
        CompoundTag root;
        try {
            root = NBTIO.read(bytes, ByteOrder.LITTLE_ENDIAN);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        var blocksTag = root.getCompound("Blocks");
        BlockState[][][] blocks = new BlockState[3][3][3];
        for (int x = 0, i = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                for (int y = 0; y < 3; y++) {
                    blocks[x][z][y] = BlockState.of(blocksTag.getShort(Integer.toString(i++)));
                }
            }
        }

        var canBreak = root.getBoolean("CanBreak");

        return new SchematicGameObject(id, blocks, canBreak);
    }
}
