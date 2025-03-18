package KalendarUspomena.Auth;


import static KalendarUspomena.Util.CustomContainsMatcher.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import KalendarUspomena.DTO.Auth.LoginRequest;
import KalendarUspomena.DTO.Auth.RefreshRequest;
import KalendarUspomena.DTO.Auth.RegisterRequest;
import KalendarUspomena.Model.Korisnik;
import KalendarUspomena.Model.Uloga;
import KalendarUspomena.Repository.KorisnikRepository;
import KalendarUspomena.Repository.UlogaRepository;
import KalendarUspomena.Util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")

public class AuthIntegrationTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  private KorisnikRepository korisnikRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private UlogaRepository ulogaRepository;
  @Autowired
  private JwtUtil jwtUtil;

  @Nested
  class RegisterTests{

    static RegisterRequest rr;
    static Korisnik k;
    @BeforeEach
    void setUpRegisterRequest(){
      rr = new RegisterRequest("Test","TestTest","Test@Test","Test","Test");
      k.setUloga(ulogaRepository.findByNaziv("USER")
          .orElseGet(() -> {
            Uloga newRole = new Uloga();
            newRole.setNaziv("USER");
            return ulogaRepository.save(newRole);
          }));
      k.setPassword(passwordEncoder.encode("TestTest"));
      korisnikRepository.deleteAll();
      korisnikRepository.save(k);
    }
    @BeforeAll
    static void setUpKorisnik(){
      k = Korisnik.builder()
          .username("Test1")
          .email("Test1@Test")
          .ime("Test")
          .prezime("Test")
          .dateCreated(new Date(System.currentTimeMillis()))
          .build();
    }
    //Success 200
    @Test
    void testRegisterUserSucces() throws Exception {
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Registracija uspešna!"));

      Korisnik korisnik = korisnikRepository.findByEmail("Test@Test");
      assertNotNull(korisnik, "User should be saved in the database");
      assertEquals("Test@Test",korisnik.getEmail(),"User email should be the same as the one registered");
      assertEquals("Test",korisnik.getUsername(),"Username should be the same as the one registered");
      assertEquals("Test",korisnik.getIme(),"User first name should be the same as the one registered");
      assertEquals("Test",korisnik.getPrezime(),"User last name should be the same as the one registered");
      assertTrue(passwordEncoder.matches("TestTest",korisnik.getPassword()),"User password should be the same as the one registered");
    }
    //ERROR 400
    @Test
    void testRegisterUserExistingUsername() throws Exception {
      //given
      rr.setUsername("Test1");
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Korisničko ime već postoji!"));
    }
    @Test
    void testRegisterUserExistingEmail() throws Exception {
      //given
      rr.setEmail("Test1@Test");
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Email već postoji!"));
    }
    @Test
    void testRegisterUserNoFirstName() throws Exception {
      //given
      rr.setIme(null);
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđeno ime korisnika!"));
    }
    @Test
    void testRegisterUserNoLastName() throws Exception {
      //given
      rr.setPrezime(null);
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđeno prezime korisnika!"));
    }
    @Test
    void testRegisterUserNoPassword() throws Exception {
      //given
      rr.setPassword(null);
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđena lozinka!"));
    }
    @Test
    void testRegisterUserNoUsername() throws Exception {
      //given
      rr.setUsername(null);
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđeno korisničko ime!"));
    }
    @Test
    void testRegisterUserNoEmail() throws Exception {
      //given
      rr.setEmail(null);
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđen email!"));
    }
    @Test
    void testRegisterUserNoEmailAndUsername() throws Exception {
      //given
      rr.setUsername(null);
      rr.setEmail(null);
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđeno korisničko ime!"))
          .andExpect(containsString("Nije prosleđen email!"));
    }
    @Test
    void testRegisterUserInvalidPassword() throws Exception {
      //given
      rr.setPassword("p");
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Lozinka nije prosleđena u odgovarajućem formatu!"));
    }
    @Test
    void testRegisterUserInvalidFirstName() throws Exception {
      //given
      rr.setIme(" ");
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Ime nije prosleđeno u odgovarajućem foramtu!"));
    }
    @Test
    void testRegisterUserInvalidLastName() throws Exception {
      //given
      rr.setPrezime(" ");
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Prezime nije prosleđeno u odgovarajućem foramtu!"));
    }
    @Test
    void testRegisterUserInvalidUsername() throws Exception {
      //given
      rr.setUsername(" ");
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Korisničko ime nije prosleđeno u odgovarajućem foramtu!"));
    }
    @Test
    void testRegisterUserInvalidEmail() throws Exception {
      //given
      rr.setEmail("asd");
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Email nije prosleđen u odgovarajućem foramtu!"));
    }
  }
  @Nested
  class LoginTests{
    static Korisnik k;
    static LoginRequest lr;
    @BeforeEach
    void setUpRegisterRequest(){
      lr = new LoginRequest("Test","TestTest");
      k.setUloga(ulogaRepository.findByNaziv("USER")
          .orElseGet(() -> {
            Uloga newRole = new Uloga();
            newRole.setNaziv("USER");
            return ulogaRepository.save(newRole);
          }));
      k.setPassword(passwordEncoder.encode("TestTest"));
      korisnikRepository.deleteAll();
      korisnikRepository.save(k);
    }
    @BeforeAll
    static void setUpKorisnik(){
      k = Korisnik.builder()
          .username("Test")
          .email("Test@Test")
          .ime("Test")
          .prezime("Test")
          .dateCreated(new Date(System.currentTimeMillis()))
          .build();
    }
    //SUCCESS 200
    @Test
    void testLoginUserSuccess() throws Exception{
      //when then
      MvcResult result = mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(lr)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andReturn();
      JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
      String accesToken = jsonNode.get("accesToken").asText()
          ,refreshToken = jsonNode.get("refreshToken").asText();
      assertTrue(jwtUtil.isTokenValid(accesToken,"Test"),"Invalid access token!");
      assertTrue(jwtUtil.isTokenValid(refreshToken,"Test"),"Invalid refresh token!");
    }
    //Bad credentials 401
    @Test
    void testLoginUserBadCredentials() throws Exception {
      //given
      lr.setPassword("TestTest123");
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(lr)))
          .andExpect(status().is(401))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Prosleđena lozinka je neispravna!"));
    }
    //Bad request 400
    @Test
    void testLoginUserNoIdentificator() throws Exception {
      //given
      lr.setIdentificator(null);
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(lr)))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđen identifikator!"));
    }
    @Test
    void testLoginUserNoPassword() throws Exception {
      //given
      lr.setPassword(null);
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(lr)))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđena lozinka!"));
    }
    @Test
    void testLoginUserInvalidIdentificator() throws Exception {
      //given
      lr.setIdentificator("Test1");
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(lr)))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Uneto korisničko ime(ili email) nije validno!"));
    }
    @Test
    void testLoginUserInvalidPassword() throws Exception {
      //given
      lr.setPassword("Test");
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(lr)))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Lozinka nije prosleđena u odgovarajućem formatu!"));
    }
  }
  @Nested
  class RefreshTests{
    static Korisnik k;
    static RefreshRequest rr;
    @BeforeEach
    void setUpRegisterRequest(){
      k.setUloga(ulogaRepository.findByNaziv("USER")
          .orElseGet(() -> {
            Uloga newRole = new Uloga();
            newRole.setNaziv("USER");
            return ulogaRepository.save(newRole);
          }));
      k.setPassword(passwordEncoder.encode("TestTest"));
      korisnikRepository.deleteAll();
      korisnikRepository.save(k);
      rr = new RefreshRequest(jwtUtil.generateRefreshToken("Test"));
    }
    @BeforeAll
    static void setUpKorisnik(){
      k = Korisnik.builder()
          .username("Test")
          .email("Test@Test")
          .ime("Test")
          .prezime("Test")
          .dateCreated(new Date(System.currentTimeMillis()))
          .build();
    }

    //SUCCES 200
    @Test
    void testRefreshTokenSuccess() throws Exception{
      //when then
      MvcResult result = mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn();
      JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
      String accesToken = jsonNode.get("accesToken").asText()
          ,refreshToken = jsonNode.get("refreshToken").asText();
      assertTrue(jwtUtil.isTokenValid(accesToken,"Test"),"Invalid access token!");
      assertTrue(jwtUtil.isTokenValid(refreshToken,"Test"),"Invalid refresh token!");
    }
    //Bad request 400
    @Test
    void testRefreshTokenInvalidToken() throws Exception {
      //given
      rr = new RefreshRequest(jwtUtil.generateAccessToken("Test"));
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nevažeći token!"));
    }
    @Test
    void testRefreshTokenExpiredJwt() throws Exception {
      //given
      String expiredJwt=Jwts.builder()
          .claim("Type","Refresh")
          .setSubject("Test")
          .setIssuedAt(new Date())
          .setExpiration(new Date(System.currentTimeMillis() - 1000))
          .signWith(jwtUtil.getSigningKey(), SignatureAlgorithm.HS256)
          .compact();
      rr = new RefreshRequest(expiredJwt);
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Prosleđeni token je istekao!"));
    }
    @Test
    void testRefreshTokenUnsupportedJwt() throws Exception {
      //given
      String unsupportedToken = "eyJhbGciOiJub25lIiwidHlwIjoiSldUIn0.eyJzdWIiOiJUZXN0In0.";
      rr = new RefreshRequest(unsupportedToken);
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Token koristi nepodržan algoritam ili format!"));
    }
    @Test
    void testRefreshTokenMalformedJwt() throws Exception {
      //given
      rr = new RefreshRequest("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidpayload");
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Token je loše formatiran ili korumpiran!"));
    }
    @Test
    void testRefreshTokenWrongSignatureJwt() throws Exception {
      //given
      String wrongSignatureToken = Jwts.builder()
          .claim("Type","Refresh")
          .setSubject("Test")
          .setIssuedAt(new Date())
          .setExpiration(new Date(System.currentTimeMillis()+5000))
          .signWith(Keys.hmacShaKeyFor(("amsoifmaoifnasnfoansfonasfonasoifnaoisfbnoiasbnfoiabi").getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
          .compact();
      rr = new RefreshRequest(wrongSignatureToken);
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(rr)))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Potpis na tokenu se ne poklapa sa potpisom servera!"));
    }
    @Test
    void testRefreshTokenNoJwt() throws Exception {
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RefreshRequest(null))))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Refresh token nije prosleđen!"));
    }
  }
}
