package KalendarUspomena.Controller;

import KalendarUspomena.DTO.Auth.AuthResponse;
import KalendarUspomena.DTO.Exceptions.ErrorMessage;
import KalendarUspomena.DTO.Auth.LoginRequest;
import KalendarUspomena.DTO.Auth.RefreshRequest;
import KalendarUspomena.DTO.Auth.RegisterRequest;
import KalendarUspomena.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;

  // Login endpoint
  @Operation(summary = "Login user",
      responses = {
          @ApiResponse(responseCode = "200", description = "Successful operation",
              content = @Content(mediaType = "application/json",schema = @Schema(implementation = AuthResponse.class),
                  examples = @ExampleObject(name="Uspešan login"
                      ,value = (
                          "{\"accesToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJUeXBlIjoiQWNjZXNzIiwic3ViIjoiYm9tYm1hdGlqYSIsImlhdCI6MTczNTMwMzk3NSwiZXhwIjoxNzM1MzA0ODc1fQ.n8vwKeEAIUdSkYkQyMo-VcIySBipxcSwof2Lpe45EIQ\", \"refreshToken\": \"EyJhbGciOiJIUzI1NiJ9.eyJUeXBlIjoiUmVmcmVzaCIsInN1YiI6ImJvbWJtYXRpamEiLCJpYXQiOjE3MzUzMDM5NzUsImV4cCI6MTczNTkwODc3NX0.dTKOY_siADW9JMiWNJBPMLi34Ayl1lJO0i8VqxzM4P0\"}")
                  ))),
          @ApiResponse(responseCode ="401", description = "Incorrect Credentials",
           content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorMessage.class),
           examples = {
               @ExampleObject(name = "Pogrešna lozinka", value = "{\"message\": [\"Prosleđena lozinka je neispravna!\"]}")
           })),
          @ApiResponse(responseCode = "400", description = "Bad request",
              content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorMessage.class),
                  examples = {
                      @ExampleObject(name = "Neprosleđena lozinka",value = "{\"message\" : [\"Nije prosleđena lozinka!\"]}"),
                      @ExampleObject(name = "Neprosleđen identifikator",value = "{\"message\" : [\"Nije prosleđen identifikator!\"]}"),
                      @ExampleObject(name = "Neodgovarajući identifikator",value = "{\"message\" : [\"Uneto korisničko ime(ili email) nije validno!\"]}"),
                      @ExampleObject(name = "Neodgovarajuća lozinka",value = "{\"message\" : [\"Lozinka nije prosleđena u odgovarajućem formatu!\"]}")
                  })),

          @ApiResponse(responseCode = "500",
              description = "Internal Server Error",
              content = @Content(mediaType = "application/json",schema = @Schema(implementation = String.class),
                  examples = @ExampleObject(name = "Greška na serveru",value = "{\"message\" : [\"Greška na serveru!\"]}")))
      })

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    try {
      return  ResponseEntity.ok(authService.loginUser(loginRequest));
    }catch (BadCredentialsException e){
      return ResponseEntity.status(401).body(e.getMessage());
    }catch (IllegalArgumentException e){
      return ResponseEntity.status(400).body(e.getMessage());
    }catch (Exception e){
      return ResponseEntity.status(500).body(new ErrorMessage("Greška na serveru!").toString());
    }
  }
  // Register endpoint
  @Operation(summary = "Save a new user.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(mediaType = "application/json",schema = @Schema(implementation = String.class),
      examples = @ExampleObject(name="Uspešna registracija",value = "{\"message\" : [\"Registracija uspešna!\"]}"))),

      @ApiResponse(responseCode = "400", description = "Bad request",
      content = @Content(mediaType = "application/json",schema = @Schema(implementation = String.class),
      examples = {
          @ExampleObject(name = "Postojeće korisničko ime",value = "{\"message\" : [\"Korisničko ime već postoji!\"]}"),
          @ExampleObject(name = "Postojeći email",value = "{\"message\" : [\"Email već postoji!\"]}"),
          @ExampleObject(name = "Neprosleđeno ime korisnika",value = "{\"message\" : [\"Nije prosleđeno ime korisnika!\"]}"),
          @ExampleObject(name = "Neprosleđeno prezime korisnika",value = "{\"message\" : [\"Nije prosleđeno prezime korisnika!\"]}"),
          @ExampleObject(name = "Neprosleđena lozinka",value = "{\"message\" : [\"Nije prosleđena lozinka!\"]}"),
          @ExampleObject(name = "Neprosleđeno korisničko ime",value = "{\"message\" : [\"Nije prosleđeno korisničko ime!\"]}"),
          @ExampleObject(name = "Neprosleđen email",value = "{\"message\" : [\"Nije prosleđen email!\"]}"),
          @ExampleObject(name = "Neprosleđeni email i korisničko ime",value = "{\"message\" : [\"Nije prosleđen email!\",\"Nije prosleđeno korisničko ime!\"]}"),
          @ExampleObject(name = "Neodgovarajuća lozinka",value = "{\"message\" : [\"Lozinka nije prosleđena u odgovarajućem formatu!\"]}"),
          @ExampleObject(name = "Neodgovarajuće ime",value = "{\"message\" : [\"Ime nije prosleđeno u odgovarajućem foramtu!\"]}"),
          @ExampleObject(name = "Neodgovarajuće prezime",value = "{\"message\" : [\"Prezime nije prosleđeno u odgovarajućem foramtu!\"]}"),
          @ExampleObject(name = "Neodgovarajuće korisničko ime",value = "{\"message\" : [\"Korisničko ime nije prosleđeno u odgovarajućem foramtu!\"]}"),
          @ExampleObject(name = "Neodgovarajući email",value = "{\"message\" : [\"Email nije prosleđen u odgovarajućem foramtu!\"]}"),
      })),

      @ApiResponse(responseCode = "500",
          description = "Internal Server Error",
          content = @Content(mediaType = "application/json",schema = @Schema(implementation = String.class),
          examples = @ExampleObject(name = "Greška na serveru",value = "{\"message\" : \"Greška na serveru!\"}")))
  })
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
      try {
        authService.registerUser(registerRequest);
      }catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
      }catch (Exception e){
        return ResponseEntity.status(500).body(new ErrorMessage("Greška na serveru!").toString());
      }
    return ResponseEntity.ok((new ErrorMessage("Registracija uspešna!")).toString());
  }
  // Refresh endpoint
  @Operation(summary = "Refresh users access token",
  responses = {
      @ApiResponse(responseCode = "200", description = "Successful operation",
          content = @Content(mediaType = "application/json",schema = @Schema(implementation = AuthResponse.class),
              examples = @ExampleObject(name="Uspešno osvežavanje tokena"
                  ,value = (
                  "{\"accesToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJUeXBlIjoiQWNjZXNzIiwic3ViIjoiYm9tYm1hdGlqYSIsImlhdCI6MTczNTMwMzk3NSwiZXhwIjoxNzM1MzA0ODc1fQ.n8vwKeEAIUdSkYkQyMo-VcIySBipxcSwof2Lpe45EIQ\", \"refreshToken\": \"EyJhbGciOiJIUzI1NiJ9.eyJUeXBlIjoiUmVmcmVzaCIsInN1YiI6ImJvbWJtYXRpamEiLCJpYXQiOjE3MzUzMDM5NzUsImV4cCI6MTczNTkwODc3NX0.dTKOY_siADW9JMiWNJBPMLi34Ayl1lJO0i8VqxzM4P0\"}")
              ))),
      @ApiResponse(responseCode = "400", description = "Bad request",
          content = @Content(mediaType = "application/json",schema = @Schema(implementation = ErrorMessage.class),
              examples = {
                  @ExampleObject(name = "Nevažeći token",value = "{\"message\" : [\"Nevažeći token!\"]}"),
                  @ExampleObject(name = "Istekli refresh token",value = "{\"message\" : [\"Prosleđeni token je istekao!\"]}"),
                  @ExampleObject(name = "Nepodržan algoritam ili format tokena",value = "{\"message\" : [\"Token koristi nepodržan algoritam ili format!\"]}"),
                  @ExampleObject(name = "Loše formatiran ili korumpiran token",value = "{\"message\" : [\"Token je loše formatiran ili korumpiran!\"]}"),
                  @ExampleObject(name = "Pogrešan potpis na tokenu", value = "{\"message\": [\"Potpis na tokenu se ne poklapa sa potpisom servera!\"]}"),
                  @ExampleObject(name = "Neprosleđen refresh token", value = "{\"message\": [\"Refresh token nije prosleđen!\"]}")
              })),
      @ApiResponse(responseCode = "500",
          description = "Internal Server Error",
          content = @Content(mediaType = "application/json",schema = @Schema(implementation = String.class),
              examples = @ExampleObject(name = "Greška na serveru",value = "{\"message\" : [\"Greška na serveru!\"]}")))
  })
  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(@RequestBody RefreshRequest refreshRequest) {
    try{
      return  ResponseEntity.ok(authService.refreshUsersToken(refreshRequest));
    }catch (IllegalArgumentException e){
      return ResponseEntity.status(400).body(e.getMessage());
    }catch (Exception e){
      return ResponseEntity.status(500).body("Greška na serveru!");
    }
  }
}


