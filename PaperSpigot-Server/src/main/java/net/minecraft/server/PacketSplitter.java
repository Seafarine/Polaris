package net.minecraft.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import net.shieldcommunity.spigot.velocity.LegacyVarIntByteDecoder;

import java.util.List;

public class PacketSplitter extends ByteToMessageDecoder {

    public PacketSplitter() {}

    protected void decode(ChannelHandlerContext channelhandlercontext, ByteBuf bytebuf, List<Object> list) throws Exception {
        if (!channelhandlercontext.channel().isActive()) {
            bytebuf.clear();
            return;
        }

        final LegacyVarIntByteDecoder reader = new LegacyVarIntByteDecoder();
        int varIntEnd = bytebuf.forEachByte(reader);

        if (varIntEnd == -1) {
            // We tried to go beyond the end of the buffer. This is probably a good sign that the
            // buffer was too short to hold a proper varint.
            if (reader.getResult() == LegacyVarIntByteDecoder.DecodeResult.RUN_OF_ZEROES) {
                // Special case where the entire packet is just a run of zeroes. We ignore them all.
                bytebuf.clear();
            }
            return;
        }

        if (reader.getResult() == LegacyVarIntByteDecoder.DecodeResult.RUN_OF_ZEROES) {
            // this will return to the point where the next varint starts
            bytebuf.readerIndex(varIntEnd);
        } else if (reader.getResult() == LegacyVarIntByteDecoder.DecodeResult.SUCCESS) {
            int readVarint = reader.getReadVarint();
            int bytesRead = reader.getBytesRead();
            if (readVarint < 0) {
                bytebuf.clear();
                throw new CorruptedFrameException("Bad packet length");
            } else if (readVarint == 0) {
                // skip over the empty packet(s) and ignore it
                bytebuf.readerIndex(varIntEnd + 1);
            } else {
                int minimumRead = bytesRead + readVarint;
                if (bytebuf.isReadable(minimumRead)) {
                    list.add(bytebuf.retainedSlice(varIntEnd + 1, readVarint));
                    bytebuf.skipBytes(minimumRead);
                }
            }
        } else if (reader.getResult() == LegacyVarIntByteDecoder.DecodeResult.TOO_BIG) {
            bytebuf.clear();
            throw new CorruptedFrameException("VarInt too big");
        }
        // PandaSpigot end
    }
}
