package kz.ifihtich.playerProfiles.Expansions;

import kz.ifihtich.playerProfiles.PlayerProfiles;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class LikesManager {
    private final File file;
    private final FileConfiguration config;

    public LikesManager(){
        file = new File(PlayerProfiles.getInstance().getDataFolder(), "likes.yml");
        if (!file.exists()){
            try{
                file.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }
    public boolean hasVoted(String voter, String target){
        return config.contains("players." + voter + ".votes." + target);
    }

    public void cancelVote(String voter, String target){
        if (!hasVoted(voter, target)) return;
        String path = "players." + voter + ".votes." + target;
        String previousVote = config.getString(path);

        config.set(path, null);
        String statsPath = "players." + target + ".stats." + (previousVote.equalsIgnoreCase("like") ? "likes" : "dislikes");
        int current  = config.getInt(statsPath, 0);
        config.set(statsPath, Math.max(0, current - 1));
        save();

        Player player = Bukkit.getPlayer(voter);
        String sound = PlayerProfiles.getInstance().getConfig().getString("sound.vote_cancelled.sound");
        float volume = (float) PlayerProfiles.getInstance().getConfig().getDouble("sound.vote_cancelled.volume", 1.0);
        float pitch = (float) PlayerProfiles.getInstance().getConfig().getDouble("sound.vote_cancelled.pitch", 1.0);
        assert player != null;
        player.playSound(player.getLocation(), sound, org.bukkit.SoundCategory.MASTER, volume, pitch);
    }

    public int getRating(String target){
        int likes = getLikes(target);
        int dislikes = getDislikes(target);
        return likes - dislikes;
    }
    public void vote(String voter, String target, String type){
        if (voter.equalsIgnoreCase(target)) return;

        if (hasVoted(voter, target)) return;

        if (type.equalsIgnoreCase("like")){
            Player player = Bukkit.getPlayer(voter);
            if (PlayerProfiles.getInstance().getConfig().getBoolean("sound.like.enable")){
                String sound = PlayerProfiles.getInstance().getConfig().getString("sound.like.sound");
                float volume = (float) PlayerProfiles.getInstance().getConfig().getDouble("sound.like.volume", 1.0);
                float pitch = (float) PlayerProfiles.getInstance().getConfig().getDouble("sound.like.pitch", 1.0);
                assert player != null;
                player.playSound(player.getLocation(), sound, org.bukkit.SoundCategory.MASTER, volume, pitch);
            }
        }
        else {
            Player player = Bukkit.getPlayer(voter);
            if (PlayerProfiles.getInstance().getConfig().getBoolean("sound.dislike.enable")){
                String sound = PlayerProfiles.getInstance().getConfig().getString("sound.dislike.sound");
                float volume = (float) PlayerProfiles.getInstance().getConfig().getDouble("sound.dislike.volume");
                float pitch = (float) PlayerProfiles.getInstance().getConfig().getDouble("sound.dislike.pitch");
                assert player != null;
                player.playSound(player.getLocation(), sound, org.bukkit.SoundCategory.MASTER, volume, pitch);
            }
        }

        config.set("players." + voter + ".votes." + target, type);
        String path = "players." + target + ".stats." + (type.equalsIgnoreCase("like") ? "likes" : "dislikes");
        int current = config.getInt(path, 0);
        config.set(path, current + 1);

        save();
    }
    public int getLikes(String target){
        return config.getInt("players." + target + ".stats.likes", 0);
    }
    public int getDislikes(String target){
        return config.getInt("players." + target + ".stats.dislikes", 0);
    }

    public void save(){
        try{
            config.save(file);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
