package KalendarUspomena.Util;

import KalendarUspomena.DTO.Exceptions.ErrorMessage;
import KalendarUspomena.Model.Korisnik;
import KalendarUspomena.Repository.KorisnikRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class JwtUtil {
  private static final String SECRET_KEY = "secureSecretKeyForJwtGenerationabcdefghijklmikasnansfansfiansfonasofosn";
  private static final long ACCESS_TOKEN_VALIDITY = 15 * 60 * 1000; // 15 minuta
  private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 dana
  private final KorisnikRepository korisnikRepository;

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

  public Korisnik extractUser(String token) throws Exception {
    try {
      String username = extractUsername(token);
      Korisnik korisnik = korisnikRepository.findByUsername(username);
      return korisnik;
    }catch (ExpiredJwtException e){
      throw new IllegalArgumentException(new ErrorMessage("Prosleđeni token je istekao!").toString());
    }catch (UnsupportedJwtException e){
      throw new IllegalArgumentException(new ErrorMessage("Token koristi nepodržan algoritam ili format!").toString());
    }catch (MalformedJwtException e){
      throw new IllegalArgumentException(new ErrorMessage("Token je loše formatiran ili korumpiran!").toString());
    }catch (SignatureException e){
      throw new IllegalArgumentException(new ErrorMessage("Potpis na tokenu se ne poklapa sa potpisom servera!").toString());
    }catch (IllegalArgumentException e){
      throw new IllegalArgumentException(new ErrorMessage("Nevažeći token!").toString());
    }catch (NullPointerException e){
      throw new IllegalArgumentException(new ErrorMessage("Refresh token nije prosleđen!").toString());
    }catch (Exception e){
      throw new Exception(new ErrorMessage("Greška na serveru!").toString());
    }
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
