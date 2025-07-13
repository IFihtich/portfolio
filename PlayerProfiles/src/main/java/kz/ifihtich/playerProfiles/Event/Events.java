package kz.ifihtich.playerProfiles.Event;

import kz.ifihtich.playerProfiles.ChatUtils.ChatUtil;
import kz.ifihtich.playerProfiles.Expansions.LikesManager;
import kz.ifihtich.playerProfiles.Menues.MenuConstruction;
import kz.ifihtich.playerProfiles.Menues.MenuHolder;
import kz.ifihtich.playerProfiles.PlayerProfiles;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class Events implements Listener {

    LikesManager likesManager = PlayerProfiles.getInstance().getLikesManager();

    @EventHandler
    public void onClickPlayer(PlayerInteractEntityEvent event){
        if (event.getRightClicked() instanceof Player){
            Player player = (Player) event.getRightClicked();
            Player openPlayer = event.getPlayer();
            if (CitizensAPI.getNPCRegistry().isNPC(player)) return;

            if (openPlayer.isSneaking()){
                openPlayer.setMetadata("profile_target", new FixedMetadataValue(PlayerProfiles.getInstance(), player.getName()));
                openPlayer.openInventory(MenuConstruction.open(player.getName(),"MainProfileMenu"));
            }
        }
    }
    @EventHandler
    public void onClickInventory(InventoryClickEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof MenuHolder)) return;

        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        MenuHolder menuHolder = (MenuHolder) holder;
        String menuPath = menuHolder.getMenuName();
        ConfigurationSection slots = getConfig().getConfigurationSection(menuPath + ".slots");
        if (slots == null) return;
        for (String key : slots.getKeys(false)){
            String path = menuPath + ".slots." + key;
            int slot = getConfig().getInt(path + ".slot", -1);
            if (slot == event.getRawSlot()){
                if (getConfig().contains(path + ".action")){
                    String type = getConfig().getString(path + ".action.type", "");
                    if (type.equalsIgnoreCase("open")) {
                        String MenuName = getConfig().getString(path + ".action.menu");
                        String targetName = getProfileTarget(player);
                        if (targetName == null) targetName = player.getName();

                        Player targetPlayer = Bukkit.getPlayerExact(targetName);
                        if (targetPlayer == null) {
                            String offline = getConfig().getString("messages.offline").replace("%player%", targetName);
                            ChatUtil.sendMessage(player, offline);
                            player.closeInventory();
                            return;
                        }

                        player.openInventory(MenuConstruction.open(targetName, MenuName));
                    }
                    if (getConfig().contains(path + ".action.sound")){
                        String sound = PlayerProfiles.getInstance().getConfig().getString(path + ".action.sound");
                        try {
                            player.playSound(player.getLocation(), sound, org.bukkit.SoundCategory.MASTER, 1f, 1f);
                        } catch (Exception e) {
                            Bukkit.getLogger().warning("Неверное имя звука: " + sound);
                        }
                    }
                    if (type.equalsIgnoreCase("like") || type.equalsIgnoreCase("dislike")){
                        String targetName = getProfileTarget(player);
                        if (targetName == null) return;
                        if (likesManager.hasVoted(player.getName(), targetName)){
                            String voted = getConfig().getString("messages.already").replace("%player%", targetName);
                            ChatUtil.sendMessage(player, voted);
                            return;
                        }
                        likesManager.vote(player.getName(), targetName, type);
                        String setLike = getConfig().getString("messages.setLike").replace("%player%", targetName);
                        String setDislike = getConfig().getString("messages.setDislike").replace("%player%", targetName);
                        if (type.equalsIgnoreCase("like")){
                            ChatUtil.sendMessage(player, setLike);
                            player.closeInventory();
                        }
                        else {
                            ChatUtil.sendMessage(player, setDislike);
                            player.closeInventory();
                        }
                    }
                    if (type.equalsIgnoreCase("cancel_vote")){
                        String targetName = getProfileTarget(player);
                        if (targetName == null) return;
                        if (!likesManager.hasVoted(player.getName(), targetName)){
                            ChatUtil.sendMessage(player, getConfig().getString("messages.noVote").replace("%player%", targetName));
                            return;
                        }
                        likesManager.cancelVote(player.getName(), targetName);
                        ChatUtil.sendMessage(player, getConfig().getString("messages.voteCancelled").replace("%player%", targetName));
                        player.closeInventory();
                    }
                }
            }
        }
    }
    private String getProfileTarget(Player player){
        if (player.hasMetadata("profile_target")){
            List<MetadataValue> metadataValues = player.getMetadata("profile_target");
            for (MetadataValue value : metadataValues){
                if (value.getOwningPlugin() == PlayerProfiles.getInstance()){
                    return value.asString();
                }
            }
        }
        return null;
    }
    private static FileConfiguration getConfig(){
        return PlayerProfiles.getInstance().getConfig();
    }
}
