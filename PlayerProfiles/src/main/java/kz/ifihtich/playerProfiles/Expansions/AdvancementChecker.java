package kz.ifihtich.playerProfiles.Expansions;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

public class AdvancementChecker {
    public static boolean hasCompleted(Player player, String advancementKey) {
        NamespacedKey key = NamespacedKey.minecraft(advancementKey);
        Advancement advancement = Bukkit.getAdvancement(key);
        if (advancement == null) return false;

        AdvancementProgress progress = player.getAdvancementProgress(advancement);
        return progress.isDone();
    }
}
