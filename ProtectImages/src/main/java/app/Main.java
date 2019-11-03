package app;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String text = "lol";
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash);
        System.out.println(encoded);
    }
}
