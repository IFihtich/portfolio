package kz.ifihtich.preventexplosion;

import de.tr7zw.nbtapi.NBTEntity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExplEvent implements Listener {

    FileConfiguration config = PreventExplosion.getInstance().getConfig();

    @EventHandler
    public void Explosion(EntityExplodeEvent event){
        if (event.getEntityType() == EntityType.END_CRYSTAL){
            EnderCrystal crystal = (EnderCrystal) event.getEntity();
            NBTEntity nbtEntity = new NBTEntity(crystal);
            if (nbtEntity.getBoolean("ShowBottom") && config.getBoolean("Prevent.ENDER_CRYSTAL.natural")){
                event.blockList().clear();
            }
            else if (!nbtEntity.getBoolean("ShowBottom") && config.getBoolean("Prevent.ENDER_CRYSTAL.default")){
                event.blockList().clear();
            }
        }
        else if (event.getEntityType() == EntityType.TNT && config.getBoolean("Prevent.PRIMED_TNT")){
            event.blockList().clear();
        }
        else if (event.getEntityType() == EntityType.TNT_MINECART && config.getBoolean("Prevent.TNT_MINECART")){
            event.blockList().clear();
        }
        else if (event.getEntityType() == EntityType.WITHER && config.getBoolean("Prevent.WITHER")){
            event.blockList().clear();
        }
        else if (event.getEntityType() == EntityType.CREEPER && config.getBoolean("Prevent.CREEPER")){
            event.blockList().clear();
        }
        else if (event.getEntityType() == EntityType.WITHER_SKULL && config.getBoolean("Prevent.WITHER_SKULL")){
            event.blockList().clear();
        }
    }

    @EventHandler
    public void AnchorExplosion(PlayerInteractEvent event){
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() == Material.RESPAWN_ANCHOR &&
        event.getClickedBlock().getWorld().getEnvironment() == World.Environment.NORMAL){
            RespawnAnchor anchor = (RespawnAnchor) event.getClickedBlock().getBlockData();
            if (anchor.getCharges() > 0 && event.getAction().isRightClick()){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void CrystalExplosion(EntityDamageByEntityEvent event){
        if (event.getDamager().getType() == EntityType.END_CRYSTAL){
            if (event.getDamage() > 8){
                event.setDamage(8);
            }
        }
    }
}

