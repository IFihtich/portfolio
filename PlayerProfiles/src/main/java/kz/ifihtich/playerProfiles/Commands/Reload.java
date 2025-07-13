package kz.ifihtich.playerProfiles.Commands;

import kz.ifihtich.playerProfiles.ChatUtils.ChatUtil;
import kz.ifihtich.playerProfiles.PlayerProfiles;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Reload implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender.hasPermission("playerprofiles.reload")) {
            if (strings.length > 0 && strings[0].equalsIgnoreCase("reload")) {
                PlayerProfiles.getInstance().reloadConfig();
                PlayerProfiles.getInstance().reloadLikesConfig();
                String message = PlayerProfiles.getInstance().getConfig().getString("messages.reload");
                ChatUtil.sendMessage(commandSender, message);
                return true;
            }
        }
        else {
            String perm = PlayerProfiles.getInstance().getConfig().getString("messages.permission");
            ChatUtil.sendMessage(commandSender, perm);
            return true;
        }
        return true;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (commandSender.hasPermission("playerprofiles.reload")){
            return List.of("reload");
        }
        return null;
    }
}
