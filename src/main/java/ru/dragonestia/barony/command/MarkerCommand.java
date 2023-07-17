package ru.dragonestia.barony.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import ru.dragonestia.barony.object.GameObject;
import ru.dragonestia.barony.object.procedural.DirtFloorObj;
import ru.dragonestia.barony.object.registry.ObjectRegistry;

public class MarkerCommand extends Command {

    private final ObjectRegistry objectRegistry;
    private final ConcurrentHashMap<String, GameObject> players = new ConcurrentHashMap<>();

    @Inject
    public MarkerCommand(@NotNull ObjectRegistry objectRegistry) {
        super("marker", "", "/marker <Id объекта>", new String[0]);

        this.objectRegistry = objectRegistry;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.sendMessage(getUsage());
                return true;
            }

            var objectId = args[0].toLowerCase();
            try {
                var object = objectRegistry.findById(objectId);

                setMarker(player, object);

                player.sendMessage("Объект успешно выбран!");
            } catch (IllegalArgumentException ex) {
                player.sendMessage("Введен неверный Id объекта!");
            }
            return true;
        }

        sender.sendMessage("Данную команду можно использовать только в игре!");
        return false;
    }

    public void updateParameters() {
        getCommandParameters().clear();

        var list = objectRegistry.getObjectIds();
        var arr = new String[list.size()];
        int i = 0;
        for (var objectId : list) {
            arr[i++] = objectId;
        }

        getCommandParameters()
                .put("default", new CommandParameter[] {CommandParameter.newEnum("ObjectId", false, arr)});
        Server.getInstance().getOnlinePlayers().forEach((key, value) -> value.sendCommandData());
    }

    public void setMarker(@NotNull Player player, @NotNull GameObject gameObject) {
        players.put(player.getName(), gameObject);
    }

    public @NotNull GameObject get(@NotNull Player player) {
        return players.get(player.getName());
    }

    public void join(@NotNull Player player) {
        players.put(player.getName(), new DirtFloorObj());
    }

    public void quit(@NotNull Player player) {
        players.remove(player.getName());
    }
}
