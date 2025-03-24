package KalendarUspomena.Auth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import KalendarUspomena.DTO.Auth.AuthResponse;
import KalendarUspomena.DTO.Auth.LoginRequest;
import KalendarUspomena.DTO.Auth.RefreshRequest;
import KalendarUspomena.DTO.Auth.RegisterRequest;
import KalendarUspomena.Model.Korisnik;
import KalendarUspomena.Model.Uloga;
import KalendarUspomena.Repository.KorisnikRepository;
import KalendarUspomena.Repository.UlogaRepository;
import KalendarUspomena.Service.AuthService;
import KalendarUspomena.Util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.security.SignatureException;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  KorisnikRepository korisnikRepository;
  @Mock
  UlogaRepository ulogaRepository;
  @Mock
  PasswordEncoder passwordEncoder;
  @Mock
  JwtUtil jwtUtil;
  @Mock
  AuthenticationManager authenticationManager;

  @InjectMocks
  AuthService authService;
  @Nested
  class RegisterTests {
    //USPESAN TEST REGISTER USER
    @Test
    void testRegisterUserSuccess() {
      //Given
      Korisnik a = Korisnik.builder()
          .id(1L)
          .ime("Test")
          .prezime("Test")
          .email("test@gmail.com")
          .password("TestTest")
          .username("Test")
          .build();
      RegisterRequest rr = RegisterRequest.builder()
          .ime("Test")
          .prezime("Test")
          .email("test@gmail.com")
          .password("TestTest")
          .username("Test")
          .build();


      given(korisnikRepository.findByUsername(a.getUsername())).willReturn(null);
      given(korisnikRepository.findByEmail(a.getEmail())).willReturn(null);
      given(ulogaRepository.findByNaziv("USER")).willReturn(Optional.of(Uloga.builder().idUloga(1L).naziv("USER").build()));
      given(korisnikRepository.save(any(Korisnik.class))).willReturn(a);
      given(passwordEncoder.encode(rr.getPassword())).willReturn(rr.getPassword());
      //When, Then
      assertThatCode(() -> authService.registerUser(rr)).doesNotThrowAnyException();
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
      verify(passwordEncoder, times(1)).encode(rr.getPassword());
      verify(korisnikRepository, times(1)).save(any(Korisnik.class));

    }

    //TESTOVI U KOJIMA JE UNET PODATAK POSTOJEĆI U BAZI
    @Test
    void testRegisterUserUsernameExists() {
      //Given
      given(korisnikRepository.findByUsername(anyString())).willReturn(Korisnik.builder().build());
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .prezime("Test")
          .email("test@gmail.com")
          .password("TestTest")
          .username("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Korisničko ime već postoji!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testRegisterUserEmailExists() {
      //Given
      given(korisnikRepository.findByUsername(anyString())).willReturn(null);
      given(korisnikRepository.findByEmail(anyString())).willReturn(Korisnik.builder().build());
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .prezime("Test")
          .email("test@gmail.com")
          .password("TestTest")
          .username("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Email već postoji!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

    //Testovi u kojima fale podaci REGISTER USER
    @Test
    void testRegisterUserNoFirstName() {
      //Given
      given(korisnikRepository.findByUsername(anyString())).willReturn(null);
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .prezime("Test")
          .email("test@gmail.com")
          .password("TestTest")
          .username("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Nije prosleđeno ime korisnika!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testRegisterUserNoLastName() {
      //Given
      given(korisnikRepository.findByUsername(anyString())).willReturn(null);
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .email("test@gmail.com")
          .password("TestTest")
          .username("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Nije prosleđeno prezime korisnika!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testRegisterUserNoPassword() {
      //Given
      given(korisnikRepository.findByUsername(anyString())).willReturn(null);
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .email("test@gmail.com")
          .prezime("TestTest")
          .username("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Nije prosleđena lozinka!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testRegisterUserNoUsername() {
      //Given
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .email("test@gmail.com")
          .password("TestTest")
          .prezime("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Nije prosleđeno korisničko ime!");
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testRegisterUserNoEmail() {
      //Given
      given(korisnikRepository.findByUsername(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .prezime("Test")
          .password("TestTest")
          .username("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Nije prosleđen email!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
    }

    @Test
    void testRegisterUserNoEmailAndUsername() {
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .prezime("Test")
          .password("TestTest")
          .build()));
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Nije prosleđen email!");
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Nije prosleđeno korisničko ime!");
    }

    //TESTOVI GDE UNETI PODACI NISU VALIDNI REGISTER USER
    @Test
    void testRegisterUserInvalidPassword() {
      //Given
      given(korisnikRepository.findByUsername(anyString())).willReturn(null);
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .email("test@gmail.com")
          .prezime("TestTest")
          .password("a")
          .username("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Lozinka nije prosleđena u odgovarajućem formatu!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testRegisterUserInvalidFirstName() {
      //Given
      given(korisnikRepository.findByUsername(anyString())).willReturn(null);
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test ")
          .email("test@gmail.com")
          .prezime("TestTest")
          .password("aasdasdasd")
          .username("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Ime nije prosleđeno u odgovarajućem foramtu!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testRegisterUserInvalidLastName() {
      //Given
      given(korisnikRepository.findByUsername(anyString())).willReturn(null);
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .email("test@gmail.com")
          .prezime("Test Test")
          .password("aaaaaaa")
          .username("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Prezime nije prosleđeno u odgovarajućem foramtu!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testRegisterUserInvalidUsername() {
      //Given
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .email("test@gmail.com")
          .prezime("TestTest")
          .password("aasdasdasd")
          .username("Te")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Korisničko ime nije prosleđeno u odgovarajućem foramtu!");
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void testRegisterUserInvalidEmail() {
      //Given
      given(korisnikRepository.findByUsername(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.registerUser(RegisterRequest.builder()
          .ime("Test")
          .email("testgmail.com")
          .prezime("TestTest")
          .password("aaaaaaa")
          .username("Test")
          .build()));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Email nije prosleđen u odgovarajućem foramtu!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
    }
  }
  @Nested
  class LoginTests {
    //USPESAN TEST Login USER
    @Test
    void testLoginUserUsernameSuccess(){
      //given
      LoginRequest loginRequest = LoginRequest.builder()
          .identificator("Test").password("TestTest").build();
      given(jwtUtil.generateAccessToken("Test")).willReturn("Test");
      given(jwtUtil.generateRefreshToken("Test")).willReturn("Test");
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      given(korisnikRepository.findByUsername(anyString())).willReturn(
          Korisnik.builder().username("Test").build());
      given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .willReturn(null);
      //when
      AuthResponse res = authService.loginUser(loginRequest);
      //then
      assertThat(res.getAccesToken()).isEqualTo("Test");
      assertThat(res.getRefreshToken()).isEqualTo("Test");

      verify(authenticationManager, times(1)).authenticate(
          any(UsernamePasswordAuthenticationToken.class));
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
      verify(jwtUtil, times(1)).generateAccessToken(anyString());
      verify(jwtUtil, times(1)).generateRefreshToken(anyString());
    }
    @Test
    void testLoginUserEmailSuccess() {
      //given
      LoginRequest loginRequest = LoginRequest.builder()
          .identificator("Test@gmail.com").password("TestTest").build();
      given(jwtUtil.generateAccessToken("Test")).willReturn("Test");
      given(jwtUtil.generateRefreshToken("Test")).willReturn("Test");
      given(korisnikRepository.findByEmail(anyString())).willReturn(Korisnik.builder().username("Test").build());
      given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .willReturn(null);
      //when
      AuthResponse res = authService.loginUser(loginRequest);
      //then
      assertThat(res.getAccesToken()).isEqualTo("Test");
      assertThat(res.getRefreshToken()).isEqualTo("Test");

      verify(authenticationManager, times(1)).authenticate(
          any(UsernamePasswordAuthenticationToken.class));
      verify(korisnikRepository, times(1)).findByEmail(anyString());
      verify(jwtUtil, times(1)).generateAccessToken(anyString());
      verify(jwtUtil, times(1)).generateRefreshToken(anyString());
    }

    //TESTOVI U KOJIMA NISU UNETI NEKI PODACI

    @Test
    void testLoginUserNoPassword()  {
      //Given
      LoginRequest loginRequest = LoginRequest.builder()
          .identificator("Test@gmail.com").build();
      given(korisnikRepository.findByUsername(anyString())).willReturn(Korisnik.builder().username("Test").build());
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.loginUser(loginRequest));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Nije prosleđena lozinka!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }
    @Test
    void testLoginUserNoIdentificator(){
      //Given
      LoginRequest loginRequest = LoginRequest.builder()
          .password("Test@gmail.com").build();
      //When
      Throwable t = catchThrowable(() -> authService.loginUser(loginRequest));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Nije prosleđen identifikator!");
    }

    //TESTOVI GDE UNETI PODACI NISU VALIDNI
    @Test
    void testLoginUserInvalidPassword() {
      //Given
      LoginRequest loginRequest = LoginRequest.builder()
          .identificator("Test@gmail.com").password("asd").build();
      given(korisnikRepository.findByUsername(anyString())).willReturn(Korisnik.builder().username("Test").build());
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.loginUser(loginRequest));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Lozinka nije prosleđena u odgovarajućem formatu!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }
    @Test
    void testLoginUserWrongPassword() {
      //Given
      LoginRequest loginRequest = LoginRequest.builder()
          .identificator("Test@gmail.com").password("asdasdasd").build();
      given(korisnikRepository.findByUsername(anyString())).willReturn(Korisnik.builder().username("Test").build());
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).willThrow(new BadCredentialsException("wrong password"));
      //When
      Throwable t = catchThrowable(() -> authService.loginUser(loginRequest));
      //Then
      assertThat(t).isInstanceOf(BadCredentialsException.class)
          .hasMessageContaining("Prosleđena lozinka je neispravna!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
      verify(authenticationManager,times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
    @Test
    void testLoginUserInvalidIdentificator() {
      //Given
      LoginRequest loginRequest = LoginRequest.builder()
          .identificator("Test@gmail.com").password("asdasdasda").build();
      given(korisnikRepository.findByUsername(anyString())).willReturn(null);
      given(korisnikRepository.findByEmail(anyString())).willReturn(null);
      //When
      Throwable t = catchThrowable(() -> authService.loginUser(loginRequest));
      //Then
      assertThat(t).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Uneto korisničko ime(ili email) nije validno!");
      verify(korisnikRepository, times(1)).findByUsername(anyString());
      verify(korisnikRepository, times(1)).findByEmail(anyString());
    }

  }
  @Nested
  class RefreshTests{

    //USPESAN REFRESH TOKEN
    @Test
    void testRefreshTokenSucces() throws Exception {
      //given
      RefreshRequest rr = new RefreshRequest("Test");
      given(jwtUtil.extractUsername(rr.refreshToken())).willReturn("Test");
      given(jwtUtil.isTokenValid(rr.refreshToken(),"Test")).willReturn(true);
      given(jwtUtil.generateAccessToken(rr.refreshToken())).willReturn("Test");
      //when
      AuthResponse ar = authService.refreshUsersToken(rr);
      //then
      assertThat(ar).isNotNull();
      assertThat(ar.getRefreshToken()).isEqualTo("Test");
      assertThat(ar.getAccesToken()).isEqualTo("Test");
      verify(jwtUtil,times(1)).extractUsername("Test");
      verify(jwtUtil,times(1)).isTokenValid("Test","Test");
      verify(jwtUtil,times(1)).generateAccessToken("Test");
    }
    //NIJE PROSLEĐEN TOKEN
    @Test
    void testNoTokenProvided(){
      //when
      Throwable t = catchThrowable(()-> authService.refreshUsersToken(new RefreshRequest(null)));
      //then
      assertThat(t).isInstanceOf(IllegalArgumentException.class);
      assertThat(t.getMessage()).contains("Refresh token nije prosleđen!");
    }
    //NIJE VALIDAN TOKEN
    @Test
    void testExpiredToken()  {
      //given
      RefreshRequest rr = new RefreshRequest("Test");
      given(jwtUtil.extractUsername(rr.refreshToken())).willThrow(new ExpiredJwtException(new DefaultHeader(),
          new DefaultClaims(),"Istekao jwt"));
      //when
      Throwable t = catchThrowable(()-> authService.refreshUsersToken(rr));
      //then
      assertThat(t).isInstanceOf(IllegalArgumentException.class);
      assertThat(t.getMessage()).contains("Prosleđeni token je istekao!");
      verify(jwtUtil,times(1)).extractUsername("Test");
    }
    @Test
    void testUnsuportedToken()  {
      //given
      RefreshRequest rr = new RefreshRequest("Test");
      given(jwtUtil.extractUsername(rr.refreshToken())).willThrow(new UnsupportedJwtException(""));
      //when
      Throwable t = catchThrowable(()->authService.refreshUsersToken(rr));
      //then
      assertThat(t).isInstanceOf(IllegalArgumentException.class);
      assertThat(t.getMessage()).contains("Token koristi nepodržan algoritam ili format!");
      verify(jwtUtil,times(1)).extractUsername("Test");
    }
    @Test
    void testMalformedToken()  {
      //given
      RefreshRequest rr = new RefreshRequest("Test");
      given(jwtUtil.extractUsername(rr.refreshToken())).willThrow(new MalformedJwtException(""));
      //when
      Throwable t = catchThrowable(()->authService.refreshUsersToken(rr));
      //then
      assertThat(t).isInstanceOf(IllegalArgumentException.class);
      assertThat(t.getMessage()).contains("Token je loše formatiran ili korumpiran!");
      verify(jwtUtil,times(1)).extractUsername("Test");
    }
    @Test
    void testWrongSignature()  {
      //given
      RefreshRequest rr = new RefreshRequest("Test");
      given(jwtUtil.extractUsername(rr.refreshToken())).willThrow(new SignatureException(""));
      //when
      Throwable t = catchThrowable(()->authService.refreshUsersToken(rr));
      //then
      assertThat(t).isInstanceOf(IllegalArgumentException.class);
      assertThat(t.getMessage()).contains("Potpis na tokenu se ne poklapa sa potpisom servera!");
      verify(jwtUtil,times(1)).extractUsername("Test");
    }
    @Test
    void testBadToken()  {
      //given
      RefreshRequest rr = new RefreshRequest("Test");
      given(jwtUtil.extractUsername(rr.refreshToken())).willThrow(new IllegalArgumentException(""));
      //when
      Throwable t = catchThrowable(()->authService.refreshUsersToken(rr));
      //then
      assertThat(t).isInstanceOf(IllegalArgumentException.class);
      assertThat(t.getMessage()).contains("Nevažeći token!");
      verify(jwtUtil,times(1)).extractUsername("Test");
    }
    @Test
    void testServerError()  {
      RefreshRequest rr = new RefreshRequest("Test");
      given(jwtUtil.extractUsername(rr.refreshToken())).willThrow(new RuntimeException());
      //when
      Throwable t = catchThrowable(()->authService.refreshUsersToken(rr));
      //then
      assertThat(t).isInstanceOf(Exception.class);
      assertThat(t.getMessage()).contains("Greška na serveru!");
      verify(jwtUtil,times(1)).extractUsername("Test");
    }
  }

}