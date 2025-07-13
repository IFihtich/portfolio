package kz.ifihtich.chatutils.Commands;

import kz.ifihtich.chatutils.ChatUtils;
import kz.ifihtich.chatutils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Discord implements CommandExecutor {

    FileConfiguration config = ChatUtils.getInstance().getConfig();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> ds = config.getStringList("discord");

        for (String discord : ds) {
            commandSender.sendMessage(Utils.color(discord));
            return true;
        }
        return true;
    }
}
