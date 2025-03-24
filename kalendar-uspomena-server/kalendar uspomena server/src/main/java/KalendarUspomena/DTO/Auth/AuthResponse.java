package KalendarUspomena.DTO.Auth;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
  private String accesToken;
  private String refreshToken;

  @Override
  public String toString() {
    return (new StringBuilder()).append("{\"accesToken\": \"")
        .append(accesToken)
        .append("\",\"refreshToken\": \"")
        .append(refreshToken)
        .append("\"}").toString();
  }
}