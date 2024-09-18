package com.itranswarp.jerrymouse.utils;


import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

public class JwtUtil {
    private static final String PUB_KEY_FILENAME = "rsa_pub.key";

    private static final String PRIVATE_KEY_FILENAME = "rsa_private.key";

    // 单位: ms
    private static final long EXPIRE_TIME_LEN = 3600_000;

    private static final PrivateKey privateKey;

    public static final PublicKey publicKey;

    static {
        File pubFile = new File(PUB_KEY_FILENAME), privateFile = new File(PRIVATE_KEY_FILENAME);

        try {
            if (pubFile.exists() && privateFile.exists()) {
                publicKey = loadPublicKey();
                privateKey = loadPrivateKey();
            } else {
                KeyPair pair = generateKeyPair();
                publicKey = pair.getPublic();
                privateKey = pair.getPrivate();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateJwt(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME_LEN))
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    private static PublicKey loadPublicKey() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(PUB_KEY_FILENAME));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    private static PrivateKey loadPrivateKey() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(PRIVATE_KEY_FILENAME));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }

    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        // 保存
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
        Files.write(Paths.get(PUB_KEY_FILENAME), x509EncodedKeySpec.getEncoded());
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
        Files.write(Paths.get(PRIVATE_KEY_FILENAME), pkcs8EncodedKeySpec.getEncoded());

        return pair;
    }
}
