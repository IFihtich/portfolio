package kz.ifihtich.dropmoney;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Event implements Listener {

    @EventHandler
    public void onKill(PlayerDeathEvent event){
        if (event.getEntity().getKiller() != null){
            Player victim = event.getEntity();
            Player killer = victim.getKiller();
            double money = Dropmoney.getEconomy().getBalance(victim) * 0.1;
            money = Math.round(money * 100.0) / 100.0;
            Dropmoney.getEconomy().withdrawPlayer(victim, money);
            Dropmoney.getEconomy().depositPlayer(killer, money);
        }
    }
}
