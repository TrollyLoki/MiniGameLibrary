package net.trollyloki.minigames.library.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class MiniGameUtils {

    /**
     * Removes a random element from the given list
     *
     * @param list List
     * @param <T> Type of object
     * @return Element that was removed
     */
    public static <T> T removeRandomElement(List<T> list) {
        int index = (int) (Math.random() * list.size());
        return list.remove(index);
    }

    /**
     * Returns a random element from the given list
     *
     * @param list List
     * @param <T> Type of object
     * @return Random element
     */
    public static <T> T getRandomElement(List<T> list) {
        int index = (int) (Math.random() * list.size());
        return list.get(index);
    }

    /**
     * Loads a location from a configuration section
     *
     * @param config Configuration section
     * @return Location
     */
    public static Location loadLocation(ConfigurationSection config) {
        return new Location(Bukkit.getWorld(config.getString("world")), config.getDouble("x"),
                config.getDouble("y"), config.getDouble("z"), (float) config.getDouble("yaw"),
                (float) config.getDouble("pitch"));
    }

    /**
     * Formats a length of seconds as a string
     *
     * @param seconds Seconds
     * @return String
     */
    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = seconds % 3600 / 60;
        seconds = seconds % 60;

        String string = "";
        if (hours > 0) {
            string += hours + ":";
            if (minutes < 10)
                string += "0";
        }
        string += minutes + ":";
        if (seconds < 10)
            string += "0";
        string += seconds;
        return string;
    }

    /**
     * Clears potion effects from the given player
     *
     * @param player Player
     */
    public static void clearPotionEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects())
            player.removePotionEffect(effect.getType());
    }

}
