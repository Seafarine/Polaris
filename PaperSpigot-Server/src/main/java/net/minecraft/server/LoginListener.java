package net.minecraft.server;

import io.netty.util.concurrent.GenericFutureListener;
import net.shieldcommunity.spigot.utils.UtilHandler;
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.github.paperspigot.PaperSpigotConfig;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
// CraftBukkit end

public class LoginListener implements PacketLoginInListener, IUpdatePlayerListBox {

    private static final AtomicInteger b = new AtomicInteger(0);
    private static final Logger c = LogManager.getLogger();
    private static final Random random = new Random();
    private final byte[] e = new byte[4];
    private final MinecraftServer server;
    public final NetworkManager networkManager;
    private LoginListener.EnumProtocolState g;
    private int h;
    private GameProfile i;
    private String j;
    private SecretKey loginKey;
    private EntityPlayer l;
    public String hostname = ""; // CraftBukkit - add field
    public int packetsPerSecond;
    private long packetsPerSecondTime;
    private final Pattern nickNameHandler;

    public LoginListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.nickNameHandler = UtilHandler.safePatternCompile(PaperSpigotConfig.nameLoginHandler);
        this.g = LoginListener.EnumProtocolState.HELLO;
        this.j = "";
        this.server = minecraftserver;
        this.networkManager = networkmanager;
        this.packetsPerSecond = 0;
        this.packetsPerSecondTime = System.currentTimeMillis() + 1000L;
        LoginListener.random.nextBytes(this.e);
    }

    private boolean checkPacketLimit() {
        if (PaperSpigotConfig.usePacketLimiter) {
            if (this.packetsPerSecondTime < System.currentTimeMillis()) {
                this.packetsPerSecondTime = System.currentTimeMillis() + 1000L;
                this.packetsPerSecond = 0;
            }

            if (++this.packetsPerSecond > PaperSpigotConfig.maxPacketsPerSecond) {
                this.disconnect("%prefix% Too many packets!".replace("%prefix%", PaperSpigotConfig.nettyIoPrefix));
                return true;
            }
        }

        return false;
    }

    public void c() {
        if (!MinecraftServer.getServer().isRunning()) {
            this.disconnect(org.spigotmc.SpigotConfig.restartMessage);
            return;
        }
        if (this.g == LoginListener.EnumProtocolState.READY_TO_ACCEPT) {
            this.b();
        } else if (this.g == LoginListener.EnumProtocolState.e) {
            EntityPlayer entityplayer = this.server.getPlayerList().a(this.i.getId());

            if (entityplayer == null) {
                this.g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
                this.server.getPlayerList().a(this.networkManager, this.l);
                this.l = null;
            }
        }

        if (this.h++ == PaperSpigotConfig.timeOutTime) {
            this.disconnect("Took too long to log in");
        }

    }

    public void disconnect(String s) {
        try {
            LoginListener.c.info("Disconnecting " + this.d() + ": " + s);
            ChatComponentText chatcomponenttext = new ChatComponentText(s);

            this.networkManager.handle(new PacketLoginOutDisconnect(chatcomponenttext));
            this.networkManager.close(chatcomponenttext);
        } catch (Exception exception) {
            LoginListener.c.error("Error whilst disconnecting player", exception);
        }

    }

    private static final java.util.concurrent.ExecutorService authenticatorPool = java.util.concurrent.Executors.newCachedThreadPool(
            r -> new Thread(r, "User Authenticator #" + b.incrementAndGet())
    );

    // Spigot start
    public void initUUID() {
        UUID uuid;
        if (networkManager.spoofedUUID != null) {
            uuid = networkManager.spoofedUUID;
        } else {
            uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.i.getName()).getBytes(Charsets.UTF_8));
        }

        this.i = new GameProfile(uuid, this.i.getName());

        if (networkManager.spoofedProfile != null) {
            for (com.mojang.authlib.properties.Property property : networkManager.spoofedProfile) {
                this.i.getProperties().put(property.getName(), property);
            }
        }
    }
    // Spigot end

    public void b() {
        // Spigot start - Moved to initUUID
        /*
        if (!this.i.isComplete()) {
            this.i = this.a(this.i);
        }
        */
        // Spigot end

        // CraftBukkit start - fire PlayerLoginEvent
        EntityPlayer s = this.server.getPlayerList().attemptLogin(this, this.i, hostname);

        if (s == null) {
            // this.disconnect(s);
            // CraftBukkit end
        } else {
            this.g = LoginListener.EnumProtocolState.ACCEPTED;
            if (this.server.aK() >= 0 && !this.networkManager.c()) {
                this.networkManager.a(new PacketLoginOutSetCompression(this.server.aK()), new ChannelFutureListener() {
                    public void a(ChannelFuture channelfuture) throws Exception {
                        LoginListener.this.networkManager.a(LoginListener.this.server.aK());
                    }


                    public void operationComplete(ChannelFuture future) throws Exception { // CraftBukkit - fix decompile error
                        this.a(future);
                    }
                }, new GenericFutureListener[0]);
            }

            this.networkManager.handle(new PacketLoginOutSuccess(this.i));
            EntityPlayer entityplayer = this.server.getPlayerList().a(this.i.getId());

            if (entityplayer != null) {
                this.g = LoginListener.EnumProtocolState.e;
                this.l = this.server.getPlayerList().processLogin(this.i, s); // CraftBukkit - add player reference
            } else {
                this.server.getPlayerList().a(this.networkManager, this.server.getPlayerList().processLogin(this.i, s)); // CraftBukkit - add player reference
            }
        }

    }

    public void a(IChatBaseComponent ichatbasecomponent) {
        LoginListener.c.info(this.d() + " lost connection: " + ichatbasecomponent.c());
    }

    public String d() {
        String ip = PaperSpigotConfig.logPlayerConnectionSocket ? this.networkManager.getSocketAddress().toString() : "<ip address withheld>";
        return this.i != null ? this.i + " (" + ip + ")" : ip;
    }

    public void a(PacketLoginInStart packetlogininstart) {
        if (!this.checkPacketLimit()) {
            Validate.validState(this.g == LoginListener.EnumProtocolState.HELLO, "Unexpected hello packet");
            this.i = packetlogininstart.a();
            if (this.server.getOnlineMode() && !this.networkManager.c()) {
                this.g = LoginListener.EnumProtocolState.KEY;
                this.networkManager.handle(new PacketLoginOutEncryptionBegin(this.j, this.server.Q().getPublic(), this.e));
            } else {
                // Spigot start
                initUUID();
                authenticatorPool.execute(() -> {
                    try {
                        new LoginHandler().fireEvents();
                    } catch (Exception ex) {
                        disconnect("Failed to verify username!");
                        server.server.getLogger().log(java.util.logging.Level.WARNING, "Exception verifying " + i.getName(), ex);
                    }
                });
                // Spigot end
            }
        }
    }

    public void a(PacketLoginInEncryptionBegin packetlogininencryptionbegin) {
        if (!this.checkPacketLimit()) {
            Validate.validState(this.g == LoginListener.EnumProtocolState.KEY, "Unexpected key packet");
            PrivateKey privatekey = this.server.Q().getPrivate();

            if (!Arrays.equals(this.e, packetlogininencryptionbegin.b(privatekey))) {
                throw new IllegalStateException("Invalid nonce!");
            } else {
                this.loginKey = packetlogininencryptionbegin.a(privatekey);
                this.g = LoginListener.EnumProtocolState.AUTHENTICATING;
                this.networkManager.a(this.loginKey);

                authenticatorPool.execute(() -> {
                    GameProfile gameprofile = LoginListener.this.i;

                    try {
                        String s = (new BigInteger(MinecraftEncryption.a(LoginListener.this.j, LoginListener.this.server.Q().getPublic(), LoginListener.this.loginKey))).toString(16);

                        LoginListener.this.i = LoginListener.this.server.aD().hasJoinedServer(new GameProfile(null, gameprofile.getName()), s);
                        if (LoginListener.this.i != null) {
                            // CraftBukkit start - fire PlayerPreLoginEvent
                            if (!networkManager.g()) {
                                return;
                            }

                            new LoginHandler().fireEvents();
                        } else if (LoginListener.this.server.T()) {
                            LoginListener.c.warn("Failed to verify username but will let them in anyway!");
                            LoginListener.this.i = LoginListener.this.a(gameprofile);
                            LoginListener.this.g = EnumProtocolState.READY_TO_ACCEPT;
                        } else {
                            LoginListener.this.disconnect("Failed to verify username!");
                            LoginListener.c.error("Username \'" + gameprofile.getName() + "\' tried to join with an invalid session"); // CraftBukkit - fix null pointer
                        }
                    } catch (AuthenticationUnavailableException authenticationunavailableexception) {
                        if (LoginListener.this.server.T()) {
                            LoginListener.c.warn("Authentication servers are down but will let them in anyway!");
                            LoginListener.this.i = LoginListener.this.a(gameprofile);
                            LoginListener.this.g = EnumProtocolState.READY_TO_ACCEPT;
                        } else {
                            LoginListener.this.disconnect("Authentication servers are down. Please try again later, sorry!");
                            LoginListener.c.error("Couldn\'t verify username because servers are unavailable");
                        }
                        // CraftBukkit start - catch all exceptions
                    } catch (Exception exception) {
                        disconnect("Failed to verify username!");
                        server.server.getLogger().log(java.util.logging.Level.WARNING, "Exception verifying " + gameprofile.getName(), exception);
                        // CraftBukkit end
                    }

                });
            }
        }
    }

    // Spigot start
    public class LoginHandler {

        public void fireEvents() throws Exception {
            String playerName = i.getName();
            java.net.InetAddress address = ((java.net.InetSocketAddress) networkManager.getSocketAddress()).getAddress();
            java.util.UUID uniqueId = i.getId();
            final org.bukkit.craftbukkit.CraftServer server = LoginListener.this.server.server;

            AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId);
            server.getPluginManager().callEvent(asyncEvent);

            if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
                final PlayerPreLoginEvent event = new PlayerPreLoginEvent(playerName, address, uniqueId);
                if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
                    event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
                }
                Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() {
                    @Override
                    protected PlayerPreLoginEvent.Result evaluate() {
                        server.getPluginManager().callEvent(event);
                        return event.getResult();
                    }
                };

                LoginListener.this.server.processQueue.add(waitable);
                if (waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) {
                    disconnect(event.getKickMessage());
                    return;
                }
            } else {
                if (asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                    disconnect(asyncEvent.getKickMessage());
                    return;
                }
                if (asyncEvent.getAddress() == null) {
                    asyncEvent.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "[ShieldSpigot] Invalid Inet-Socket pool");
                    return;
                }

                String name = asyncEvent.getName();
                if (name.length() > PaperSpigotConfig.maxNameLength || name.length() < PaperSpigotConfig.minNameLength) {
                    asyncEvent.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "[ShieldSpigot] Invalid Name Length");
                    return;
                }

                if (!LoginListener.this.nickNameHandler.matcher(name).matches()) {
                    asyncEvent.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "[ShieldSpigot] Invalid Name Characters");
                    return;
                }
            }
            // CraftBukkit end
            LoginListener.c.info("UUID of player " + LoginListener.this.i.getName() + " is " + LoginListener.this.i.getId());
            LoginListener.this.g = LoginListener.EnumProtocolState.READY_TO_ACCEPT;
        }
    }
    // Spigot end

    protected GameProfile a(GameProfile gameprofile) {
        UUID uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + gameprofile.getName()).getBytes(Charsets.UTF_8));

        return new GameProfile(uuid, gameprofile.getName());
    }

    enum EnumProtocolState {

        HELLO, KEY, AUTHENTICATING, READY_TO_ACCEPT, e, ACCEPTED;

        EnumProtocolState() {
        }
    }
}
