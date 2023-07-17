package ru.dragonestia.barony.structure.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.level.generator.StructureViewGenerator;
import ru.dragonestia.barony.level.grid.GridPos;
import ru.dragonestia.barony.object.editor.EditorBorderObj;
import ru.dragonestia.barony.structure.EditorItems;
import ru.dragonestia.barony.structure.WorldStructure;

public class EditorEventListener implements Listener {

    @EventHandler
    void onChangeLevel(EntityLevelChangeEvent event) {
        if (StructureViewGenerator.isGame(event.getTarget()) && StructureViewGenerator.isGame(event.getOrigin())) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            player.getInventory().clearAll();
            if (StructureViewGenerator.isEditor(event.getTarget())) {
                var inv = player.getInventory();

                inv.addItem(EditorItems.createPlacerItem());
            }
        }
    }

    @EventHandler
    void onBreak(BlockBreakEvent event) {
        var player = event.getPlayer();
        var level = player.getLevel();

        if (!StructureViewGenerator.isEditor(level)) return;
        var world = WorldStructure.of(level);
        var handItem = player.getInventory().getItemInHand();

        if (EditorItems.isEditorItem(handItem)) {
            if (!isInsideWorld(world, event.getBlock())) {
                event.setCancelled();
                return;
            }
        }

        GridPos gridPos;
        try {
            gridPos = world.gridPosOf(event.getBlock());

            if (gridPos.x() >= world.getXLen() || gridPos.y() >= world.getYLen() || gridPos.y() >= world.getZLen()) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            return;
        }

        if (EditorItems.isPlacerItem(handItem)) {
            playPlacerSound(player, event.getBlock());
            world.update(gridPos, null);
            return;
        }

        // ...
    }

    @EventHandler
    void onPlace(BlockPlaceEvent event) {
        var player = event.getPlayer();
        var level = player.getLevel();

        if (!StructureViewGenerator.isEditor(level)) return;
        var world = WorldStructure.of(level);
        var handItem = player.getInventory().getItemInHand();

        if (EditorItems.isEditorItem(handItem)) {
            if (!isInsideWorld(world, event.getBlock())) {
                event.setCancelled();
                return;
            }
        }

        GridPos gridPos;
        try {
            gridPos = world.gridPosOf(event.getBlock());

            if (gridPos.x() >= world.getXLen() || gridPos.y() >= world.getYLen() || gridPos.y() >= world.getZLen()) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException ex) {
            return;
        }

        if (EditorItems.isPlacerItem(handItem)) {
            world.update(gridPos, new EditorBorderObj()); // TODO: selecting objects
            playPlacerSound(player, event.getBlock());
            event.setCancelled();
            return;
        }

        // ...
    }

    @EventHandler
    void onDrop(PlayerDropItemEvent event) {
        var player = event.getPlayer();
        var level = player.getLevel();

        if (!StructureViewGenerator.isEditor(level)) return;

        if (EditorItems.isEditorItem(player.getInventory().getItemInHand())) {
            event.setCancelled();
        }
    }

    private boolean isInsideWorld(@NotNull WorldStructure world, @NotNull Vector3 pos) {
        var start = world.getOffset().add(0, 1, 0);
        var end = world.getOffset()
                .add(3 * world.getXLen(), 3 * world.getYLen(), 3 * world.getZLen())
                .subtract(1, 0, 1);

        return (start.x <= pos.x && pos.x <= end.x)
                && (start.y <= pos.y && pos.y <= end.y)
                && (start.z <= pos.z && pos.z <= end.z);
    }

    private void playPlacerSound(@NotNull Player player, @NotNull Vector3 soundSource) {
        player.getLevel().addSound(soundSource, Sound.DIG_BONE_BLOCK);
    }
}
