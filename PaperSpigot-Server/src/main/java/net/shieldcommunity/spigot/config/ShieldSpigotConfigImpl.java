package net.shieldcommunity.spigot.config;

import net.elytrium.serializer.NameStyle;
import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.annotations.Transient;

import java.nio.file.Paths;

public class ShieldSpigotConfigImpl extends SafeYamlSerializable {

    @Transient
    private static final SerializerConfig SPIGOT_CONFIG = new SerializerConfig.Builder()
            .setCommentValueIndent(1)
            .setFieldNameStyle(NameStyle.MACRO_CASE)
            .setNodeNameStyle(NameStyle.KEBAB_CASE)
            .build();

    public static final ShieldSpigotConfigImpl IMP = new ShieldSpigotConfigImpl();

    public ShieldSpigotConfigImpl() {
        super(Paths.get("ShieldSpigot","config.yml"), SPIGOT_CONFIG);
    }

    @Comment(value = {
            @CommentValue("ShieldSpigot - Performant & Anti-Exploit tunned for 1.8.8"),
            @CommentValue(""),
            @CommentValue("Version: 0.0.5"),
            @CommentValue("Author: xism4 (discord)"),
            @CommentValue(""),
            @CommentValue("Support discord: https://discord.shieldcommunity.net/"),
            @CommentValue("Polymart: https://polymart.org/resource/shieldspigot-30-off.4906"),
    })

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "You must change the default field for your license"
            ),
            @CommentValue(
                    "You can get your license in https://licenses.shieldcommunity.net/"
            )
    })
    public String YOUR_LICENSE = "YOUR-LICENSE";

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "What should be the byte limit in the player instance? Increase this if your server has many books"
            )
    })
    public int MAX_BYTES_PER_CONNECTION = 50000;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the spigot use a custom packet decoder? This may help but cause some issues with plugins"
            )
    })
    public boolean USE_PACKET_FILTER = false;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "What should be the max packets-per-second for the spigot"
            )
    })
    public int MAX_PACKETS_PER_SECOND = 700;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "1.7 clients send weirdo big amount of packets, the backend will make a exception for this"
            )
    })
    public int MAX_PACKET_MULTIPLIER = 1024;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "What should be the player static interval? If you want higher items and stuff increase this option CAREFUL WITH THIS!"
            )
    })
    public int PLAYER_STATIC_INTERVAL = 1;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the spigot use Netty's IO_URING?"
            ),
            @CommentValue(
                    "In theory it should improve performance on processing operations with data"
            ),
            @CommentValue(
                    "How ever if UDM (Unix domain socket) is enabled would not work as excepted since is not supported by netty's client"
            )
    })
    public boolean USE_IO_URING = false;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the ligthning be ignored on grass? This exists on modern paper versions"
            )
    })
    public boolean IGNORE_LIGTH_ON_GRASS = false;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the backend return before MOB AI starts?"
            )
    })
    public boolean DISABLE_MOB_AI = false;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the spigot use TFO? (TCP Fast Open) This only works with NullCordX due proxy impl!"
            )
    })
    public boolean USE_TCP_FAST_OPEN = true;
    public int TCP_FAST_OPEN_MODE = 3;

    @NewLine
   @Comment(value = {
           @CommentValue(
                   "What should be the amount of threads per chunk? This can be influenced by the number of players you have"
           )
   })
    public int CHUNK_THREADS = 2;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "This will skip useless explosions particles, making less-lag and better FPS while seeing them"
            )
    })
    public boolean OPTIMIZE_EXPLOSIONS = false;

    @NewLine
    @Comment(value = {
              @CommentValue(
                     "What should be the amount of threads per player? This can be influenced by the number of players you have"
              )
    })
    public int PLAYER_PER_CHUNK_THREAD = 50;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the spigot try to optimize chunk chest handling?"
            )
    })
    public boolean OPTIMIZE_CHUNK_CHEST = false;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the spigot cache some player movements and animations? This may increase ram but deflate CPU usage"
            )
    })
    public boolean CACHE_PLAYERS_MOVEMENT = false;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the spigot handle faster potion handling? This may help on pot-pvp servers"
            )
    })
    public boolean FASTER_POTION_HANDLING = false;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the spigot load BlockPhysicsEvent this may help on some servers"
            )
    })
    public boolean USE_BLOCKPHISIC_EVENT = false;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the spigot disable Spigot player tracker thread? This may increase performance"
            ),
            @CommentValue(
                    "Some plugins use this to track players, so if you have some issues with some plugins, try to disable this"
            )
    })
    public boolean DISABLE_SPIGOT_TRACKER = false;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Name for Netty operations (may not appear on client disconnection)"
            )
    })
    public String NETTY_HANDLER_PREFIX = "ShieldSpigot";

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the spigot log some player connection exceptions? This may be useful for debug"
            )
    })

    public boolean LOG_PLAYER_CONNECTION_EXCEPTIONS = true;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Should the spigot log player ip? This may be useful for connection privacity"
            )
    })

    public boolean LOG_PLAYER_ADDRESS = false;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "Maximum amount of ms for a connection to send timeout handler"
            )
    })
    public int TIMEOUT_HANDLER = 600;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "This is the configuration for book limitation, if you need more information, pages you must edit here"
            )
    })
    public int MAX_PAGES = 50;
    public int MAX_BOOK_PAGE_LENGTH = 256;
    public int MAX_TITLE_LENGTH = 32;
    public int MAX_BOOK_TICKS = 20;

    @NewLine
    @Comment(value = {
            @CommentValue(
                    "This is the knockback configuration, you can edit here the knockback values"
            )
    })

    public double KNOCKBACK_FRICTION = 2D;
    public double KNOCKBACK_HORIZONTAL = 0.4D;
    public double KNOCKBACK_VERTICAL = 0.4D;
    public double KNOCKBACK_HORIZONTAL_LIMIT = 0.4D;
    public double KNOCKBACK_VERTICAL_LIMIT = 0.4D;
    public double KNOCKBACK_EXTRA_HORIZONTAL = 0.5D;
    public double KNOCKBACK_EXTRA_VERTICAL = 0.1D;

}
