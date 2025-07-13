package kz.ifihtich.cobblestonegendisable;

import org.bukkit.plugin.java.JavaPlugin;

public final class Cobblestonegendisable extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Event(), this);

    }

    @Override
    public void onDisable() {
    }
}
