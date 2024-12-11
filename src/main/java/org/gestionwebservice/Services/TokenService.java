package org.gestionwebservice.Services;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TokenService {

    // Method to load private key for signing
    private static PrivateKey loadPrivateKey(String filePath) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(filePath)))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", ""); // Remove newlines and spaces

        byte[] decodedKey = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    // Method to load public key for verification
    private static PublicKey loadPublicKey(String filePath) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(filePath)))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", ""); // Remove newlines and spaces

        byte[] decodedKey = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    // Generate JWT token
    public static String generateToken(String email, Set<String> roles, String privateKeyPath) throws Exception {
        PrivateKey privateKey = loadPrivateKey(privateKeyPath);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) 
                .claim("groups", roles) //embedds the roles in the token
                .signWith(privateKey, SignatureAlgorithm.RS256) // Sign with private key
                .compact();
    }


    //testing the roles that are being extracted from the token to verify if the admin is there 
    public Set<String> extractRoles(String token, String publicKeyPath) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyPath);
    
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey) // Verify signature with public key
                .build()
                .parseClaimsJws(token)
                .getBody();
    
        // Extract "roles" claim as a Set
        Set<String> roles = new HashSet<>(claims.get("roles", List.class));

        //to see if he is reading the role sor not 
        System.out.println("Token Claims: " + claims); 
        System.out.println("Extracted Roles: " + roles); 
    
        return roles;
    }
    
}
