package kz.ifihtich.drophead;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Event implements Listener {

    @EventHandler
    public void Drophead(PlayerDeathEvent event){
        if (event.getEntity().getKiller() != null){
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headmeta = (SkullMeta) head.getItemMeta();
            headmeta.setOwningPlayer(event.getEntity());
            headmeta.setDisplayName(Utils.color("&bГолова " + "&e" + event.getEntity().getName()));
            head.setItemMeta(headmeta);
            event.getDrops().add(head);
        }
    }
}
