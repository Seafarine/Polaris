package net.shieldcommunity.spigot.commands;

import net.shieldcommunity.api.CPUBufferReader;
import net.shieldcommunity.spigot.utils.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class ShieldSpigotCommand extends Command {

    public ShieldSpigotCommand(String name) {
        super(name);

        this.description = "Gets the information of the commands";
        this.usageMessage = "/system [plugin name]";
        this.setAliases(Arrays.asList("systeminformation", "shieldspigot"));
    }

    public final String PREFIX = " §e§lShield§6§lSpigot §7-> ";

    public static final String SEP = "§c§m--------------------------------------";
    public final String VERSION = "§a[0.0.6]";

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {

        if (!sender.hasPermission("shieldspigot.command.system")) {
            sender.sendMessage(PREFIX+VERSION +ChatColor.YELLOW+" powered by shieldcommunity.net");
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(
                    TextUtil.colorize(
                            "&e&lShield&6&lSpigot " +VERSION+"&7- &7by &6xism4")
            );
            sender.sendMessage(
                    TextUtil.colorize(
                            "&eUse /shieldspigot help &fto see more info about the fork"
                    ));
            return true;
        }

        else {
            switch (args[0].toLowerCase()) {
                case "help":
                    helpSubcommand(sender);
                    return true;
                case "performance":
                    performanceSubCommand(sender);
                    return true;
                case "jvm":
                    sendJvmInformation(sender);
                    return true;
                case "cpu":
                    sendCpuName(sender);
                    return true;
                case "gc":
                    callGarbageCollector(sender);
                    return true;
                default:
                    sender.sendMessage(
                            TextUtil.colorize("&cCommand not recognized!")
                    );
                    return true;
            }
        }
    }


    public void performanceSubCommand(CommandSender sender) {
        long maxHeapMem = (Runtime.getRuntime().maxMemory() / 1024 / 1024);
        long totalHeapMem = (Runtime.getRuntime().totalMemory() / 1024 / 1024);
        long freeHeapMem = (Runtime.getRuntime().freeMemory() / 1024 / 1024);
        long usedHeapMem = totalHeapMem - freeHeapMem;

        sender.sendMessage(SEP);
        ;
        sender.sendMessage(" ");
        sender.sendMessage(PREFIX + "Backend cpu usage: " + getProcessorUsage(false) +
                " §f(Threads: §c" + ManagementFactory.getThreadMXBean().getThreadCount() + "§f)");
        sender.sendMessage(PREFIX + "Processor usage: " + getProcessorUsage(true) +
                " §f(Cores: §c" + Runtime.getRuntime().availableProcessors() + "§f)");
        sender.sendMessage(" ");
        sender.sendMessage(PREFIX + "Maximum heap memory: §b" + maxHeapMem + " §fMB");
        sender.sendMessage(PREFIX + "Total heap memory: §b" + totalHeapMem + " §fMB");
        sender.sendMessage(PREFIX + "Free heap memory: §b" + freeHeapMem + " §fMB");
        sender.sendMessage(PREFIX + "Used heap memory: §b" + usedHeapMem + " §fMB");
        sender.sendMessage(" ");
        long usedDirectMem = 0;
        List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        for (BufferPoolMXBean pool : pools) {
            usedDirectMem = usedDirectMem + pool.getMemoryUsed();
        }

        usedDirectMem = usedDirectMem / 1024 / 1024;

        sender.sendMessage(PREFIX + "Used direct memory: §b" + usedDirectMem + " §fMB");

        long totalUsed = usedHeapMem + usedDirectMem;

        sender.sendMessage(" ");
        sender.sendMessage(PREFIX + "Total used memory: §b" + totalUsed + " §fMB");

        sender.sendMessage(SEP);
    }

    private void helpSubcommand(CommandSender sender){
        sender.sendMessage(
                TextUtil.colorize("&e&lShield&6&lSpigot &fcommands")
        );
        sender.sendMessage(
                TextUtil.colorize(
                        "- &e/shieldspigot help&f: Shows all the commands available for you")
        );
        if (sender.hasPermission("shieldspigot.admin")){
            sender.sendMessage(
                    TextUtil.colorize(
                            "- &e/shieldspigot performance&f: Gets the performance information")
            );
        }
        if (sender.hasPermission("shieldspigot.admin")){
            sender.sendMessage(
                    TextUtil.colorize(
                            "- &e/shieldspigot jvm&f: Gets the JVM information")
            );
        }
        if (sender.hasPermission("shieldspigot.admin")){
            sender.sendMessage(
                    TextUtil.colorize(
                            "- &e/shieldspigot CPU&f: Gets the CPU name and ID to check it")
            );
        }
        if (sender.hasPermission("shieldspigot.admin")){
            sender.sendMessage(
                    TextUtil.colorize(
                            "- &e/shieldspigot netty&f: Gets netty information")
            );
        }
        if(sender.hasPermission("shieldspigot.admin")) {
            sender.sendMessage(
                    TextUtil.colorize(
                            "- &e/shieldspigot &agc&f: Creates a big array to call GC"
                    )
            );
        }
    }


    private void sendCpuName(CommandSender sender) {
        try {
            sender.sendMessage(" ");
            sender.sendMessage(PREFIX +ChatColor.YELLOW+" CPU Model: " + ChatColor.GREEN+ CPUBufferReader.getModelName());
            sender.sendMessage(" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            sender.sendMessage(" ");
            sender.sendMessage(PREFIX + ChatColor.YELLOW+"CPU ID: " + ChatColor.GREEN+ CPUBufferReader.getCPUID());
            sender.sendMessage(" ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendJvmInformation(CommandSender sender) {
        java.lang.management.RuntimeMXBean runtimeMX = java.lang.management.ManagementFactory.getRuntimeMXBean();
        java.lang.management.OperatingSystemMXBean osMX = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        if (runtimeMX != null && osMX != null) {

            sender.sendMessage(PREFIX + ChatColor.GOLD + String.format(" System Information: %s (%s %s) Host: %s %s (%s) ",
                    runtimeMX.getSpecVersion(), runtimeMX.getVmName(), runtimeMX.getVmVersion(),
                    osMX.getName(), osMX.getVersion(), osMX.getArch())
            );
        }
    }

    private void callGarbageCollector(CommandSender sender) {
        System.gc(); //momento
        sender.sendMessage(TextUtil.colorize(PREFIX + "&aGarbage collector has been called sucesfully!"));
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