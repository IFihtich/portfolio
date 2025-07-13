package kz.ifihtich.playerProfiles.Expansions;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class Skull {

    public static ItemStack getHeadFrom64 ( String value ) {
        ItemStack head = new ItemStack ( Material. PLAYER_HEAD , 1 , ( short ) 3 ) ;
        SkullMeta meta = ( SkullMeta ) head. getItemMeta ( ) ;
        PlayerProfile profile = Bukkit. createProfile ( UUID. randomUUID ( ) ) ;
        profile. setProperty ( new ProfileProperty( "textures" , value ) ) ;
        meta. setPlayerProfile ( profile ) ;
        head. setItemMeta ( meta ) ;

        return head ;
    }
}
