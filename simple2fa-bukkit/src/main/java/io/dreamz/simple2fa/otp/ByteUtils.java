package io.dreamz.simple2fa.otp;

public final class ByteUtils {
    public static int truncate(byte[] hmac) {
        int offset = hmac[hmac.length - 1] & 0xf;
        return (hmac[offset] & 0x7f) << 24
                | (hmac[offset + 1] & 0xff) << 16
                | (hmac[offset + 2] & 0xff) << 8
                | (hmac[offset + 3] & 0xff);
    }


    private ByteUtils() {
    }
}
