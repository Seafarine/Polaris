package org.spigotmc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.List;

public class TicksPerSecondCommand extends Command
{

    public TicksPerSecondCommand(String name)
    {
        super( name );
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission( "bukkit.command.tps" );
    }


    public final String PREFIX = " §e§lShield§6§lSpigot §7-> ";
    public final String VERSION = "§a[0.0.7]";

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args)
    {
        if ( !testPermission( sender ) )
        {
            return true;
        }

        // PaperSpigot start - Further improve tick handling
        double[] tps = org.bukkit.Bukkit.spigot().getTPS();
        String[] tpsAvg = new String[tps.length];

         for ( int i = 0; i < tps.length; i++) {
             tpsAvg[i] = format( tps[i] );
        }

        sender.sendMessage(ChatColor.GRAY+"§n------------------------------------");
         sender.sendMessage("");

        sender.sendMessage(PREFIX+VERSION +ChatColor.YELLOW+" powered by shieldcommunity.net");
        sender.sendMessage("");
         sender.sendMessage(
                 ChatColor.translateAlternateColorCodes(
                         '&', "&aTPS from last 1m, 5m, 15m: &e" +
                                 org.apache.commons.lang.StringUtils.join(tpsAvg, ", "))
         );

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY+"§n------------------------------------");
        //NullCordX - end

        return true;
    }

    private static String format(double tps) // PaperSpigot - made static
    {
        return ( ( tps > 18.0 ) ? ChatColor.GREEN : ( tps > 16.0 ) ? ChatColor.YELLOW : ChatColor.RED ).toString()
                + ( ( tps > 20.0 ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, 20.0 );
    }

}
