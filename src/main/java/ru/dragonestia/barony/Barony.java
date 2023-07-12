package ru.dragonestia.barony;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import ru.dragonestia.barony.command.EditorCommand;
import ru.dragonestia.barony.di.BaronyGuiceModule;
import ru.dragonestia.barony.level.LevelLoader;
import ru.dragonestia.barony.level.generator.StructureViewGenerator;
import ru.dragonestia.barony.level.generator.VoidGenerator;
import ru.dragonestia.barony.level.grid.GridPlacer;
import ru.dragonestia.barony.level.provider.InMemoryLevelProvider;
import ru.dragonestia.barony.object.registry.ObjectRegistry;
import ru.dragonestia.barony.structure.WorldStructure;
import ru.dragonestia.barony.structure.registry.StructureRegistry;

public class Barony extends PluginBase {

    private Injector injector;

    @Override
    public void onLoad() {
        getDataFolder().mkdir();

        injector = Guice.createInjector(
                new BaronyGuiceModule(getLogger(), getDataFolder().toPath()));

        LevelProviderManager.addProvider(getServer(), InMemoryLevelProvider.class);
        Generator.addGenerator(VoidGenerator.class, "void");

        initObjectRegistry();
        initStructureRegistry();
    }

    @Override
    public void onEnable() {
        getServer().getCommandMap().registerAll("MCBarony", List.of(injector.getInstance(EditorCommand.class)));

        level = LevelLoader.createInMemoryLevel(
                new StructureViewGenerator(10, 5, 10, GridPlacer.Mode.EDITOR, new WorldStructure(10, 5, 10)));
    }

    private void initObjectRegistry() {
        var registry = injector.getInstance(ObjectRegistry.class);

        ObjectRegistry.registerDefault(registry);
        registry.loadAll();
    }

    private void initStructureRegistry() {
        var registry = injector.getInstance(StructureRegistry.class);

        registry.loadAll();
    }
}
