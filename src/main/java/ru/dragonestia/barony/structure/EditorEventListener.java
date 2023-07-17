package ru.dragonestia.barony.structure;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import ru.dragonestia.barony.level.generator.StructureViewGenerator;
import ru.dragonestia.barony.level.grid.GridPos;
import ru.dragonestia.barony.object.editor.EditorBorderObj;

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
}
