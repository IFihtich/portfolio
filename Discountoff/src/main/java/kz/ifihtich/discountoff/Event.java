package kz.ifihtich.discountoff;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.MerchantRecipe;

public class Event implements Listener {


    @EventHandler
    public void villagerClick(PlayerInteractEntityEvent event){
        if (event.getRightClicked() instanceof Villager){
            Villager villager = (Villager) event.getRightClicked();
            if (villager.hasAI()){
                for (MerchantRecipe recipe : villager.getRecipes()){
                    float multiply = 0.0F;
                    recipe.setPriceMultiplier(multiply);
                }
            }
        }
    }
}
