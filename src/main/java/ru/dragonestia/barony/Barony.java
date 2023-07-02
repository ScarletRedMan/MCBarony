package ru.dragonestia.barony;

import cn.nukkit.plugin.PluginBase;
import com.google.inject.Guice;
import com.google.inject.Injector;
import ru.dragonestia.barony.di.BaronyGuiceModule;

public class Barony extends PluginBase {

    private Injector injector;

    @Override
    public void onLoad() {
        injector = Guice.createInjector(new BaronyGuiceModule(getLogger(), getDataFolder().toPath()));
    }
}
