package kz.ifihtich.discountoff;

import org.bukkit.plugin.java.JavaPlugin;

public final class Discountoff extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Event(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
