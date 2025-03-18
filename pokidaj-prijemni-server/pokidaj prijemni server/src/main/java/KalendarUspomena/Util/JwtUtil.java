package KalendarUspomena.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  private static final String SECRET_KEY = "secureSecretKeyForJwtGenerationabcdefghijklmikasnansfansfiansfonasofosn";
  private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 minuta
  private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 dana

  public Key getSigningKey() {
    return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
  }

  public String generateAccessToken(String username) {
    return Jwts.builder()
        .claim("Type","Access")
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(String username) {
    return Jwts.builder()
        .claim("Type","Refresh")
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token){
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean isTokenValid(String token, String username) {
    String extractedUsername = extractUsername(token);
    return extractedUsername.equals(username) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    Date expiration = Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getExpiration();
    return expiration.before(new Date());
  }

  public void CheckIsAccesToken(String token){
    if(!tokenType(token).equals("Access"))
      throw new JwtException("Invalid jwt token.");
  }
  public void CheckIsRefreshToken(String token){
    if(!tokenType(token).equals("Refresh"))
      throw new IllegalArgumentException("Invalid refresh token!");
  }

  private String tokenType(String token) {
    try {
      Claims c = Jwts.parserBuilder()
          .setSigningKey(getSigningKey())
          .build()
          .parseClaimsJws(token)
          .getBody();
      if (c.containsKey("Type")) {
        return c.get("Type", String.class);
      } else throw new JwtException("Invalid jwt token.");
    } catch (Exception e) {
      throw new JwtException("Invalid jwt token: "+e.getMessage());
    }
  }
}
