package kz.ifihtich.mobsmoney;


import org.bukkit.*;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MobsMoney extends JavaPlugin{
    public static Economy economy;
    public static boolean soundEnabled = true;
    public static MobsMoney instance;

    public MobsMoney() {
    }

    public void onEnable() {
        instance = this;
        this.getLogger().info("&7[&6MobsMoney&7] &aEnabled");
        if (!this.setupEconomy()) {
            this.getLogger().severe(Utils.color("&eVault &cне обнаружен. Плагин выключен."));
            this.getServer().getPluginManager().disablePlugin(this);
        } else {
            this.saveDefaultConfig();
            this.getServer().getPluginManager().registerEvents(new Listeners(), instance);
            EnableText();
        }
    }

    public static MobsMoney getInstance(){
        return instance;
    }

    public void EnableText() {
        String text =
                "\n" +
                        "       ____  ______  _____ _______ _____  ________      __\n" +
                        "      |  _ \\|  ____|/ ____|__   __|  __ \\|  ____\\ \\    / /\n" +
                        "      | |_) | |__  | (___    | |  | |  | | |__   \\ \\  / /\n" +
                        "      |  _ <|  __|  \\___ \\   | |  | |  | |  __|   \\ \\/ /\n" +
                        "      | |_) | |____ ____) |  | |  | |__| | |____   \\  /\n" +
                        "      |____/|______|_____/   |_|  |_____/|______|   \\/" +
                        "\n" +
                        "\n" +
                        "\n" +
                        "   Studio: BestDev\n" +
                        "   TG: https://t.me/bestdevstudio\n" +
                        "   VK: https://vk.com/bestdevstudio\n" +
                        "&7[&6MobsMoney&7] &aEnabled";
        Bukkit.getLogger().info((text));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("soundmoney")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                soundEnabled = !soundEnabled;
                player.sendMessage("§x§f§b§8§e§0§e[❖] §fВы успешно " + (soundEnabled ? "§aвключили" : "§cвыключили") + " §fзвук при подборе монеток");
            } else {
                sender.sendMessage("§x§f§b§3§f§1§6[❖] §fКоманду можно вводить только от имени игрока");
            }
            return true;
        }
        return false;
    }

    public boolean setupEconomy() {
        RegisteredServiceProvider<Economy> var1 = getServer().getServicesManager().getRegistration(Economy.class);
        if (var1 == null) {
            return false;
        }

        economy = var1.getProvider();
        return true;
    }
}