package kz.ifihtich.custominvulnerable;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;

public class Event implements Listener {

    FileConfiguration config = CustomInvulnerable.getInstance().getConfig();

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        player.setInvulnerable(true);
        Set<UUID> invulPlayers = CustomInvulnerable.getInstance().getInvulnerablePlayers();
        invulPlayers.add(player.getUniqueId());
        int time = config.getInt("seconds") * 20;
        new BukkitRunnable(){

            @Override
            public void run() {
                player.setInvulnerable(false);
                invulPlayers.remove(player.getUniqueId());
            }
        }.runTaskLater(CustomInvulnerable.getInstance(), time);
    }
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event){
        if (event.getDamager() instanceof Player){
            Player attacker = (Player) event.getDamager();
            if (CustomInvulnerable.getInstance().getInvulnerablePlayers().contains(attacker.getUniqueId())){
                event.setCancelled(true);
            }
        }
    }

}
