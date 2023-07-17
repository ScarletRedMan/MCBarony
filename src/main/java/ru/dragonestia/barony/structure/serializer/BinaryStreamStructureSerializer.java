package ru.dragonestia.barony.structure.serializer;

import cn.nukkit.utils.BinaryStream;
import java.util.HashMap;
import java.util.HashSet;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.GameObject;
import ru.dragonestia.barony.object.registry.ObjectRegistry;
import ru.dragonestia.barony.structure.Structure;

@Singleton
public class BinaryStreamStructureSerializer implements StructureSerializer {

    /*
    Structure:
    int | xLen
    int | yLen
    int | zLen
    int | Palette size
    [ Palette
        int | Runtime object id
        string | Permanent object id
    ]
    [ List of runtime object ids. Order XZY
        int | Runtime object id
    ]
    byte | End symbol. value = 0x00 => finish operation. It's for backward compatibility
     */

    private final ObjectRegistry objectRegistry;

    @Inject
    public BinaryStreamStructureSerializer(@NotNull ObjectRegistry objectRegistry) {
        this.objectRegistry = objectRegistry;
    }

    private static final byte END = 0x00;

    @NotNull @Override
    public byte[] serialize(@NotNull Structure structure) {
        var buffer = new BinaryStream();
        var objects = structure.getObjects();

        // Size
        buffer.putInt(structure.getXLen());
        buffer.putInt(structure.getYLen());
        buffer.putInt(structure.getZLen());

        // Palette content
        var unique = new HashSet<String>();
        for (int x = 0; x < structure.getXLen(); x++) {
            for (int z = 0; z < structure.getZLen(); z++) {
                for (int y = 0; y < structure.getYLen(); y++) {
                    var object = objects[x][z][y];

                    if (object == null) continue;
                    unique.add(object.id());
                }
            }
        }
        buffer.putInt(unique.size());
        unique.forEach(objectId -> {
            var runtimeId = objectRegistry.getRuntimeIdFor(objectRegistry.findById(objectId));

            buffer.putInt(runtimeId);
            buffer.putString(objectId);
        });

        for (int x = 0; x < structure.getXLen(); x++) {
            for (int z = 0; z < structure.getZLen(); z++) {
                for (int y = 0; y < structure.getYLen(); y++) {
                    var object = objects[x][z][y];

                    if (object == null) {
                        buffer.putInt(ObjectRegistry.AIR);
                        continue;
                    }

                    buffer.putInt(objectRegistry.getRuntimeIdFor(object));
                }
            }
        }

        buffer.putByte(END);

        return buffer.getBuffer();
    }

    @NotNull @Override
    public Structure deserialize(@NotNull byte[] bytes) {
        var buffer = new BinaryStream(bytes);

        var xLen = buffer.getInt();
        var yLen = buffer.getInt();
        var zLen = buffer.getInt();
        GameObject[][][] objects = new GameObject[xLen][zLen][yLen];

        var paletteSize = buffer.getInt();
        var palette = new HashMap<Integer, GameObject>();
        palette.put(ObjectRegistry.AIR, null);
        for (int i = 0; i < paletteSize; i++) {
            var runtimeId = buffer.getInt();
            var permanentId = buffer.getString();
            GameObject object;

            try {
                object = objectRegistry.findById(permanentId);
            } catch (IllegalArgumentException ex) {
                object = null;
            }

            palette.put(runtimeId, object);
        }

        for (int x = 0; x < xLen; x++) {
            for (int z = 0; z < zLen; z++) {
                for (int y = 0; y < yLen; y++) {
                    var object = palette.getOrDefault(buffer.getInt(), null);

                    objects[x][z][y] = object;
                }
            }
        }

        var structure = new Structure(objects);
        if (buffer.getByte() == END) return structure;

        // ...
        return structure;
    }
}
