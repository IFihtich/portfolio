package kz.ifihtich.custominvulnerable;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CustomInvulnerable extends JavaPlugin {

    private static CustomInvulnerable instance;
    private final Set<UUID> invulnerablePlayers = new HashSet<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new Event(), this);

    }

    @Override
    public void onDisable() {
    }

    public static CustomInvulnerable getInstance(){
        return instance;
    }
    public Set<UUID> getInvulnerablePlayers() {
        return invulnerablePlayers;
    }
}
