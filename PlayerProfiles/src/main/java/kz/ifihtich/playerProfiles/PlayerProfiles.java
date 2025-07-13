package kz.ifihtich.playerProfiles;

import kz.ifihtich.playerProfiles.Commands.Reload;
import kz.ifihtich.playerProfiles.Event.Events;
import kz.ifihtich.playerProfiles.Expansions.Expansion;
import kz.ifihtich.playerProfiles.Expansions.LikesManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PlayerProfiles extends JavaPlugin {

    private static PlayerProfiles instance;

    private FileConfiguration likesConfig;

    private File likesFile;

    private LikesManager likesManager;



    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.likesManager = new LikesManager();
        getServer().getPluginManager().registerEvents(new Events(), this);
        getCommand("playerprofiles").setExecutor(new Reload());
        getCommand("playerprofiles").setTabCompleter(new Reload());
        reloadLikesConfig();
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")){
            new Expansion().register();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static PlayerProfiles getInstance(){
        return instance;
    }

    public void reloadLikesConfig(){
        likesFile = new File(getDataFolder(), "likes.yml");
        if (!likesFile.exists()){
            likesFile.getParentFile().mkdirs();
            saveResource("likes.yml", false);
        }
        likesConfig = YamlConfiguration.loadConfiguration(likesFile);
    }
    public LikesManager getLikesManager(){
        return likesManager;
    }
}
