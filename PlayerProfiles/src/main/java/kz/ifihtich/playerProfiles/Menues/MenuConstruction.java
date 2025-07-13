package kz.ifihtich.playerProfiles.Menues;

import kz.ifihtich.playerProfiles.Expansions.AdvancementChecker;
import kz.ifihtich.playerProfiles.ChatUtils.ChatUtil;
import kz.ifihtich.playerProfiles.Expansions.Skull;
import kz.ifihtich.playerProfiles.PlayerProfiles;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class MenuConstruction {
    public static Inventory open(String target, String MenuName){
        String menuPath = MenuName;
        int size = getConfig().getInt(menuPath + ".size");
        Player targetPlayer = Bukkit.getPlayer(target);
        String name = getConfig().getString(menuPath + ".name").replace("%player%", target);
        name = ChatUtil.formatItemText(name);
        MenuHolder holder = new MenuHolder(menuPath, size, name);
        Inventory inventory = holder.getInventory();
        ConfigurationSection slotSection = getConfig().getConfigurationSection(menuPath + ".slots");
        if (slotSection != null){
            for (String key : slotSection.getKeys(false)){
                String path = menuPath + ".slots." + key;
                Material material = Material.getMaterial(getConfig().getString(path + ".material", "STONE").toUpperCase());
                ItemStack itemStack;
                if (material == null) continue;
                if (material == Material.PLAYER_HEAD){

                    String value = getConfig().getString(path + ".value");
                    if (value != null && !value.isEmpty()){
                        itemStack = Skull.getHeadFrom64(value);
                    } else{
                        itemStack = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                        String skin = getConfig().getString(path + ".skin");
                        if (skin != null){
                            skin = skin.replace("%player%", target);
                            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(skin));
                            itemStack.setItemMeta(skullMeta);
                        }
                    }
                }
                else{
                    itemStack = new ItemStack(material);
                }
                ItemMeta meta = itemStack.getItemMeta();
                if (meta != null){
                    String itemName = getConfig().getString(path + ".name", " ");
                    meta.setDisplayName(ChatUtil.formatItemText(format(targetPlayer, itemName)));

                    List<String> Lore = getConfig().getStringList(path + ".lore");
                    List<String> itemLore = new ArrayList<>();

                    for (String line : Lore){
                        itemLore.add(ChatUtil.formatItemText(format(targetPlayer, line)));
                    }
                    if (getConfig().contains(path + ".action")){
                        String type = getConfig().getString(path + ".action.type", "");
                        if (type.equalsIgnoreCase("achievement")){
                            String ach = getConfig().getString(path + ".action.achievement");
                            OfflinePlayer player = Bukkit.getOfflinePlayer(target);
                            String done = ChatUtil.formatItemText(getConfig().getString("messages.done"));
                            String not_completed = ChatUtil.formatItemText(getConfig().getString("messages.not_completed"));
                            if (AdvancementChecker.hasCompleted((Player) player, ach)){
                                itemLore.add(done);
                            }
                            else {
                                itemLore.add(not_completed);
                            }
                        }
                    }
                    List<String> flags = getConfig().getStringList(path + ".itemflags");
                    for (String flag : flags){
                        try {
                            ItemFlag itemFlag = ItemFlag.valueOf(flag.toUpperCase());
                            meta.addItemFlags(itemFlag);
                        } catch (IllegalArgumentException e){
                            Bukkit.getLogger().warning("Неверный флаг");
                        }
                    }
                    meta.setLore(itemLore);

                    if (getConfig().getBoolean(path + ".glow", false)){
                        meta.addEnchant(Enchantment.MENDING, 1, true);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    }
                    itemStack.setItemMeta(meta);
                }
                Object object = getConfig().get(path + ".slot");
                if (object instanceof List<?>){
                    List<?> slotList = (List<?>) object;
                    for (Object slotValue : slotList){
                        int slot = (Integer) slotValue;
                        if (slot >= 0 && slot < inventory.getSize()){
                            inventory.setItem(slot, itemStack);
                        }
                    }
                }
                else{
                    int slot = getConfig().getInt(path + ".slot", -1);
                    if (slot >= 0 && slot < inventory.getSize()){
                        inventory.setItem(slot, itemStack);
                    }
                }
            }
        }
        return inventory;
    }

    private static String format(Player player, String text) {
        if (text == null) return "";

        String name = player != null ? player.getName() : "";
        int likes = PlayerProfiles.getInstance().getLikesManager().getLikes(name);
        int dislikes = PlayerProfiles.getInstance().getLikesManager().getDislikes(name);
        int rating = PlayerProfiles.getInstance().getLikesManager().getRating(name);

        text = text
                .replace("%player%", name)
                .replace("%like%", String.valueOf(likes))
                .replace("%dislike%", String.valueOf(dislikes))
                .replace("%rating%", String.valueOf(rating));

        if (player != null && Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }
    private static FileConfiguration getConfig() {
        return PlayerProfiles.getInstance().getConfig();
    }
}
