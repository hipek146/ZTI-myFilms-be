package com.example.myfilms;

import javax.crypto.KeyGenerator;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class SecretKey {

    private static String key;

    public void generate() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        key = keyGenerator.generateKey().toString();
    }

    public static byte[] getBytes() {
        return key.getBytes(StandardCharsets.UTF_8);
    }

}
