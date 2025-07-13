package kz.ifihtich.endermannopickup;

import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class Event implements Listener {

    @EventHandler
    public void onPickUp(EntityChangeBlockEvent event){
        if (event.getEntity() instanceof Enderman){
            if (event.getEntity().getWorld().getEnvironment() == World.Environment.NORMAL){
                if (event.getTo().isAir()){
                    event.setCancelled(true);
                }
            }
        }
    }
}
