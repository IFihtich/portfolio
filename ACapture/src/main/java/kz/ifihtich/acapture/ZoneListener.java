package kz.ifihtich.acapture;

import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;

public class ZoneListener implements Listener {
    private final ACapture plugin;
    private final Set<Player> playersInZone = new HashSet<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        MainZone.Zone zone = plugin.getMainZone().getZonePlayerIsIn(player);

        if (zone != null && !playersInZone.contains(player)){
            playersInZone.add(player);
            player.sendMessage("Вы вошли в зону");
        } else if (zone == null && playersInZone.contains(player)){
            playersInZone.remove(player);
            player.sendMessage("Вы вышли из зоны");
        }
    }
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        Item item = event.getItem();

        if (!item.hasMetadata("zone_points") || !item.hasMetadata("zone_owner_team")) return;

        String teamName = item.getMetadata("zone_owner_team").get(0).asString();
        int points = item.getMetadata("zone_points").get(0).asInt();

        Team team = player.getScoreboard().getEntryTeam(player.getName());

        if (team == null || !team.getName().equals(teamName)) {
            event.setCancelled(true);
            return;
        }

        MainZone.Zone zone = plugin.getMainZone().getZonePlayerIsIn(player);
        if (zone == null || !zone.isInside(item.getLocation())) {
            event.setCancelled(true);
            return;
        }

        ACapture.getScoreManager().addPoints(teamName, points);
        player.sendMessage(ChatColor.GREEN + "Вы принесли " + ChatColor.YELLOW + points + " оч." + ChatColor.GREEN + " своей команде" );
        item.remove();
        event.setCancelled(true);
    }
    public ZoneListener(ACapture plugin) {
        this.plugin = plugin;
    }
}
