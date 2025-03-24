package KalendarUspomena.Service;


import KalendarUspomena.DTO.Exceptions.ErrorMessage;
import KalendarUspomena.DTO.Auth.LoginRequest;
import KalendarUspomena.DTO.Auth.AuthResponse;
import KalendarUspomena.DTO.Auth.RefreshRequest;
import KalendarUspomena.DTO.Auth.RegisterRequest;
import KalendarUspomena.Model.Korisnik;
import KalendarUspomena.Model.Uloga;
import KalendarUspomena.Repository.KorisnikRepository;
import KalendarUspomena.Repository.UlogaRepository;
import KalendarUspomena.Util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.transaction.Transactional;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
  private final KorisnikRepository korisnikRepository;
  private final UlogaRepository ulogaRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;


  private void validateRegisterRequest(RegisterRequest registerRequest) throws IllegalArgumentException {
    ErrorMessage e = new ErrorMessage();
    if(registerRequest.getEmail()==null)
      e.add("Nije prosleđen email!");
    else if(!registerRequest.getEmail().contains("@"))
      e.add("Email nije prosleđen u odgovarajućem foramtu!");
    else{if (korisnikRepository.findByEmail(registerRequest.getEmail()) != null)
      e.add("Email već postoji!");}
    if(registerRequest.getUsername()==null)
      e.add("Nije prosleđeno korisničko ime!");
    else if (registerRequest.getUsername().length()<3)
      e.add("Korisničko ime nije prosleđeno u odgovarajućem foramtu!");
    else {
    if (korisnikRepository.findByUsername(registerRequest.getUsername()) != null)
      e.add("Korisničko ime već postoji!");}

    // Provera da li su ime, prezime i lozinka prosleđeni
    if (registerRequest.getIme() == null || registerRequest.getIme().isEmpty())
      e.add("Nije prosleđeno ime korisnika!");
    else if (registerRequest.getIme().contains(" "))
      e.add("Ime nije prosleđeno u odgovarajućem foramtu!");
    if (registerRequest.getPrezime() == null || registerRequest.getPrezime().isEmpty())
      e.add("Nije prosleđeno prezime korisnika!");
    else if (registerRequest.getPrezime().contains(" "))
      e.add("Prezime nije prosleđeno u odgovarajućem foramtu!");
    if(registerRequest.getPassword() == null)
      e.add("Nije prosleđena lozinka!");
    else if (registerRequest.getPassword().length() < 6)//ovde treba da se napravi metoda za validaciju šifre(pravila po kojih korisnik mora da se drzi kada unosi sifru)
      e.add("Lozinka nije prosleđena u odgovarajućem formatu!");
    if(!e.isEmpty())
      throw new IllegalArgumentException(e.toString());
  }
  private Uloga getRoleUser(){
    return ulogaRepository.findByNaziv("USER")
        .orElseGet(() -> {
          Uloga newRole = new Uloga();
          newRole.setNaziv("USER");
          return ulogaRepository.save(newRole);
        });
  }
  @Transactional
  public void registerUser(RegisterRequest registerRequest){
    validateRegisterRequest(registerRequest);
    // Dohvatanje ili kreiranje uloge
    Uloga userRole = getRoleUser();
    // Kreiranje i čuvanje korisnika
    Korisnik korisnik = Korisnik.builder()
        .username(registerRequest.getUsername())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .email(registerRequest.getEmail())
        .ime(registerRequest.getIme())
        .prezime(registerRequest.getPrezime())
        .uloga(userRole)
        .dateCreated(new Date(System.currentTimeMillis()))
        .build();
    korisnikRepository.save(korisnik);
  }

  private void validateLoginRequest(LoginRequest loginRequest) {
    ErrorMessage e = new ErrorMessage();
    if(loginRequest.getIdentificator()==null)
      e.add("Nije prosleđen identifikator!");
    else{
      Korisnik k = korisnikRepository.findByEmail(loginRequest.getIdentificator());
      if(k==null)
        k = korisnikRepository.findByUsername(loginRequest.getIdentificator());
      if(k==null)
        e.add("Uneto korisničko ime(ili email) nije validno!");
      else loginRequest.setIdentificator(k.getUsername());
    }
    if(loginRequest.getPassword()==null)
      e.add("Nije prosleđena lozinka!");
    else if (loginRequest.getPassword().length()<6)
      e.add("Lozinka nije prosleđena u odgovarajućem formatu!");

    if(!e.isEmpty())
      throw new IllegalArgumentException(e.toString());
  }
  public AuthResponse loginUser(LoginRequest loginRequest){
    validateLoginRequest(loginRequest);
    try{
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getIdentificator(),
            loginRequest.getPassword()));}
    catch (AuthenticationException e){
      throw new BadCredentialsException(new ErrorMessage("Prosleđena lozinka je neispravna!").toString());
    }
    String accessToken = jwtUtil.generateAccessToken(loginRequest.getIdentificator());
    String refreshToken = jwtUtil.generateRefreshToken(loginRequest.getIdentificator());
    return new AuthResponse(accessToken,refreshToken);
  }
  public AuthResponse refreshUsersToken(RefreshRequest refreshRequest) throws Exception{
    if(refreshRequest.refreshToken()==null)
      throw new IllegalArgumentException("Refresh token nije prosleđen!");
    try {
      String username = jwtUtil.extractUsername(refreshRequest.refreshToken());
      jwtUtil.CheckIsRefreshToken(refreshRequest.refreshToken());
      if (jwtUtil.isTokenValid(refreshRequest.refreshToken(), username)) {
        String accessToken = jwtUtil.generateAccessToken(username);
        return new AuthResponse(accessToken, refreshRequest.refreshToken());
      } else {
        throw new IllegalArgumentException("Invalid refresh token!");
      }
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
}
