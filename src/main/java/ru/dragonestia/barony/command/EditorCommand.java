package ru.dragonestia.barony.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.registry.ObjectRegistry;
import ru.dragonestia.barony.structure.EditorService;

public class EditorCommand extends Command {

    private final ObjectRegistry objectRegistry;
    private final EditorService editorService;

    @Inject
    public EditorCommand(ObjectRegistry objectRegistry, EditorService editorService) {
        super("editor", "Редактор структур", "/editor", new String[0]);

        this.objectRegistry = objectRegistry;
        this.editorService = editorService;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.sendMessage("/editor create <Id структуры> <xLen> <yLen> <zLen>");
                player.sendMessage("/editor check <Id структуры>");
                player.sendMessage("/editor edit <Id структуры>");
                player.sendMessage("/editor save");
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

                default -> player.sendMessage(TextFormat.RED + "Нет такой суб-команды!");
            }
            return true;
        }

        sender.sendMessage("Команду можно использовать только в игре");
        return false;
    }

    private void create(@NotNull Player player, @NotNull String identifier, int xLen, int yLen, int zLen) {}

    private void check(@NotNull Player player, @NotNull String identifier) {}

    private void edit(@NotNull Player player, @NotNull String identifier) {}

    private void save(@NotNull Player player) {}
}
