package ru.dragonestia.barony.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.GameObject;
import ru.dragonestia.barony.structure.EditorService;
import ru.dragonestia.barony.structure.Structure;
import ru.dragonestia.barony.structure.WorldStructure;
import ru.dragonestia.barony.structure.registry.StructureRegistry;

public class EditorCommand extends Command {

    private final StructureRegistry structureRegistry;
    private final EditorService editorService;

    @Inject
    public EditorCommand(StructureRegistry structureRegistry, EditorService editorService) {
        super("editor", "Редактор структур", "/editor", new String[0]);

        this.structureRegistry = structureRegistry;
        this.editorService = editorService;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.sendMessage("/editor create <Id структуры> <xLen> <yLen> <zLen> - Создать новую структуру");
                player.sendMessage("/editor check <Id структуры> - Проверить существование структуры");
                player.sendMessage("/editor edit <Id структуры> - Редактировать структуру");
                player.sendMessage("/editor save - Сохранить текущую структуру");
                player.sendMessage("/editor close - Выгрузить текущую структуру из памяти");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "create" -> {
                    if (args.length < 5) {
                        player.sendMessage("Использование: /editor create <Id структуры> <xLen> <yLen> <zLen>");
                        return false;
                    }

                    String identifier = args[1];
                    int xLen;
                    int yLen;
                    int zLen;
                    try {
                        xLen = Integer.parseInt(args[2]);
                        yLen = Integer.parseInt(args[3]);
                        zLen = Integer.parseInt(args[4]);

                        if (xLen < 1 || yLen < 1 || zLen < 1) throw new NumberFormatException();
                    } catch (NumberFormatException ex) {
                        player.sendMessage("Введен неверный размер структуры");
                        return false;
                    }

                    create(player, identifier, xLen, yLen, zLen);
                }

                case "check" -> {
                    if (args.length < 2) {
                        player.sendMessage("Использование: /editor check <Id структуры>");
                        return false;
                    }

                    String identifier = args[1];

                    check(player, identifier);
                }

                case "edit" -> {
                    if (args.length < 2) {
                        player.sendMessage("Использование: /editor edit <Id структуры>");
                        return false;
                    }

                    String identifier = args[1];

                    edit(player, identifier);
                }

                case "save" -> save(player);

                case "close" -> close(player);

                default -> player.sendMessage("Нет такой суб-команды!");
            }
            return true;
        }

        sender.sendMessage("Команду можно использовать только в игре");
        return false;
    }

    private void create(@NotNull Player player, @NotNull String identifier, int xLen, int yLen, int zLen) {
        if (editorService.checkStructure(identifier)) {
            player.sendMessage("Структура '" + identifier + "' уже существует!");
            return;
        }

        if (editorService.isEditingRightNow(identifier)) {
            player.sendMessage("Структуру '" + identifier + "' уже создали и сейчас редактируют");
            return;
        }

        var level = editorService.createEditorLevel(identifier, new Structure(new GameObject[xLen][zLen][yLen]));
        player.teleport(level.getSafeSpawn());
        player.setGamemode(Player.CREATIVE);
    }

    private void check(@NotNull Player player, @NotNull String identifier) {
        String status;
        if (editorService.checkStructure(identifier)) status = "существует";
        else if (editorService.isEditingRightNow(identifier)) status = "создается";
        else status = "не существует";

        player.sendMessage("Структура '" + identifier + "' " + status);
    }

    private void edit(@NotNull Player player, @NotNull String identifier) {
        Structure structure;
        try {
            structure = structureRegistry.findById(identifier);
        } catch (IllegalArgumentException ex) {
            player.sendMessage("Структура '" + identifier + "' не найдена!");
            return;
        }

        var level = editorService.createEditorLevel(identifier, structure);

        player.teleport(level.getSafeSpawn());
        player.setGamemode(Player.CREATIVE);
        player.sendMessage("Перемещение в редактор стуктуры '" + identifier + "'");
    }

    private void save(@NotNull Player player) {
        var level = player.getLevel();
        WorldStructure structure;
        try {
            structure = WorldStructure.of(level);
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Player is not at editor");
        }

        structureRegistry.save(structure.getIdentifier(), structure);
    }

    private void close(@NotNull Player player) {
        player.sendMessage("Структура была выгружена из памяти");
        editorService.close(player.getLevel());
    }
}
