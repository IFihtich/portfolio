package kz.ifihtich.chatutils;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class EnterEvent implements Listener {

    FileConfiguration config = ChatUtils.getInstance().getConfig();

    @EventHandler

    public void onPlayerEnter(PlayerJoinEvent event){
        event.setJoinMessage(null);
        List<String> message = config.getStringList("messages");
        for (String msg : message){
            String msgp = msg.replace("%player%", event.getPlayer().getName());
            event.getPlayer().sendMessage(Utils.color(msgp));
        }
    }
}
