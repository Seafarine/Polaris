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


    public final String PREFIX = " §e§lShield§6§lSpigot §7->";

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

        sender.sendMessage("------------------------------------");
         sender.sendMessage("");

        sender.sendMessage(PREFIX);
        sender.sendMessage("");
        java.lang.management.RuntimeMXBean runtimeMX = java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.lang.management.OperatingSystemMXBean osMX = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        if (runtimeMX != null && osMX != null) {

            sender.sendMessage(ChatColor.GOLD+ String.format("Java: %s (%s %s) Host: %s %s (%s) ",
                    runtimeMX.getSpecVersion(), runtimeMX.getVmName(), runtimeMX.getVmVersion(),
                    osMX.getName(), osMX.getVersion(), osMX.getArch())
            );
        }
        sender.sendMessage("");
         sender.sendMessage(
                 ChatColor.translateAlternateColorCodes(
                         '&', "&aTPS from last 1m, 5m, 15m: &e" +
                                 org.apache.commons.lang.StringUtils.join(tpsAvg, ", "))
         );

        long maxHeapMem = (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        long totalHeapMem = (Runtime.getRuntime().totalMemory() / 1024 / 1024);
        long freeHeapMem = (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        long usedHeapMem = maxHeapMem - freeHeapMem;
        ;

        sender.sendMessage(" ");
        sender.sendMessage("Backend cpu usage: " + getProcessorUsage(false) +
                " §f(Threads: §c" + ManagementFactory.getThreadMXBean().getThreadCount() + "§f)");
        sender.sendMessage("Processor usage: " + getProcessorUsage(true) +
                " §f(Cores: §c" + Runtime.getRuntime().availableProcessors() + "§f)");
        sender.sendMessage(" "
        );
        sender.sendMessage(
                "Maximum heap memory: §b" + maxHeapMem + " §fMB"
        );
        sender.sendMessage(
                 "Total heap memory: §b" + totalHeapMem + " §fMB"
        );
        sender.sendMessage(
               "Free heap memory: §b" + freeHeapMem + " §fMB"
        );
        sender.sendMessage(
                "Used heap memory: §b" + usedHeapMem + " §fMB"
        );
        sender.sendMessage(" ");
        long usedDirectMem = 0;
        List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        for (BufferPoolMXBean pool : pools) {
            usedDirectMem = usedDirectMem + pool.getMemoryUsed();
        }

        usedDirectMem = usedDirectMem / 1024 / 1024;

        sender.sendMessage(
                 "Used direct memory: §b" + usedDirectMem + " §fMB"
        );

        sender.sendMessage("");
        sender.sendMessage("------------------------------------");
        //NullCordX - end

        return true;
    }

    private static String format(double tps) // PaperSpigot - made static
    {
        return ( ( tps > 18.0 ) ? ChatColor.GREEN : ( tps > 16.0 ) ? ChatColor.YELLOW : ChatColor.RED ).toString()
                + ( ( tps > 20.0 ) ? "*" : "" ) + Math.min( Math.round( tps * 100.0 ) / 100.0, 20.0 );
    }

    public static String getProcessorUsage(boolean system) {
        String usage;
        double cpuUsage;
        try {
            com.sun.management.OperatingSystemMXBean systemBean = (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();

            cpuUsage = ((system ? systemBean.getSystemCpuLoad() : systemBean.getProcessCpuLoad()) * 100);
        }
        catch (Throwable t) {
            return "Not available: " + t.getMessage();
        }
        if (cpuUsage < 0) {
            usage = "Not available";
        }
        else {
            usage = getCpuFormat(cpuUsage, new DecimalFormat("#.#"));
        }

        return usage;
    }


    public static String getCpuFormat(double cpu, DecimalFormat df) {
        if (cpu <= 15) {
            return "§7" + df.format(cpu) + "%";
        }
        if (cpu <= 45) {
            return "§f" + df.format(cpu) + "%";
        }
        if (cpu <= 75) {
            return "§c" + df.format(cpu) + "%";
        }
        if (cpu <= 100) {
            return "§4" + df.format(cpu) + "%";
        }
        return "§7" + df.format(cpu) + "%";
    }
}
