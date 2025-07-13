package kz.ifihtich.endermannopickup;

import org.bukkit.plugin.java.JavaPlugin;

public final class EndermanNoPickup extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Event(), this);
    }

    @Override
    public void onDisable() {
    }
}
