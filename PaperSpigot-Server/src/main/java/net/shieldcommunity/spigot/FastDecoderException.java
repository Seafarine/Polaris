package net.shieldcommunity.spigot;

import io.netty.handler.codec.DecoderException;

public class FastDecoderException extends DecoderException {

    private static final boolean PROCESS_TRACES = Boolean.getBoolean("shieldspigot-decoder-traces");

    public FastDecoderException(String message, Throwable cause) {
        super(message, cause);
    }

    public FastDecoderException(String message) {
        super(message);
    }

    @Override
    public Throwable initCause(Throwable cause) {
        if (PROCESS_TRACES) {
            return super.initCause(cause);
        }
        return this;
    }

    @Override
    public Throwable fillInStackTrace() {
        if (PROCESS_TRACES) {
            return super.fillInStackTrace();
        }
        return this;
    }
}
