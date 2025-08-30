package kz.ifihtich.acapture;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReloadCommand implements TabExecutor {
    private final ACapture plugin;

    public ReloadCommand(ACapture plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.hasPermission("ACapture.reload")) {
            sender.sendMessage("§cНет прав.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§eИспользование: /acapture reload | givepoints <team> <amount>");
            return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            ACapture.getInstance().onReload(plugin);
            sender.sendMessage("§aПлагин перезагружен");
            return true;
        }
        if (args[0].equalsIgnoreCase("givepoints")) {
            if (args.length < 3) {
                sender.sendMessage("§eИспользование: /acapture givepoints <team> <amount>");
                return true;
            }
            String teamName = args[1];
            int amount;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cНеверное число: " + args[2]);
                return true;
            }

            Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
            if (team == null) {
                sender.sendMessage("§cКоманда не найдена: " + teamName);
                return true;
            }

            plugin.getScoreManager().addPoints(teamName, amount);
            sender.sendMessage("§aВыдано " + amount + " очков команде " + teamName);
            return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.hasPermission("ACapture.reload")) return completions;

        if (args.length == 1) {
            completions.addAll(Arrays.asList("reload", "givepoints"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("givepoints")) {
            for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                completions.add(team.getName());
            }
        }

        return completions;
    }
}
