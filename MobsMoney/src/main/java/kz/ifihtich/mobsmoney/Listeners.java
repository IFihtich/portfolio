package kz.ifihtich.mobsmoney;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Listeners implements Listener {

    FileConfiguration config = MobsMoney.getInstance().getConfig();
    Economy economy = MobsMoney.economy;
    boolean soundEnabled = MobsMoney.soundEnabled;

    public void sendColoredMessage(Player player, List<String> list, String killer, Double value) {
        String replace = String.format("%.2f", value).replace(",", ".");

        for (String arg : list) {
            player.sendMessage(Utils.color(arg).replace("%x%", "" + player.getLocation().getBlockX()).replace("%y%", "" + player.getLocation().getBlockY()).replace("%z%", "" + player.getLocation().getBlockZ()).replace("%player%", killer).replace("%money%", "" + replace));
        }

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();
        double playermoney = economy.getBalance(player);
        double moneyperplayer = playermoney * config.getDouble("money.player");
        if (moneyperplayer > 0.0) {
            economy.withdrawPlayer(player, moneyperplayer);

            ItemStack sunflower = new ItemStack(Material.SUNFLOWER);
            ItemMeta meta = sunflower.getItemMeta();

            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            sunflower.setItemMeta(meta);

            Item Coin = player.getWorld().dropItemNaturally(player.getLocation(), sunflower);
            String coinName = String.format("%.2f", moneyperplayer).replace(",", ".");
            String coloredCoinName = "&#FFF000" + coinName;
            Coin.setCustomName(Utils.color(coloredCoinName));
            Coin.setCustomNameVisible(config.getBoolean("drop.visible"));
            Coin.setMetadata("shooter", new FixedMetadataValue(MobsMoney.getInstance(), player.getUniqueId().toString()));

            String var8 = ((MetadataValue) Coin.getMetadata("shooter").get(0)).asString();
                if (killer != null) {
                    Player deathPlayer = MobsMoney.getInstance().getServer().getPlayer(UUID.fromString(var8));
                    sendColoredMessage(player.getPlayer(), config.getStringList("messages.kill-deathplayer"), event.getEntity().getKiller().getName(), moneyperplayer);
                } else{
                    Player deathPlayer = MobsMoney.getInstance().getServer().getPlayer(UUID.fromString(var8));
                    sendColoredMessage(player.getPlayer(), config.getStringList("messages.kill-deathother"), "Unknown source", moneyperplayer);
                }
            }
        }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER && event.getEntity().getKiller() instanceof Player && event.getEntity() instanceof Mob) {
            double randomMoney = 0;
            if (event.getEntity() instanceof Mob) {
                ConfigurationSection section = config.getConfigurationSection("money.kill_mob");
                if (section != null) {
                    EntityType entityType = event.getEntityType();
                    ConfigurationSection entitySection = section.getConfigurationSection(entityType.toString());
                    if (entitySection != null) {
                        double min = entitySection.getDouble("min", config.getDouble("money.mobs.min"));
                        double max = entitySection.getDouble("max", config.getDouble("money.mobs.max"));
                        randomMoney = ThreadLocalRandom.current().nextDouble(min, max);
                    } else {
                        double min = config.getDouble("money.mobs.min");
                        double max = config.getDouble("money.mobs.max");
                        randomMoney = ThreadLocalRandom.current().nextDouble(min, max);
                    }
                } else {
                    double moneyMonsterMinDefault = config.getDouble("money.mobs.min");
                    double moneyMonsterMaxDefault = config.getDouble("money.mobs.max");
                    randomMoney = ThreadLocalRandom.current().nextDouble(moneyMonsterMinDefault, moneyMonsterMaxDefault);
                }
            }

            Item droppedSunflower = event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), new ItemStack(Material.SUNFLOWER));
            Object[] randomInt = new Object[]{(new Random()).nextInt(10000000)};
            String IdName = "red" + String.format("%07d", randomInt);

            ItemStack sunflowerItem = new ItemStack(Material.SUNFLOWER);
            ItemMeta sunflowerMeta = sunflowerItem.getItemMeta();
            if (sunflowerMeta != null) {
                sunflowerMeta.setLore(Collections.singletonList(IdName));

                sunflowerMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                sunflowerMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                sunflowerItem.setItemMeta(sunflowerMeta);
            }

            droppedSunflower.setItemStack(sunflowerItem);
            String coinName = String.format("%.2f", randomMoney).replace(",", ".");
            String coloredCoinName = "&#FFF000" + coinName;
            droppedSunflower.setCustomName(Utils.color(coloredCoinName));
            droppedSunflower.setCustomNameVisible(config.getBoolean("drop.visible"));
            droppedSunflower.setInvulnerable(true);
        }

    }


    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) throws NumberFormatException {
        Item pickupItem = event.getItem();

        if (pickupItem.getItemStack().getType() == Material.SUNFLOWER) {
            String customName = pickupItem.getCustomName();

            String shooter = null;
            if (customName != null) {
                String coinName = customName.replaceAll("&#§[a-fA-F0-9x]{6}", "");
                coinName = coinName.replaceAll("[^0-9.]", "");
                if (!coinName.isEmpty()) {
                    try {
                        double pickupMoneyValue = Double.parseDouble(coinName);

                        ItemStack sunflowerItem = pickupItem.getItemStack();
                        ItemMeta sunflowerMeta = sunflowerItem.getItemMeta();

                        if (sunflowerMeta != null) {
                            sunflowerMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
                            sunflowerMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            sunflowerItem.setItemMeta(sunflowerMeta);
                            pickupItem.setItemStack(sunflowerItem);

                            if (soundEnabled) {
                                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                            }
                        }

                        if (!(pickupMoneyValue <= 0.0)) {
                            double multiplier = 1.0;
                            ConfigurationSection permSection = config.getConfigurationSection("permissions");

                            if (permSection != null) {
                                for (String perm : permSection.getKeys(false)) {
                                    if (event.getPlayer().hasPermission("mobsmoneymobmoney." + perm)) {
                                        multiplier = permSection.getDouble(perm, 1.0);
                                        break;
                                    }
                                }
                            }

                            shooter = null;
                            if (pickupItem.hasMetadata("shooter")) {
                                shooter = pickupItem.getMetadata("shooter").get(0).asString();
                            }

                            if (shooter != null) {
                                Player player = MobsMoney.getInstance().getServer().getPlayer(UUID.fromString(shooter));
                                String messageMoneyValue = String.format("%.2f", pickupMoneyValue).replace(",", ".");
                                if (player != null) {
                                    String message = config.getString("messages.kill-getplayer");
                                    if (message != null) {
                                        message = message.replace("%money%", messageMoneyValue).replace("%player%", player.getName());
                                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                        event.setCancelled(true);
                                        pickupItem.remove();
                                    }

                                    this.economy.depositPlayer(event.getPlayer(), pickupMoneyValue);
                                }
                            } else {
                                String replace = String.format("%.2f", pickupMoneyValue).replace(",", ".");
                                double totalMultiplier = 1.0;

                                boolean hasBoost = false;
                                for (String permission : permSection.getKeys(false)) {
                                    if (event.getPlayer().hasPermission("mobsmoneymobmoney." + permission)) {
                                        double permissionMultiplier = permSection.getDouble(permission, 1.0);

                                        totalMultiplier *= permissionMultiplier;

                                        hasBoost = true;


                                        String message = config.getString("messages.kill-getmobs.boost");
                                        if (message != null) {
                                            String var12 = String.format("%.2f", permissionMultiplier * pickupMoneyValue - pickupMoneyValue).replace(",", ".");
                                            message = message.replace("%money%", replace).replace("%boost%", var12);
                                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                            break;
                                        }
                                    }
                                }

                                if (hasBoost) {
                                    pickupMoneyValue *= totalMultiplier;
                                    this.economy.depositPlayer(event.getPlayer(), pickupMoneyValue);
                                }

                                if (!hasBoost) {
                                    String message = config.getString("messages.kill-getmobs.no-boost");
                                    if (message != null) {
                                        message = message.replace("%money%", replace);
                                        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                                    }
                                }
                                pickupItem.remove();
                                event.setCancelled(true);
                            }
                        }
                    } catch (NumberFormatException e) {
                        event.getPlayer().sendMessage("Ошибка при преобразовании имени предмета в число.");
                    }
                } else {
                    event.getPlayer().sendMessage("Ошибка: Имя предмета не содержит числовое значение.");
                }
            }
        }
    }

}
