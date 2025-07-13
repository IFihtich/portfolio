package kz.ifihtich.changeLight.Listeners;

import kz.ifihtich.changeLight.ChangeLight;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HandEvent implements Listener {

    private final Set<UUID> glowingPlayers = new HashSet<>();

    private final ChangeLight plugin;

    public HandEvent(ChangeLight plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (item != null && item.getType() == Material.LIGHT){
            if (!glowingPlayers.contains(player.getUniqueId())){
                glowingPlayers.add(player.getUniqueId());
                startParticleLoop(player);

            }
        } else {
            glowingPlayers.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.LIGHT){
            glowingPlayers.add(player.getUniqueId());
            startParticleLoop(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        glowingPlayers.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event){
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL) return;

        Block clicked = event.getClickedBlock();
        if (clicked != null && clicked.getType() == Material.LIGHT){
            clicked.setType(Material.AIR);
            ItemStack lightItem = new ItemStack(Material.LIGHT, 1);

            Location dropLoc = clicked.getLocation().add(0.5, 0.5, 0.5);
            player.getWorld().dropItemNaturally(dropLoc, lightItem);
        }
    }

    private void startParticleLoop(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!glowingPlayers.contains(player.getUniqueId()) || player.getInventory().getItemInMainHand().getType() != Material.LIGHT) {
                    this.cancel();
                    return;
                }
                showLightBlockOutline(player);
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }


    private void showLightBlockOutline(Player player) {
        Location center = player.getLocation();
        int radius = 8;

        Particle.DustOptions red = new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 0, 0), 1.5F);

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    Location loc = center.clone().add(dx, dy, dz);
                    Block block = loc.getBlock();
                    if (block.getType() == Material.LIGHT) {
                        drawBlockOutline(player, block.getLocation(), red);
                    }
                }
            }
        }
    }

    private void drawBlockOutline(Player player, Location base, Particle.DustOptions options) {
        double step = 0.7;

        double[][] edges = {
                {0, 0, 0, 1, 0, 0},
                {1, 0, 0, 1, 0, 1},
                {1, 0, 1, 0, 0, 1},
                {0, 0, 1, 0, 0, 0},
                {0, 1, 0, 1, 1, 0},
                {1, 1, 0, 1, 1, 1},
                {1, 1, 1, 0, 1, 1},
                {0, 1, 1, 0, 1, 0},
                {0, 0, 0, 0, 1, 0},
                {1, 0, 0, 1, 1, 0},
                {1, 0, 1, 1, 1, 1},
                {0, 0, 1, 0, 1, 1}
        };

        for (double[] edge : edges) {
            Location from = base.clone().add(edge[0], edge[1], edge[2]);
            Location to = base.clone().add(edge[3], edge[4], edge[5]);
            Vector direction = to.toVector().subtract(from.toVector()).normalize().multiply(step);
            double distance = from.distance(to);

            for (double d = 0; d <= distance; d += step) {
                Location point = from.clone().add(direction.clone().multiply(d));
                player.spawnParticle(Particle.DUST, point, 1, 0, 0, 0, 0, options);
            }
        }
    }
}
