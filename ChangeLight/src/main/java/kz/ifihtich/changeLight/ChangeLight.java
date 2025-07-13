package kz.ifihtich.changeLight;

import kz.ifihtich.changeLight.Listeners.HandEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChangeLight extends JavaPlugin {

    private static ChangeLight instance;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new HandEvent(this), this);

    }

    @Override
    public void onDisable() {

    }
    public static ChangeLight getInstance(){
        return instance;
    }
}
