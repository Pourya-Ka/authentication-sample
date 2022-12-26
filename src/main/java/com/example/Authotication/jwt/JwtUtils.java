package com.example.Authotication.jwt;

import com.example.Authotication.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access_token");
        return createToken(claims, user.getId(), tokenAccessExpirationAfterDays);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh_token");
        String token = createToken(claims, user.getId(), tokenRefreshExpirationAfterDays);
        return token;
    }


    private String createToken(Map<String, Object> claims, Long id, int expiration) {
        return Jwts.builder().setClaims(claims)
                .setSubject(String.valueOf(id))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000 * expiration))
                .setId(uuid.toString())
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();

    }

    public void validateToken(String token, User user) {

        SigningKeyResolver signingKeyResolver = new SigningKeyResolverAdapter() {
            @Override
            public byte[] resolveSigningKeyBytes(JwsHeader header, Claims claims) {
                return TextCodec.BASE64.decode(secretKey);
            }
        };

        Jws<Claims> jws = Jwts.parser()
                .setSigningKeyResolver(signingKeyResolver)
                .parseClaimsJws(token);

        if (!
                (jws.getBody().get("type").toString().equals("refresh_token") &&
                        Long.valueOf(extractClaim(token, Claims::getSubject)).equals(user.getId()) && !isTokenExpired(token))
        )
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token is invalidate");
    }

    public void findByID(String token) {
        if (REFRESH_ID_LIST
                .stream()
                .filter(u -> u.equalsIgnoreCase(extractClaim(token, Claims::getId)))
                .findAny()
                .orElse("") != ""
        ) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "this token used!");
        }
        REFRESH_ID_LIST.add(extractClaim(token, Claims::getId));
    }
}
