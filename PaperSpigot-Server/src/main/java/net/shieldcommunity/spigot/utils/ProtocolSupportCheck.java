package net.shieldcommunity.spigot.utils;

import org.bukkit.Bukkit;

public class ProtocolSupportCheck {

    private ProtocolSupportCheck() {
    }

    public static boolean hasProtocolSupport() {
        return Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport");
    }
}
