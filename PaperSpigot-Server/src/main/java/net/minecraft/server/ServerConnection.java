package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.velocitypowered.natives.util.Natives;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.GenericFutureListener;
import net.shieldcommunity.spigot.config.ShieldSpigotConfigImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;


public class ServerConnection {

    private static final Logger e = LogManager.getLogger();
    public static final LazyInitVar<NioEventLoopGroup> a = new LazyInitVar() {
        private NioEventLoopGroup a() {
            return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };

    //shieldspigot
    public static final LazyInitVar<io.netty.incubator.channel.uring.IOUringEventLoopGroup> SERVER_IO_URING_EVENT_GROUP = new LazyInitVar<io.netty.incubator.channel.uring.IOUringEventLoopGroup>() {
        private io.netty.incubator.channel.uring.IOUringEventLoopGroup a() {
            return new io.netty.incubator.channel.uring.IOUringEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty io_uring Server IO #%d").setDaemon(true).build());
        }

        protected io.netty.incubator.channel.uring.IOUringEventLoopGroup init() {
            return this.a();
        }
    };
    //shieldspigot
    public static final LazyInitVar<EpollEventLoopGroup> b = new LazyInitVar() {
        private EpollEventLoopGroup a() {
            return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    public static final LazyInitVar<LocalEventLoopGroup> c = new LazyInitVar() {
        private LocalEventLoopGroup a() {
            return new LocalEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    private final MinecraftServer f;
    public volatile boolean d;
    private final List<ChannelFuture> g = Collections.synchronizedList(Lists.newArrayList());
    private final List<NetworkManager> h = Collections.synchronizedList(Lists.newArrayList());
    private static final boolean disableFlushConsolidation = Boolean.getBoolean("Paper.disableFlushConsolidate");
    private final java.util.Queue<NetworkManager> pending = new java.util.concurrent.ConcurrentLinkedQueue<>();

    private void addPending() {
        NetworkManager manager;
        while ((manager = pending.poll()) != null) {
            manager.isPending = false;
            this.h.add(manager);
        }
    }

    public ServerConnection(MinecraftServer minecraftserver) {
        this.f = minecraftserver;
        this.d = true;
    }

    // PandaSpigot start
    public void a(InetAddress inetaddress, int i) throws IOException {
        bind(new java.net.InetSocketAddress(inetaddress, i));
    }
    public void bind(java.net.SocketAddress address) throws IOException {
        // PandaSpigot end
        List list = this.g;

        synchronized (this.g) {
            Class oclass;
            LazyInitVar lazyinitvar;

            // PandaSpigot start - Add support for io_uring
            if ((io.netty.incubator.channel.uring.IOUring.isAvailable() || Epoll.isAvailable()) && this.f.ai()) {
                if (ShieldSpigotConfigImpl.IMP.USE_IO_URING && io.netty.incubator.channel.uring.IOUring.isAvailable() && !(address instanceof io.netty.channel.unix.DomainSocketAddress) && this.f.aK() == -1) {
                    oclass = io.netty.incubator.channel.uring.IOUringServerSocketChannel.class;
                    lazyinitvar = ServerConnection.SERVER_IO_URING_EVENT_GROUP;
                    ServerConnection.e.info("Using io_uring channel type");
                } else if (Epoll.isAvailable()) {
                    // PandaSpigot start - Unix domain socket support
                    if (address instanceof io.netty.channel.unix.DomainSocketAddress) {
                        oclass = io.netty.channel.epoll.EpollServerDomainSocketChannel.class;
                    } else {
                        oclass = EpollServerSocketChannel.class;
                    }
                    // PandaSpigot end
                    lazyinitvar = ServerConnection.b;
                    ServerConnection.e.info("Using epoll channel type");
                } else {
                    oclass = NioServerSocketChannel.class;
                    lazyinitvar = ServerConnection.a;
                    ServerConnection.e.info("Using default channel type");
                }
                // PandaSpigot end - Add support for io_uring
            } else {
                oclass = NioServerSocketChannel.class;
                lazyinitvar = ServerConnection.a;
                ServerConnection.e.info("Using default channel type");
            }

            this.g.add(((ServerBootstrap) ((ServerBootstrap) (new ServerBootstrap()).channel(oclass)).childHandler(new ChannelInitializer() {
                protected void initChannel(Channel channel) throws Exception {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
                    } catch (ChannelException channelexception) {
                        ;
                    }

                    if (!disableFlushConsolidation) channel.pipeline().addFirst(new io.netty.handler.flush.FlushConsolidationHandler()); // PandaSpigot
                    // PandaSpigot start - newlines
                    channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30))
                            .addLast("legacy_query", new LegacyPingHandler(ServerConnection.this))
                            .addLast("splitter", new PacketSplitter())
                            .addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND))
                            .addLast("prepender", PacketPrepender.INSTANCE) // PandaSpigot - Share PacketPrepender instance
                            .addLast("encoder", new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));
                    // PandaSpigot end
                    NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);

                    // PandaSpigot start - prevent blocking on adding a new network manager while the server is ticking
                    //ServerConnection.this.h.add(networkmanager);
                    ServerConnection.this.pending.add(networkmanager);
                    // PandaSpigot end
                    channel.pipeline().addLast("packet_handler", networkmanager);
                    networkmanager.a((PacketListener) (new HandshakeListener(ServerConnection.this.f, networkmanager)));
                }
            }).group((EventLoopGroup) lazyinitvar.c()).localAddress(address)).bind().syncUninterruptibly()); // PandaSpigot - Unix domain socket support
        }
    }
    public void b() {
        this.d = false;

        for (ChannelFuture channelfuture : this.g) {
            try {
                channelfuture.channel().close().sync();
            } catch (InterruptedException interruptedexception) {
                ServerConnection.e.error("Interrupted whilst closing channel, exception?");
            }
        }

    }

    public void c() {
        List list = this.h;

        synchronized (this.h) {
            this.addPending();
            // Spigot Start
            // This prevents players from 'gaming' the server, and strategically relogging to increase their position in the tick order
            if ( org.spigotmc.SpigotConfig.playerShuffle > 0 && MinecraftServer.currentTick % org.spigotmc.SpigotConfig.playerShuffle == 0 )
            {
                Collections.shuffle( this.h );
            }
            // Spigot End
            Iterator iterator = this.h.iterator();

            while (iterator.hasNext()) {
                final NetworkManager networkmanager = (NetworkManager) iterator.next();

                if (!networkmanager.h()) {
                    if (!networkmanager.g()) {
                        // Spigot Start
                        // Fix a race condition where a NetworkManager could be unregistered just before connection.
                        if (networkmanager.preparing) continue;
                        // Spigot End
                        iterator.remove();
                        networkmanager.l();
                    } else {
                        try {
                            networkmanager.a();
                        } catch (Exception exception) {
                            if (networkmanager.c()) {
                                CrashReport crashreport = CrashReport.a(exception, "Ticking memory connection");
                                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Ticking connection");

                                crashreportsystemdetails.a("Connection", new Callable() {
                                    public String a() throws Exception {
                                        return networkmanager.toString();
                                    }

                                    public Object call() throws Exception {
                                        return this.a();
                                    }
                                });
                                throw new ReportedException(crashreport);
                            }

                            ServerConnection.e.warn("Failed to handle packet for " + networkmanager.getSocketAddress() + "<ip address withheld>");
                            final ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");

                            networkmanager.a(new PacketPlayOutKickDisconnect(chatcomponenttext), (GenericFutureListener) future -> networkmanager.close(chatcomponenttext), new GenericFutureListener[0]);
                            networkmanager.k();
                        }
                    }
                }
            }

        }
    }

    public MinecraftServer d() {
        return this.f;
    }
}
