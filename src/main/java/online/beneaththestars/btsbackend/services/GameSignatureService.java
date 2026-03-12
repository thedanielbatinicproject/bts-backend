package online.beneaththestars.btsbackend.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class GameSignatureService {

    private final byte[] secretBytes;

    public GameSignatureService(@Value("${bts.gameSecret}") String gameSecret) {
        if (gameSecret == null || gameSecret.isBlank()) {
            throw new IllegalStateException("Missing config property bts.gameSecret");
        }
        this.secretBytes = gameSecret.getBytes(StandardCharsets.UTF_8);
    }

    public void verifyOrThrow(String steamId, String puzzleCode, long timeMs, long clientTimestamp, String providedSignature) {
        String canonical = canonicalString(steamId, puzzleCode, timeMs, clientTimestamp);
        String expected = hmacSha256Hex(canonical);

        if (!constantTimeEqualsIgnoreCase(expected, providedSignature)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid signature!");
        }
    }

    private String canonicalString(String steamId, String puzzleCode, long timeMs, long clientTimestamp) {
        return steamId + ":" + puzzleCode + ":" + timeMs + ":" + clientTimestamp;
    }

    private String hmacSha256Hex(String canonical) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"));
            byte[] raw = mac.doFinal(canonical.getBytes(StandardCharsets.UTF_8));
            return toHex(raw);
        } catch (Exception e) {
            throw new RuntimeException("HMAC error", e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static boolean constantTimeEqualsIgnoreCase(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;

        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            char ca = Character.toLowerCase(a.charAt(i));
            char cb = Character.toLowerCase(b.charAt(i));
            result |= (ca ^ cb);
        }
        return result == 0;
    }
}