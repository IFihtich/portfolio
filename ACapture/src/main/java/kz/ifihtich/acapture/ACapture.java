package kz.ifihtich.acapture;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.stream.Collectors;

public final class ACapture extends JavaPlugin {
    private MainZone mainZone;
    private static ACapture instance;
    private static ScoreManager scoreManager;
    private ZoneListener zoneListener;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        scoreManager = new ScoreManager();
        this.mainZone = new MainZone();
        mainZone.loadFromConfig(this);

        getCommand("acapture").setExecutor(new ReloadCommand(this));
        getCommand("acapture").setTabCompleter(new ReloadCommand(this));

        zoneListener = new ZoneListener(this);
        getServer().getPluginManager().registerEvents(zoneListener, this);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (MainZone.Zone zone : mainZone.getZones()) {
                    Set<Player> inside = Bukkit.getOnlinePlayers().stream()
                            .filter(zone::isInside)
                            .collect(Collectors.toSet());
                    zone.tickCapture(inside);
                }
            }
        }.runTaskTimer(this, 0L, 1L);

        new BukkitRunnable() {
            @Override
            public void run() {
                mainZone.showZoneOutline();
            }
        }.runTaskTimer(this, 0L, 5L);

        getLogger().info("Плагин запущен!");


    }
    @Override
    public void onDisable() {
        if (mainZone != null) {
            mainZone.resetCaptures();
            for (MainZone.Zone zone : mainZone.getZones()) {
                zone.removeHologram();
            }
        }
        getLogger().info("Плагин отключен.");
    }
    public static ACapture getInstance(){
        return instance;
    }
    public static ScoreManager getScoreManager() {
        return scoreManager;
    }
    public MainZone getMainZone() {
        return mainZone;
    }

    public void onReload(ACapture plugin) {
        if (mainZone != null) {
            for (MainZone.Zone zone : mainZone.getZones()) {
                zone.shutdown();
            }
            mainZone.getZones().clear();
        }

        if (zoneListener != null) {
            HandlerList.unregisterAll(zoneListener);
        }


        getScoreManager().reset();

        plugin.reloadConfig();

        plugin.mainZone = new MainZone();
        plugin.mainZone.loadFromConfig(plugin);


        zoneListener = new ZoneListener(plugin);
        plugin.getServer().getPluginManager().registerEvents(zoneListener, plugin);
    }


}
