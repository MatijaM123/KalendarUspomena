package KalendarUspomena.Auth;


import static KalendarUspomena.Util.CustomContainsMatcher.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import KalendarUspomena.DTO.Auth.AuthResponse;
import KalendarUspomena.DTO.Auth.LoginRequest;
import KalendarUspomena.DTO.Auth.RefreshRequest;
import KalendarUspomena.DTO.Auth.RegisterRequest;
import KalendarUspomena.Service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  AuthService authService;
  @Nested
  class RegisterTests{
    //SUCCESS 200
    @Test
    void testRegisterUserSucces() throws Exception {
      //given
      doNothing().when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
          .accept(MediaType.APPLICATION_JSON)
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Registracija uspešna!"));
    }
    //ERROR 500
    @Test
    void testRegisterUserServerError() throws Exception {
      //given
      doThrow(new RuntimeException()).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().is(500))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Greška na serveru!"));
    }
    //ERROR 400
    @Test
    void testRegisterUserExistingUsername() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Korisničko ime već postoji!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Korisničko ime već postoji!"));
    }
    @Test
    void testRegisterUserExistingEmail() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Email već postoji!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Email već postoji!"));
    }
    @Test
    void testRegisterUserNoFirstName() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Nije prosleđeno ime korisnika!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđeno ime korisnika!"));
    }
    @Test
    void testRegisterUserNoLastName() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Nije prosleđeno prezime korisnika!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđeno prezime korisnika!"));
    }
    @Test
    void testRegisterUserNoPassword() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Nije prosleđena lozinka!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđena lozinka!"));
    }
    @Test
    void testRegisterUserNoUsername() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Nije prosleđeno korisničko ime!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđeno korisničko ime!"));
    }
    @Test
    void testRegisterUserNoEmail() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Nije prosleđen email!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđen email!"));
    }
    @Test
    void testRegisterUserNoEmailAndUsername() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Nije prosleđen email!\",\"Nije prosleđeno korisničko ime!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđeno korisničko ime!"))
          .andExpect(containsString("Nije prosleđen email!"));
    }
    @Test
    void testRegisterUserInvalidPassword() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Lozinka nije prosleđena u odgovarajućem formatu!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Lozinka nije prosleđena u odgovarajućem formatu!"));
    }
    @Test
    void testRegisterUserInvalidFirstName() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Ime nije prosleđeno u odgovarajućem foramtu!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Ime nije prosleđeno u odgovarajućem foramtu!"));
    }
    @Test
    void testRegisterUserInvalidLastName() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Prezime nije prosleđeno u odgovarajućem foramtu!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Prezime nije prosleđeno u odgovarajućem foramtu!"));
    }
    @Test
    void testRegisterUserInvalidUsername() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Korisničko ime nije prosleđeno u odgovarajućem foramtu!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Korisničko ime nije prosleđeno u odgovarajućem foramtu!"));
    }
    @Test
    void testRegisterUserInvalidEmail() throws Exception {
      //given
      doThrow(new IllegalArgumentException("{\"message\" : [\"Email nije prosleđen u odgovarajućem foramtu!\"]}")).when(authService).registerUser(any(RegisterRequest.class));
      //when then
      mockMvc.perform(post("/api/auth/register")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RegisterRequest(null,null,null,null,null))))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Email nije prosleđen u odgovarajućem foramtu!"));
    }
  }

  @Nested
  class LoginTests{
    //SUCCESS 200
    @Test
    void testLoginUserSuccess() throws Exception{
      //given
      when(authService.loginUser(any(LoginRequest.class))).thenReturn(new AuthResponse("Test","Test"));
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new LoginRequest(null,null))))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.accesToken").value("Test"))
          .andExpect(jsonPath("$.refreshToken").value("Test"));
    }
    //SERVER ERROR 500
    @Test
    void testLoginUserServerError() throws Exception {
      //given
      when(authService.loginUser(any(LoginRequest.class))).thenThrow(new RuntimeException());
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new LoginRequest(null,null))))
          .andExpect(status().is(500))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Greška na serveru!"));
    }
    //Bad credentials 401
    @Test
    void testLoginUserBadCredentials() throws Exception {
      //given
      when(authService.loginUser(any(LoginRequest.class))).thenThrow(new BadCredentialsException("{\"message\": [\"Prosleđena lozinka je neispravna!\"]}"));
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new LoginRequest(null,null))))
          .andExpect(status().is(401))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Prosleđena lozinka je neispravna!"));
    }
    //Bad request 400
    @Test
    void testLoginUserNoIdentificator() throws Exception {
      //given
      when(authService.loginUser(any(LoginRequest.class))).thenThrow(new IllegalArgumentException("{\"message\" : [\"Nije prosleđen identifikator!\"]}"));
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new LoginRequest(null,null))))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđen identifikator!"));
    }
    @Test
    void testLoginUserNoPassword() throws Exception {
      //given
      when(authService.loginUser(any(LoginRequest.class))).thenThrow(new IllegalArgumentException("{\"message\" : [\"Nije prosleđena lozinka!\"]}"));
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new LoginRequest(null,null))))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nije prosleđena lozinka!"));
    }
    @Test
    void testLoginUserInvalidIdentificator() throws Exception {
      //given
      when(authService.loginUser(any(LoginRequest.class))).thenThrow(new IllegalArgumentException("{\"message\" : [\"Uneto korisničko ime(ili email) nije validno!\"]}"));
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new LoginRequest(null,null))))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Uneto korisničko ime(ili email) nije validno!"));
    }
    @Test
    void testLoginUserInvalidPassword() throws Exception {
      //given
      when(authService.loginUser(any(LoginRequest.class))).thenThrow(new IllegalArgumentException("{\"message\" : [\"Lozinka nije prosleđena u odgovarajućem formatu!\"]}"));
      //when then
      mockMvc.perform(post("/api/auth/login")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new LoginRequest(null,null))))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Lozinka nije prosleđena u odgovarajućem formatu!"));
    }
  }

  @Nested
  class RefreshTests{
    //SUCCES 200
    @Test
    void testRefreshTokenSuccess() throws Exception{
      //given
      when(authService.refreshUsersToken(any(RefreshRequest.class))).thenReturn(new AuthResponse("Test","Test"));
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RefreshRequest(null))))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.accesToken").value("Test"))
          .andExpect(jsonPath("$.refreshToken").value("Test"));
    }
    //SERVER ERROR 500
    @Test
    void testRefreshTokenServerError() throws Exception {
      //given
      when(authService.refreshUsersToken(any(RefreshRequest.class))).thenThrow(new Exception("asd"));
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RefreshRequest(null))))
          .andExpect(status().is(500))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Greška na serveru!"));
    }
    //Bad request 400
    @Test
    void testRefreshTokenInvalidToken() throws Exception {
      //given
      when(authService.refreshUsersToken(any(RefreshRequest.class))).thenThrow(new IllegalArgumentException("{\"message\" : [\"Nevažeći token!\"]}"));
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RefreshRequest(null))))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Nevažeći token!"));
    }
    @Test
    void testRefreshTokenExpiredJwt() throws Exception {
      //given
      when(authService.refreshUsersToken(any(RefreshRequest.class))).thenThrow(new IllegalArgumentException("{\"message\" : [\"Prosleđeni token je istekao!\"]}"));
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RefreshRequest(null))))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Prosleđeni token je istekao!"));
    }
    @Test
    void testRefreshTokenUnsupportedJwt() throws Exception {
      //given
      when(authService.refreshUsersToken(any(RefreshRequest.class))).thenThrow(new IllegalArgumentException("{\"message\" : [\"Token koristi nepodržan algoritam ili format!\"]}"));
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RefreshRequest(null))))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Token koristi nepodržan algoritam ili format!"));
    }
    @Test
    void testRefreshTokenMalformedJwt() throws Exception {
      //given
      when(authService.refreshUsersToken(any(RefreshRequest.class))).thenThrow(new IllegalArgumentException("{\"message\" : [\"Token je loše formatiran ili korumpiran!\"]}"));
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RefreshRequest(null))))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Token je loše formatiran ili korumpiran!"));
    }
    @Test
    void testRefreshTokenWrongSignatureJwt() throws Exception {
      //given
      when(authService.refreshUsersToken(any(RefreshRequest.class))).thenThrow(new IllegalArgumentException("{\"message\" : [\"Potpis na tokenu se ne poklapa sa potpisom servera!\"]}"));
      //when then
      mockMvc.perform(post("/api/auth/refresh")
              .accept(MediaType.APPLICATION_JSON)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(new RefreshRequest(null))))
          .andExpect(status().is(400))
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(containsString("Potpis na tokenu se ne poklapa sa potpisom servera!"));
    }
    @Test
    void testRefreshTokenNoJwt() throws Exception {
      //given
      when(authService.refreshUsersToken(any(RefreshRequest.class))).thenThrow(new IllegalArgumentException("{\"message\" : [\"Refresh token nije prosleđen!\"]}"));
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