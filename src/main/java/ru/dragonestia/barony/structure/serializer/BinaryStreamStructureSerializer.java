package ru.dragonestia.barony.structure.serializer;

import cn.nukkit.utils.BinaryStream;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.GameObject;
import ru.dragonestia.barony.object.registry.ObjectRegistry;
import ru.dragonestia.barony.structure.Structure;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;

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

    private final static byte END = 0x00;

    @NotNull
    @Override
    public byte[] serialize(@NotNull Structure structure) {
        var buffer = new BinaryStream();
        var objects = structure.getObjects();

        //Size
        buffer.putInt(structure.getXLen());
        buffer.putInt(structure.getYLen());
        buffer.putInt(structure.getZLen());

        //Palette content
        var unique = new HashSet<GameObject>();
        for (int x = 0; x < structure.getXLen(); x++) {
            for (int z = 0; z < structure.getZLen(); z++) {
                for (int y = 0; y < structure.getYLen(); y++) {
                    var object = objects[x][z][y];

                    if (object == null) continue;
                    unique.add(object);
                }
            }
        }
        buffer.putInt(unique.size());
        unique.forEach(object -> {
            buffer.putInt(objectRegistry.getRuntimeIdFor(object));
            buffer.putString(object.id());
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

    @NotNull
    @Override
    public Structure deserialize(@NotNull byte[] bytes) {
        return null;
    }
}
