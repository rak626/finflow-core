package com.rakesh.finflow.util.common;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

public final class UserProfileIdGenerator {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    // 2-char machine fingerprint for distributed uniqueness
    private static final String MACHINE_ID = generateMachineId();

    private UserProfileIdGenerator() {
    }

    public static String generate() {
        long timestamp = Instant.now().toEpochMilli();
        int counter = COUNTER.getAndUpdate(x -> (x + 1) % 1000); // 0..999 rolling

        // base62 encoding pieces
        String ts = toBase62(timestamp);                 // typically 7 chars
        String cnt = padLeft(toBase62(counter), 2);     // 2 chars
        // make random component 4 chars (62^4 = 14,776,336)
        String rand = padLeft(toBase62(RANDOM.nextInt(62 * 62 * 62 * 62)), 4);

        StringBuilder sb = new StringBuilder();
        sb.append(ts).append(MACHINE_ID).append(cnt).append(rand);

        // If shorter than 15 (extremely unlikely), pad with extra random base62 chars
        while (sb.length() < 15) {
            sb.append(BASE62[RANDOM.nextInt(BASE62.length)]);
        }

        // If longer than 15, trim to 15
        return sb.substring(0, 15);
    }


    private static String generateMachineId() {
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            while (nets.hasMoreElements()) {
                NetworkInterface ni = nets.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null && mac.length >= 2) {
                    return "" + BASE62[mac[0] & 0xFF] + BASE62[mac[1] & 0xFF];
                }
            }
        } catch (Exception ignored) {
        }
        return "AA"; // fallback if MAC unavailable
    }

    private static String toBase62(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(BASE62[(int) (value % 62)]);
            value /= 62;
        }
        return sb.reverse().toString();
    }

    private static String padLeft(String s, int length) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < length) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }
}
