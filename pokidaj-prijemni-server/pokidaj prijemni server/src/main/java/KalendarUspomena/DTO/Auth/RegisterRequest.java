package KalendarUspomena.DTO.Auth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class RegisterRequest {
  private String username;
  private String password;
  private String email;
  private String ime;
  private String prezime;
}