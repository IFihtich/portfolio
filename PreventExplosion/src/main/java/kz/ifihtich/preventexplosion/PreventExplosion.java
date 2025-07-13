package kz.ifihtich.preventexplosion;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class PreventExplosion extends JavaPlugin {

    private static PreventExplosion instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new ExplEvent(), this);
        if (Bukkit.getPluginManager().getPlugin("NBTAPI") != null && Bukkit.getPluginManager().getPlugin("NBTAPI").isEnabled()) {
            saveDefaultConfig();
            getServer().getPluginManager().registerEvents(new ExplEvent(), this);
        } else {
            this.getLogger().severe(Utils.color("Для работы плагина нужен NBTAPI"));
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public static PreventExplosion getInstance () {
        return instance;
    }
}

