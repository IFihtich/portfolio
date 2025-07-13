package kz.ifihtich.playerProfiles.Menues;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuHolder implements InventoryHolder {
    private final String menuName;
    private Inventory inventory;

    public MenuHolder(String menuName, int size, String title) {
        this.menuName = menuName;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public String getMenuName() {
        return menuName;
    }
}