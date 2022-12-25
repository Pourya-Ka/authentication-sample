package com.example.Authotication.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Getter
@Setter
@ConfigurationProperties(prefix = "application.jwt")
@Component
public class JwtUtils {

    private String secretKey;
    private int tokenAccessExpirationAfterDays;
    private int tokenRefreshExpirationAfterDays;
    static int ID_NUMBER = 0;

    static private List<String> REFRESH_ID_LIST = new ArrayList<>();

    UUID uuid = UUID.randomUUID();


    public String extractUsername(String token) {

        return extractClaim(token, Claims::getSubject);
    }


    public Date extractExpiration(String token) {

        return extractClaim(token, Claims::getExpiration);
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access_token");
        return createToken(claims, userDetails.getUsername(), tokenAccessExpirationAfterDays);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh_token");
        String token = createToken(claims, userDetails.getUsername(), tokenRefreshExpirationAfterDays);
        return token;
    }


    private String createToken(Map<String, Object> claims, String subject, int expiration) {
        ID_NUMBER++;
        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000 * expiration))
                .setId(uuid.toString())
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();

    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        SigningKeyResolver signingKeyResolver = new SigningKeyResolverAdapter() {
            @Override
            public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
                return TextCodec.BASE64.decode(secretKey);
            }
        };

        Jws<Claims> jws = Jwts.parser()
                .setSigningKeyResolver(signingKeyResolver)
                .parseClaimsJws(token);

        if (jws.getBody().get("type").toString().equals("refresh_token") &&
                (username.equals(userDetails.getUsername()) && !isTokenExpired(token))
        )
            return true;

        return false;
    }

    public boolean findByID(String token) {
        String result = "A";
        result = REFRESH_ID_LIST
                .stream()
                .filter(u -> u.equalsIgnoreCase(extractClaim(token, Claims::getId)))
                .findFirst()
                .orElse("");
        if (result.equals(extractClaim(token, Claims::getId)))
            return false;


        REFRESH_ID_LIST.add(extractClaim(token, Claims::getId));
        return true;
    }
}
