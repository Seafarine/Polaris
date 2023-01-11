package org.github.paperspigot;

import com.google.common.base.Throwables;
import net.minecraft.server.Items;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Level;

public class PaperSpigotConfig {

    public static boolean usePacketLimiter;
    public static int bookTick;
    public static int bookPageLength;
    public static int bookTitleLength;
    public static int bookMaxPages;
    public static int timeOutTime;
    public static boolean verifyChannelBeforeDecode;
    public static boolean logPlayerConnectionSocket;
    public static String paperSpigotLicense;
    public static int maxPacketsPerSecond;
    public static int maxBytesPerConnection;
    public static String nameLoginHandler;
    public static String nettyIoPrefix;
    public static String disconnectPrefixOnException;
    public static int minNameLength;
    public static int maxNameLength;
    public static double knockbackFriction;
    public static double knockbackHorizontal;
    public static double knockbackVertical;
    public static double knockbackVerticalLimit;
    public static double knockbackExtraHorizontal;
    public static double knockbackExtraVertical;
    public static boolean warnForExcessiveVelocity;

    private static File CONFIG_FILE;
    private static final String HEADER = "This is the main configuration file for PaperSpigot.\n"
            + "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
            + "with caution, and make sure you know what each option does before configuring.\n"
            + "\n"
            + "If you need help with the configuration or have any questions related to PaperSpigot,\n"
            + "join us at the IRC.\n"
            + "\n";
    /*========================================================================*/
    public static YamlConfiguration config;
    static int version;
    static Map<String, Command> commands;
    /*========================================================================*/

    public static void init(File configFile) {
        CONFIG_FILE = configFile;
        config = new YamlConfiguration();
        try {
            config.load(CONFIG_FILE);
        } catch (IOException ex) {
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load paper.yml, please correct your syntax errors", ex);
            throw Throwables.propagate(ex);
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);

        commands = new HashMap<>();

        version = getInt("config-version", 9);
        set("config-version", 9);
        readConfig(PaperSpigotConfig.class, null);
    }

    public static void registerCommands() {
        for (Map.Entry<String, Command> entry : commands.entrySet()) {
            MinecraftServer.getServer().server.getCommandMap().register(entry.getKey(), "PaperSpigot", entry.getValue());
        }
    }

    static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException ex) {
                        throw Throwables.propagate(ex.getCause());
                    } catch (Exception ex) {
                        Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, ex);
                    }
                }
            }
        }

        try {
            config.save(CONFIG_FILE);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    private static void set(String path, Object val) {
        config.set(path, val);
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, config.getDouble(path));
    }

    private static float getFloat(String path, float def) {
        // TODO: Figure out why getFloat() always returns the default value.
        return (float) getDouble(path, (double) def);
    }

    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List getList(String path, T def) {
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    public static double babyZombieMovementSpeed;

    private static void babyZombieMovementSpeed() {
        babyZombieMovementSpeed = getDouble("settings.baby-zombie-movement-speed", 0.5D); // Player moves at 0.1F, for reference
    }

    public static boolean interactLimitEnabled;

    private static void interactLimitEnabled() {
        interactLimitEnabled = getBoolean("settings.limit-player-interactions", true);
        if (!interactLimitEnabled) {
            Bukkit.getLogger().log(Level.INFO, "Disabling player interaction limiter, your server may be more vulnerable to malicious users");
        }
    }

    private static void antiSmasher() {
        paperSpigotLicense = getString("settings.shieldspigot.license", "your-license-goes-here");
        usePacketLimiter = getBoolean("settings.shieldspigot.enable-packet-limiter", true);
        maxPacketsPerSecond = getInt("settings.shieldspigot.max-packets-per-second", 750);
        verifyChannelBeforeDecode = getBoolean("settings.shieldspigot.verify-channel-before-decode", true);
        bookPageLength = getInt("settings.shieldspigot.book-page-length", 256);
        bookMaxPages = getInt("settings.shieldspigot.book-max-pages", 50);
        bookTick = getInt("settings.shieldspigot.book-last-tick", 20);
        bookTitleLength = getInt("settings.shieldspigot.book-title-length", 32);
        maxBytesPerConnection = getInt("settings.shieldspigot.max-bytes-per-connection", 35000);
        logPlayerConnectionSocket = getBoolean("settings.shieldspigot.log-player-connection-socket", true);
        timeOutTime = getInt("settings.shieldspigot.timeout-time", 600);
        nettyIoPrefix = getString("settings.shieldspigot.prefix", "ShieldSpigot");
        disconnectPrefixOnException = getString("settings.shieldspigot.disconnect-prefix", "Disconnected by ->");
        nameLoginHandler = getString("settings.shieldspigot.allowed-name-characters", "[a-zA-Z0-9_]*");
        minNameLength = getInt("settings.shieldspigot.min-name-length", 3);
        maxNameLength = getInt("settings.shieldspigot.max-name-length", 16);
        knockbackFriction = getDouble("settings.shieldspigot.knockback.friction", 2.0D);
        knockbackHorizontal = getDouble("settings.shieldspigot.knockback.horizontal", 0.4D);
        knockbackVertical = getDouble("settings.shieldspigot.knockback.vertical", 0.4D);
        knockbackVerticalLimit = getDouble("settings.shieldspigot.knockback.vertical-limit", 0.4D);
        knockbackExtraHorizontal = getDouble("settings.shieldspigot.knockback.extra-horizontal", 0.5D);
        knockbackExtraVertical = getDouble("settings.shieldspigot.knockback.extra-vertical", 0.1D);
    }

    public static double strengthEffectModifier;
    public static double weaknessEffectModifier;

    private static void effectModifiers() {
        strengthEffectModifier = getDouble("effect-modifiers.strength", 1.3D);
        weaknessEffectModifier = getDouble("effect-modifiers.weakness", -0.5D);
    }

    public static Set<Integer> dataValueAllowedItems;

    private static void dataValueAllowedItems() {
        dataValueAllowedItems = new HashSet<Integer>(getList("data-value-allowed-items", Collections.emptyList()));
        Bukkit.getLogger().info("Data value allowed items: " + StringUtils.join(dataValueAllowedItems, ", "));
    }

    public static boolean stackableLavaBuckets;
    public static boolean stackableWaterBuckets;
    public static boolean stackableMilkBuckets;

    private static void stackableBuckets() {
        stackableLavaBuckets = getBoolean("stackable-buckets.lava", false);
        stackableWaterBuckets = getBoolean("stackable-buckets.water", false);
        stackableMilkBuckets = getBoolean("stackable-buckets.milk", false);

        int size = Material.BUCKET.getMaxStackSize();
        if (stackableLavaBuckets) {
            Material.LAVA_BUCKET.setMaxStackSize(size);
            Items.LAVA_BUCKET.c(size);

            if (stackableWaterBuckets) {
                Material.WATER_BUCKET.setMaxStackSize(size);
                Items.WATER_BUCKET.c(size);
            }

            if (stackableMilkBuckets) {
                Material.MILK_BUCKET.setMaxStackSize(size);
                Items.MILK_BUCKET.c(size);
            }
        }
    }

    private static void excessiveVelocityWarning() {
        warnForExcessiveVelocity = getBoolean("warnWhenSettingExcessiveVelocity", true);
    }
}

