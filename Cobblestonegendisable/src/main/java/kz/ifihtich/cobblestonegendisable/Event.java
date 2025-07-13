package kz.ifihtich.cobblestonegendisable;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

public class Event implements Listener {

    @EventHandler
    public void generator(BlockFormEvent event){
        if (event.getNewState().getType() == Material.COBBLESTONE){
            event.setCancelled(true);

        }
    }
}
