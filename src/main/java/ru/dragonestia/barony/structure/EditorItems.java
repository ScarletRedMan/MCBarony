package ru.dragonestia.barony.structure;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.jetbrains.annotations.NotNull;

public interface EditorItems {

    String TAG_FIELD = "editor_item";
    String TAG_VALUE_PLACER = "marker";

    static boolean isEditorItem(@NotNull Item item) {
        var tag = item.getOrCreateNamedTag();

        return tag.exist(TAG_FIELD);
    }

    static boolean isPlacerItem(@NotNull Item item) {
        var tag = item.getOrCreateNamedTag();

        return tag.exist(TAG_FIELD) && TAG_VALUE_PLACER.equals(tag.getString(TAG_FIELD));
    }

    static @NotNull Item createPlacerItem() {
        var tag = new CompoundTag();
        tag.putString(TAG_FIELD, TAG_VALUE_PLACER);

        var item = Item.getBlock(BlockID.BEDROCK)
                .setCompoundTag(tag)
                .setCustomName(TextFormat.GOLD + "Маркер установки объектов")
                .setLore(
                        TextFormat.WHITE + "Используйте ПКМ для установки объектов",
                        TextFormat.WHITE + "Используйте ЛКМ для удаления объектов",
                        "",
                        TextFormat.GRAY + "Для выбора устанавливаемого объекта",
                        TextFormat.GRAY + "используйте команду /marker");

        item.addEnchantment(Enchantment.getEnchantment(Enchantment.ID_SILK_TOUCH));

        return item;
    }
}
