package es.xism4.software.spigot.utils;

import org.bukkit.ChatColor;

public class TextUtil {

    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes(
                '&', text
        );
    }
}