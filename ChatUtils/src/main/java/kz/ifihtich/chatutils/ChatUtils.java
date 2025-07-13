package kz.ifihtich.chatutils;

import kz.ifihtich.chatutils.Commands.Discord;
import kz.ifihtich.chatutils.Commands.Top;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatUtils extends JavaPlugin {

    private static ChatUtils instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new EnterEvent(),this);
        getCommand("discord").setExecutor(new Discord());
        getCommand("top").setTabCompleter(new Top());
        getCommand("top").setExecutor(new Top());
    }

    @Override
    public void onDisable() {
    }

    public static ChatUtils getInstance(){
        return instance;
    }
}
