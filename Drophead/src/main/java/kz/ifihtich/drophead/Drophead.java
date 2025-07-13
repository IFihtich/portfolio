package kz.ifihtich.drophead;

import org.bukkit.plugin.java.JavaPlugin;

public final class Drophead extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Event(), this);
    }

    @Override
    public void onDisable() {
    }
}
