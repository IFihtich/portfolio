package kz.ifihtich.weatherchance;

import org.bukkit.plugin.java.JavaPlugin;

public final class WeatherChance extends JavaPlugin {


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Event(), this);

    }

    @Override
    public void onDisable() {
    }

}
