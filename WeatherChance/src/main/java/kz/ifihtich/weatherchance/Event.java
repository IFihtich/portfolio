package kz.ifihtich.weatherchance;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.Random;


public class Event implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void WeatherChange(WeatherChangeEvent event){
        if (event.toWeatherState() && random.nextInt(5) != 0){
            event.setCancelled(true);
        }
    }
}
