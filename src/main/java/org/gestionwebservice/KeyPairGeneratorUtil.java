package org.gestionwebservice;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyPairGeneratorUtil {
    public static void main(String[] args) {
        try {
            // Generate RSA key pair
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(2048); // Key size
            KeyPair pair = keyPairGen.generateKeyPair();

            // Extract private and public keys
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();

            // Create key specs
            PKCS8EncodedKeySpec pkcs8PrivateKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            X509EncodedKeySpec x509PublicKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());

            // Save keys in full PEM format
            saveKeyToFile("privateKey.pem", pkcs8PrivateKeySpec.getEncoded(), "PRIVATE KEY");
            saveKeyToFile("publicKey.pem", x509PublicKeySpec.getEncoded(), "PUBLIC KEY");

            System.out.println("Keys generated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveKeyToFile(String fileName, byte[] keyBytes, String keyType) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            String key = Base64.getEncoder().encodeToString(keyBytes);
            String formattedKey = formatKey(key, keyType);
            fos.write(formattedKey.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String formatKey(String key, String type) {
        StringBuilder formattedKey = new StringBuilder();
        formattedKey.append("-----BEGIN ").append(type).append("-----\n");
        int index = 0;
        while (index < key.length()) {
            formattedKey.append(key, index, Math.min(index + 64, key.length())).append("\n");
            index += 64;
        }
        formattedKey.append("-----END ").append(type).append("-----\n");
        return formattedKey.toString();
    }
}