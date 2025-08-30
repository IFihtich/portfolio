package kz.ifihtich.acapture;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class HologramManager {
    private final List<ArmorStand> lines = new ArrayList<>();
    private Location baseLocation;

    public void createHologram(Location location, List<String> textLines) {
        remove();
        baseLocation = location.clone();
        Location current = location.clone();

        for (String line : textLines) {
            ArmorStand stand = spawnLine(current, Utils.color(line));
            lines.add(stand);
            current.subtract(new Vector(0, 0.25, 0));
        }
    }

    public void updateLines(List<String> newLines) {
        remove();
        if (baseLocation == null) return;

        if (lines.size() != newLines.size()) {
            createHologram(baseLocation, newLines);
            return;
        }

        for (int i = 0; i < newLines.size(); i++) {
            ArmorStand stand = lines.get(i);
            if (stand != null && !stand.isDead()) {
                stand.setCustomName(newLines.get(i));
            }
        }
    }

    public void remove() {
        for (ArmorStand stand : lines) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }
        lines.clear();
    }

    private ArmorStand spawnLine(Location location, String text) {
        ArmorStand stand = location.getWorld().spawn(location, ArmorStand.class);
        stand.setVisible(false);
        stand.setCustomNameVisible(true);
        stand.setCustomName(text);
        stand.setGravity(false);
        stand.setMarker(true);
        stand.setSmall(true);
        return stand;
    }
}
