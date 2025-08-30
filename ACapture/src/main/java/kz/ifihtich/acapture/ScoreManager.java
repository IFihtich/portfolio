package kz.ifihtich.acapture;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class ScoreManager {
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Map<String, Integer> teamScores = new HashMap<>();
    private boolean sidebarShown = false;
    public ScoreManager(){
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        if (scoreboard.getObjective("teamPoints") != null){
            scoreboard.getObjective("teamPoints").unregister();
        }

        objective = scoreboard.registerNewObjective("teamPoints", "dummy", ChatColor.GOLD + "Очки");

    }
    public void addPoints(String teamName, int amount){
        int newScore = teamScores.getOrDefault(teamName, 0) + amount;
        teamScores.put(teamName, newScore);

        Score score = objective.getScore(teamName);
        score.setScore(newScore);

        if (!sidebarShown){
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            sidebarShown=true;
        }
    }
    public void reset() {
        teamScores.clear();
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }

        objective.setDisplaySlot(null);
        sidebarShown = false;
    }
}
