package kz.ifihtich.playerProfiles.Expansions;

import kz.ifihtich.playerProfiles.PlayerProfiles;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

public class Expansion extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "someauthor";
    }

    @Override
    public String getIdentifier() {
        return "playerprofiles";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null || !player.hasPlayedBefore()) return "";
        String name = player.getName();
        LikesManager likesManager = PlayerProfiles.getInstance().getLikesManager();

        if (params.equalsIgnoreCase("like")){
            return String.valueOf(likesManager.getLikes(name));
        }
        if (params.equalsIgnoreCase("dislike")){
            return String.valueOf(likesManager.getDislikes(name));
        }
        if (params.equalsIgnoreCase("rating")){
            int rating = likesManager.getRating(name);
            if (rating < 0){
                return "§c" + rating;
            }
            else if (rating == 0){
                return "&7" + rating;
            }
            else {
                return "&a" + rating;
            }
        }
        return null;
    }
}