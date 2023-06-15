package net.shieldcommunity.spigot.commands;

import net.shieldcommunity.api.CPUBufferReader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.nio.file.FileStore;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class SystemCommand extends Command {

    public SystemCommand(String name) {
        super(name);

        this.description = "Gets the information of the commands";
        this.usageMessage = "/system [plugin name]";
        this.setAliases(Arrays.asList("systeminformation", "shieldspigot"));
    }

    public final String PREFIX = " §e§lShield§6§lSpigot §7-> ";
    public final String VERSION = "§a[0.0.4-b]";

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {

        if(!sender.hasPermission("shieldspigot.command.system")) {
            sender.sendMessage(ChatColor.RED+" Sorry, you don't have permission to use this command.");
            return false;
        }

        long maxHeapMem = (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        long totalHeapMem = (Runtime.getRuntime().totalMemory() / 1024 / 1024);
        long freeHeapMem = (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        long usedHeapMem = maxHeapMem - freeHeapMem;
        ;


        sender.sendMessage(ChatColor.GRAY+"§n------------------------------------");
        sender.sendMessage("");

        sender.sendMessage(PREFIX+VERSION +ChatColor.YELLOW+" powered by shieldcommunity.net");
        sender.sendMessage("");

        java.lang.management.RuntimeMXBean runtimeMX = java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.lang.management.OperatingSystemMXBean osMX = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        if (runtimeMX != null && osMX != null) {

            sender.sendMessage(ChatColor.GOLD+ String.format("System Information: %s (%s %s) Host: %s %s (%s) ",
                    runtimeMX.getSpecVersion(), runtimeMX.getVmName(), runtimeMX.getVmVersion(),
                    osMX.getName(), osMX.getVersion(), osMX.getArch())
            );
        }

        sender.sendMessage("");

        try {
            sender.sendMessage(ChatColor.YELLOW+" CPU Model: " + ChatColor.GREEN+ CPUBufferReader.getModelName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            sender.sendMessage(ChatColor.YELLOW+"CPU ID: " + ChatColor.GREEN+ CPUBufferReader.getCPUID());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        sender.sendMessage("");


        File[] roots = File.listRoots();

        for (File root : roots) {
            sender.sendMessage(ChatColor.YELLOW+"File system root: " + ChatColor.GREEN+ String.format(root.getAbsolutePath()));
            sender.sendMessage(ChatColor.YELLOW+"Total space (bytes): "+ ChatColor.GREEN + String.format(String.valueOf(root.getTotalSpace())));
            sender.sendMessage(ChatColor.YELLOW+"Free space (bytes): "+ ChatColor.GREEN + String.format(String.valueOf(root.getFreeSpace())));
        }

        sender.sendMessage(" ");
        sender.sendMessage(ChatColor.YELLOW+"Backend cpu usage: " + getProcessorUsage(false));
        sender.sendMessage(ChatColor.YELLOW+"Processor usage: " + getProcessorUsage(true) +
                " §f(Cores: §c" + Runtime.getRuntime().availableProcessors() + "§f)");
        sender.sendMessage(" "
        );
        sender.sendMessage(
                ChatColor.YELLOW+"Maximum heap memory: §b" + maxHeapMem + " §AMB"
        );
        sender.sendMessage(ChatColor.YELLOW+
                "Total heap memory: §b" + totalHeapMem + " §AMB"
        );
        sender.sendMessage(ChatColor.YELLOW+
                "Free heap memory: §b" + freeHeapMem + " §AMB"
        );
        sender.sendMessage(ChatColor.YELLOW+
                "Used heap memory: §b" + usedHeapMem + " §AMB"
        );
        sender.sendMessage(" ");

        long usedDirectMem = 0;
        List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        for (BufferPoolMXBean pool : pools) {
            usedDirectMem = usedDirectMem + pool.getMemoryUsed();
        }

        usedDirectMem = usedDirectMem / 1024 / 1024;

        sender.sendMessage(ChatColor.YELLOW+
                "Used direct memory: §b" + usedDirectMem + " §AMB"
        );


        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY+"§n------------------------------------");


        return true;
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
