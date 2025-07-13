package kz.ifihtich.chatutils.Commands;

import kz.ifihtich.chatutils.ChatUtils;
import kz.ifihtich.chatutils.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Top implements TabExecutor {

    FileConfiguration config = ChatUtils.getInstance().getConfig();
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0){
            return true;
        }
        else {
            String key = strings[0].toLowerCase();
            List<String> messages = config.getStringList("top." + key);
            for (String msg : messages){
                commandSender.sendMessage(Utils.color(PlaceholderAPI.setPlaceholders((Player) commandSender, msg)));
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0){
            return Arrays.asList("blocks", "clan_coins", "coins", "me", "online", "pvp");
            }
        if (strings.length == 1){
            return Arrays.asList("blocks", "clan_coins", "coins", "me", "online", "pvp");
        }
        return Collections.emptyList();
    }
}
