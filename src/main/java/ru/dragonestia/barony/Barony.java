package ru.dragonestia.barony;

import cn.nukkit.level.format.LevelProviderManager;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import com.google.inject.Guice;
import com.google.inject.Injector;
import ru.dragonestia.barony.di.BaronyGuiceModule;
import ru.dragonestia.barony.level.provider.InMemoryLevelProvider;
import ru.dragonestia.barony.level.generator.VoidGenerator;

public class Barony extends PluginBase {

    private Injector injector;

    @Override
    public void onLoad() {
        injector = Guice.createInjector(
                new BaronyGuiceModule(getLogger(), getDataFolder().toPath()));

        LevelProviderManager.addProvider(getServer(), InMemoryLevelProvider.class);
        Generator.addGenerator(VoidGenerator.class, "void");
    }
}
