package net.minecraft.server;

import es.xism4.software.spigot.velocity.VarIntHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@io.netty.channel.ChannelHandler.Sharable
public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {

    public static final PacketPrepender INSTANCE = new PacketPrepender();
    private PacketPrepender() {}

    protected void a(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, ByteBuf bytebuf1) throws Exception {
        VarIntHandler.writeVarInt(bytebuf1, bytebuf.readableBytes());
        bytebuf1.writeBytes(bytebuf);
    }

    protected void encode(ChannelHandlerContext channelhandlercontext, ByteBuf object, ByteBuf bytebuf) throws Exception {
        this.a(channelhandlercontext, (ByteBuf) object, bytebuf);
    }
    @Override
    protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
        int anticipatedRequiredCapacity = VarIntHandler.varIntBytes(msg.readableBytes())
                + msg.readableBytes();

        return ctx.alloc().directBuffer(anticipatedRequiredCapacity);
    }
}