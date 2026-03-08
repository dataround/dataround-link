package io.dataround.admin.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Util {

    private static final String salt = "__--__";

    public static String getSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((salt + input + salt).getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error computing hash", e);
        }
    }
}
